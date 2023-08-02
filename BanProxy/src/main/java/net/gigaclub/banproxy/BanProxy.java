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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;


public final class BanProxy extends Plugin {
    private static Plugin plugin;
    private static Translation translation;
    private static BanSystem banSystemAPI;
    private static TaskScheduler scheduler;
    private static JsonArray UUids;

    private static Map<Integer, String> warntyps;
    private static final Set<String> bannedUUIDsCache = new HashSet<>();
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
    private Configuration config;

    public static void registerTranslations() {
        BanProxy.translation.registerTranslations(List.of(
                new HashMap<String, String>() {{
                    put("translationName", "bann.disconnect");
                    put("reason", "Hacking");
                    put("jahr", "1");
                    put("monat", "1");
                    put("tag", "1");
                    put("stunde", "1");
                    put("minute", "1");
                    put("sekunde", "1");
                }}

        ));
    }

    @Override
    public void onDisable() {
        scheduler.cancel(taskId);
    }

    public Configuration getConfig() {
        return config;
    }

    public static List<ProxiedPlayer> getPlayers() {
        return new ArrayList<>(ProxyServer.getInstance().getPlayers());
    }

    public static Set<String> getBannedUUIDsCache() {
        return bannedUUIDsCache;
    }

    public void reloadwarns() {
        scheduler = getProxy().getScheduler();
        taskId = scheduler.schedule(this, () -> {
            BanSystem banSystem = BanProxy.getBanSystemAPI();
            try {
                banSystem.getWarningTypes();
            } catch (NullPointerException e) {
                getProxy().getLogger().info("warns List ist Leer");
                return;
            }

            Map<Integer, String> hashMap = new HashMap<>();

            for (JsonElement jsonElement : banSystem.getWarningTypes()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String name = jsonObject.get("name").getAsString();
                int id = jsonObject.get("id").getAsInt();
                hashMap.put(id, name);
            }

            warntyps = hashMap;
        }, 0, 60, TimeUnit.MINUTES);
    }

    public static List<Integer> TimeDifferenceExample(String date) {
        List<Integer> set = new ArrayList<>();
        // Define the target date and time
        LocalDateTime targetDateTime = LocalDateTime.parse("2137-07-04 10:49:32", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Calculate the time difference
        Duration duration = Duration.between(currentDateTime, targetDateTime);

        // Extract the individual components
        long years = duration.toDays() / 365;
        set.add((int) years);
        long months = (duration.toDays() % 365) / 30;
        set.add((int) months);
        long days = (duration.toDays() % 365) % 30;
        set.add((int) days);
        long hours = duration.toHours() % 24;
        set.add((int) hours);
        long minutes = duration.toMinutes() % 60;
        set.add((int) minutes);
        long seconds = duration.getSeconds() % 60;
        set.add((int) seconds);


        return set;
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
        bannedUUIDsCache();
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

                players.stream()
                        .filter(player -> player.getUniqueId().toString().equals(mcUUID))
                        .findFirst()
                        .ifPresent(player -> {
                            String bann_date = jsonObject.get("ban_expiration_datetime").getAsString();
                            List<Integer> bannTime = TimeDifferenceExample(bann_date);
                            int Jahr = bannTime.get(0);
                            int Monate = bannTime.get(1);
                            int Tage = bannTime.get(2);
                            int Stunden = bannTime.get(3);
                            int Minuten = bannTime.get(4);
                            int Sekunden = bannTime.get(5);

                            int warnIP = jsonObject.get("current_warning_id").getAsInt();
                            Map<Integer, String> warnTyps = BanProxy.getWarntyps();
                            String reason = warnTyps.get(warnIP);

                            Translation t = BanProxy.getTranslation();
                            Component bann = t.t("bann.disconnect", player.getUniqueId().toString(), Placeholder.parsed("reason", reason), Placeholder.parsed("jahr", String.valueOf(Jahr)), Placeholder.parsed("monat", String.valueOf(Monate)), Placeholder.parsed("tag", String.valueOf(Tage)), Placeholder.parsed("stunde", String.valueOf(Stunden)), Placeholder.parsed("minute", String.valueOf(Minuten)), Placeholder.parsed("sekunde", String.valueOf(Sekunden)));
                            String json = GsonComponentSerializer.gson().serialize(bann);
                            BaseComponent[] bungeeComponent = ComponentSerializer.parse(json);
                            player.disconnect(bungeeComponent);
                        });
            }

        }, 0, 1, TimeUnit.MINUTES);
    }

    public void bannedUUIDsCache() {
        scheduler = getProxy().getScheduler();
        BanSystem banSystem = BanProxy.getBanSystemAPI();
        taskId = scheduler.schedule(this, () -> {
            JsonArray bannedUUIDs = banSystem.getBannedPlayerUUIDs();
            if (bannedUUIDs != null) {
                for (JsonElement jsonElement : bannedUUIDs) {
                    JsonObject bannedUser = jsonElement.getAsJsonObject();
                    String uuid = bannedUser.get("mc_uuid").getAsString();
                    bannedUUIDsCache.add(uuid);
                }
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

}
