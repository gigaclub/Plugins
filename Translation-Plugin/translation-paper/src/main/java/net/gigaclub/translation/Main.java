package net.gigaclub.translation;

import net.gigaclub.translation.commands.LanguageCommand;
import net.gigaclub.translation.data.Data;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public final class Main extends JavaPlugin {

    private static Main plugin;
    private static Translation translation;
    private static Data data;

    public static Translation getTranslation() {
        return Main.translation;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getPlugin() {
        return plugin;
    }

    public static void setPlugin(Main plugin) {
        Main.plugin = plugin;
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

    public static void setTranslation(Translation translation) {
        Main.translation = translation;
    }

    public static Data getData() {
        return data;
    }

    public static void setData(Data data) {
        Main.data = data;
    }

    public void registerCommands() {
        LanguageCommand languageCommand = new LanguageCommand();
        Objects.requireNonNull(getCommand("language")).setExecutor(languageCommand);
        Objects.requireNonNull(getCommand("language")).setTabCompleter(languageCommand);
    }

    public static void registerTranslations() {
        Main.translation.registerTranslations(Arrays.asList(
                new HashMap<String, String>() {{
                    put("translationName", "translation.command.language.set");
                    put("language", "en_US");
                }},
                new HashMap<String, String>() {{
                    put("translationName", "translation.command.language.does.not.exist");
                    put("language", "wronglanguage");
                }},
                "translation.command.language.no.language.parameter",
                new HashMap<String, String>() {{
                    put("translationName", "translation.command.language.list");
                    put("languages", "en_US, de_DE");
                }},
                new HashMap<String, String>() {{
                    put("translationName", "translation.command.language.incorrect.parameter");
                    put("wrongparameter", "bla");
                }},
                "translation.command.language.no.parameters"
        ));
    }

}
