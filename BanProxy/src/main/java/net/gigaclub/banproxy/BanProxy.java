package net.gigaclub.banproxy;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.gigaclub.bansystemapi.BanSystem;
import net.gigaclub.translation.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public final class BanProxy extends Plugin {
    private static Plugin plugin;
    private static Translation translation;
    private static BanSystem banSystemAPI;
    private static TaskScheduler scheduler;
    private static JsonArray UUids;

    private static Map<Integer, String> warntyps;
    private net.md_5.bungee.config.Configuration config;
    private ScheduledTask taskId;

    public static BanSystem getBanSystemAPI() {
        return banSystemAPI;
    }

    public static void setBanSystemAPI(BanSystem banSystemAPI) {
        BanProxy.banSystemAPI = banSystemAPI;
    }

    public static Translation getTranslation() {
        return translation;
    }

    public static void setTranslation(Translation translation) {
        BanProxy.translation = translation;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static void setPlugin(BanProxy plugin) {
        BanProxy.plugin = plugin;
    }

    public static JsonArray getUUids() {
        return UUids;
    }

    public static Map<Integer, String> getWarntyps() {
        return warntyps;
    }

    public static void registerTranslations() {
        BanProxy.translation.registerTranslations(List.of(
                new HashMap<String, String>() {{
                    put("translationName", "bann.disconnect");
                    put("reason", "Hacking");
                    put("date", "100 Jahre");
                }}

        ));
    }

    public static List<ProxiedPlayer> getPlayers() {
        List<ProxiedPlayer> uuids = new ArrayList<>();
        ProxyServer proxy = ProxyServer.getInstance();
        for (ProxiedPlayer player : proxy.getPlayers()) {
            uuids.add(player);
        }
        return uuids;
    }

    @Override
    public void onDisable() {
        scheduler.cancel(taskId);
    }

    public Configuration getConfig() {
        return config;
    }

    @Override
    public void onEnable() {
        plugin = this;
        setPlugin(this);

        // Listener registrieren
        getProxy().getPluginManager().registerListener(this, new PlayerPreLoginListener());

        // Konfigurationsdatei erstellen oder laden
        File odooFolder = new File(getDataFolder(), "Odoo");
        if (!odooFolder.exists())
            odooFolder.mkdirs();

        File configFile = new File(odooFolder, "config.yml");

        if (!configFile.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
                getProxy().getLogger().info("Config file copied successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                getProxy().getLogger().severe("Failed to copy config file: " + e.getMessage());
            }
        }

        // Konfigurationsdatei laden
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTranslation(new Translation(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password"),
                getPlugin()
        ));
        translation.setCategory("bannProxy");

        setBanSystemAPI(new BanSystem(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password")
        ));

        registerTranslations();
        reloadwarns();
        checkBannetConnectetPlayer();

    }

    public void checkBannetConnectetPlayer() {
        scheduler = getProxy().getScheduler();
        taskId = scheduler.schedule(this, () -> {
            BanSystem banSystem = BanProxy.getBanSystemAPI();

            try {
                JsonArray jsonArray = banSystem.getBannedPlayerUUIDs();
            } catch (NullPointerException e) {
                getProxy().getLogger().info("bann List ist Leer");
                return;
            }

            JsonArray jsonArray = banSystem.getBannedPlayerUUIDs();
            List<ProxiedPlayer> players = getPlayers();

            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                String mcUUID = jsonObject.get("mc_uuid").getAsString();

                for (ProxiedPlayer player : players) {

                    if (player.getUniqueId().toString().equals(mcUUID)) {

                        String bann_date = jsonObject.get("ban_expiration_datetime").getAsString();
                        int warnIP = jsonObject.get("current_warning_id").getAsInt();
                        Map<Integer, String> warnTyps = BanProxy.getWarntyps();

                        String reason = warnTyps.get(warnIP);


                        Translation t = BanProxy.getTranslation();
                        Component bann = t.t("bann.disconnect", player.getUniqueId().toString(), Placeholder.parsed("reason", reason), Placeholder.parsed("date", bann_date));
                        String json = GsonComponentSerializer.gson().serialize(bann);
                        BaseComponent[] bungeeComponent = ComponentSerializer.parse(json);
                        player.disconnect(bungeeComponent);
                    }
                }
            }


        }, 0, 1, TimeUnit.MINUTES);
    }


    public void reloadwarns() {
        scheduler = getProxy().getScheduler();
        // Weitere Initialisierung deines Plugins

        // Task alle 5 Minuten starten
        taskId = scheduler.schedule(this, () -> {
            BanSystem banSystem = BanProxy.getBanSystemAPI();
            try {
                banSystem.getWarningTypes();
            } catch (NullPointerException e) {
                getProxy().getLogger().info("warns List ist Leer");
                return;
            }

            Map<Integer, String> hashMap = new HashMap<>();

            // Das JSON-Array durchlaufen und nur die Parameter "name" und "id" extrahieren
            for (JsonElement jsonElement : banSystem.getWarningTypes()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String name = jsonObject.get("name").getAsString();
                int id = jsonObject.get("id").getAsInt();
                hashMap.put(id, name);
            }

            warntyps = hashMap;
        }, 0, 60, TimeUnit.MINUTES);
    }

}


