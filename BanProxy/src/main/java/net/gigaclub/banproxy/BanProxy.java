package net.gigaclub.banproxy;

import com.google.gson.JsonArray;
import net.gigaclub.bansystemapi.BanSystem;
import net.gigaclub.translation.Translation;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class BanProxy extends Plugin {
    private static Plugin plugin;
    private static Translation translation;
    private static BanSystem banSystemAPI;
    private static TaskScheduler scheduler;
    private static JsonArray UUids;
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

    public static void registerTranslations() {
        BanProxy.translation.registerTranslations(List.of(

        ));
    }

    @Override
    public void onEnable() {
        plugin = this;
        setPlugin(this);

        reloadBannList();

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


    }

    @Override
    public void onDisable() {
        scheduler.cancel(taskId);
    }

    public Configuration getConfig() {
        return config;
    }

    public void reloadBannList() {
        scheduler = getProxy().getScheduler();
        // Weitere Initialisierung deines Plugins

        // Task alle 5 Minuten starten
        taskId = scheduler.schedule(this, () -> {
            BanSystem banSystem = BanProxy.getBanSystemAPI();
            try {
                UUids = banSystem.getBannedPlayerUUIDs();
            } catch (NullPointerException e) {
                System.out.println("Bann List Leer");
                return;
            }

            UUids = banSystem.getBannedPlayerUUIDs();
            getProxy().getLogger().info("Asynchrone Aufgabe alle 5 Minuten");
        }, 0, 5, TimeUnit.MINUTES);
    }


}


