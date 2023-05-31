package net.gigaclub.bannsystem;

import net.gigaclub.bannsystem.Anderes.Data;
import net.gigaclub.bannsystem.Commads.WarnCommand;
import net.gigaclub.bansystemapi.BanSystem;
import net.gigaclub.translation.Translation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public final class Main extends JavaPlugin {
    final public static String PREFIX = "[GC-BSP]: ";
    private static Main plugin;
    private static Translation translation;
    private static Data data;
    private static BanSystem banSystemAPI;

    public static BanSystem getBanSystemAPI() {
        return banSystemAPI;
    }

    public static void setBanSystemAPI(BanSystem banSystemAPI) {
        Main.banSystemAPI = banSystemAPI;
    }

    public static Translation getTranslation() {
        return translation;
    }

    public static void setTranslation(Translation translation) {
        Main.translation = translation;
    }

    public static Main getPlugin() {
        return plugin;
    }

    public static void setPlugin(Main plugin) {
        Main.plugin = plugin;
    }

    public static Data getData() {
        return data;
    }

    public static void setData(Data data) {
        Main.data = data;
    }

    public static void registerTranslations() {
        Main.translation.registerTranslations(List.of(
                new HashMap<String, String>() {{
                    put("translationName", "warn.type.description");
                    put("description", "description");
                }},
                "warn.type.type",
                new HashMap<String, String>() {{
                    put("translationName", "warn.type.bann.time");
                    put("banntime", "0 Jahre, 0 Tage, 0 Stunden, 0 Minuten, 0 Sekunden");
                }},
                new HashMap<String, String>() {{
                    put("translationName", "warn.type.expiration.time");
                    put("expirationtime", "0 Jahre, 0 Tage, 0 Stunden, 0 Minuten, 0 Sekunden");
                }},
                new HashMap<String, String>() {{
                    put("translationName", "warn.type.points");
                    put("typepoints", "69");
                }},
                new HashMap<String, String>() {{
                    put("translationName", "warn.type.name");
                    put("typename", "Hacking");
                }},
                "Player.never.on.the.network",
                "not.player",
                new HashMap<String, String>() {{
                    put("translationName", "warn.gui.name");
                    put("towarnuser", "GigaClub");
                }},
                new HashMap<String, String>() {{
                    put("translationName", "warn.gui.userpoints");
                    put("userwarnpoints", "96");
                }}
        ));
    }

    @Override
    public void onEnable() {
        plugin = this;
        setPlugin(this);

        getCommand("warn").setExecutor(new WarnCommand());


        File file = new File("plugins//" + "Odoo", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        setTranslation(new Translation(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password"),
                getPlugin()
        ));
        translation.setCategory("bannSystem");

        setData(new Data(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password")
        ));

        setBanSystemAPI(new BanSystem(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password")
        ));

        registerTranslations();


    }


/*
                new HashMap<String, String>() {{
                    put("translationName", "auth.command.authtoken");
                    put("authtoken", "d4jJ9d");
                }}
*/

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
