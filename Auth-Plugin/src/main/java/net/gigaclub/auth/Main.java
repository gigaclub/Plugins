package net.gigaclub.auth;

import net.gigaclub.auth.commands.AuthCommand;
import net.gigaclub.auth.data.Data;
import net.gigaclub.translation.Translation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.LoadOrder;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


@Plugin(name = "Auth", version = "1.20.1.1.0.0")
@ApiVersion(ApiVersion.Target.v1_20)
@Dependency("Translation")
@LoadOrder(PluginLoadOrder.POSTWORLD)
public final class Main extends JavaPlugin {

    private static Main plugin;
    private static Translation translation;
    private static Data data;

    public static Main getPlugin() {
        return plugin;
    }

    public static void setPlugin(Main plugin) {
        Main.plugin = plugin;
    }

    public static Translation getTranslation() {
        return Main.translation;
    }

    public static void setTranslation(Translation translation) {
        Main.translation = translation;
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
                    put("translationName", "auth.command.authtoken");
                    put("authtoken", "d4jJ9d");
                }}
        ));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        setPlugin(this);


        File file = new File("plugins//" + "Odoo", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        setTranslation(new Translation(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password"),
                getPlugin()
        ));
        Main.translation.setCategory("translation");
        setData(new Data(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password")
        ));
        registerCommands();
        registerTranslations();
    }

    public void registerCommands() {
        Objects.requireNonNull(getCommand("auth")).setExecutor(new AuthCommand());
    }

}