package net.gigaclub.bansystem.bukkit;

import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.util.ModuleHelper;
import eu.cloudnetservice.ext.platforminject.api.PlatformEntrypoint;
import eu.cloudnetservice.ext.platforminject.api.stereotype.Command;
import eu.cloudnetservice.ext.platforminject.api.stereotype.Dependency;
import eu.cloudnetservice.ext.platforminject.api.stereotype.PlatformPlugin;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.gigaclub.bansystem.bukkit.Anderes.Data;
import net.gigaclub.bansystem.bukkit.Commads.WarnCommand;
import net.gigaclub.bansystem.bukkit.config.Config;
import net.gigaclub.translation.Translation;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.util.List;


@Singleton
@PlatformPlugin(
        platform = "bukkit",
        name = "BanSystem",
        authors = "GigaClub",
        version = "1.19.4.1.0.0",
        dependencies = {@Dependency(
                name = "HeadDatabase"
        )},
        commands = {@Command(
                name = "warn",
                permission = "gc.bann.warn",
                aliases = {"warning"}
        )}

)
public final class BukkitBanSystemPlugin implements PlatformEntrypoint {

    final public static String PREFIX = "[GC-Warn]: ";
    private static Translation translation;
    public static JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final ModuleHelper moduleHelper;
    private final EventManager eventManager;
    private static Data data;
    private final WarnCommand warnCommand;


    @Override
    public void onDisable() {
        this.moduleHelper.unregisterAll(this.getClass().getClassLoader());
    }

    public static Translation getTranslation() {
        return translation;
    }

    public static void setTranslation(Translation translation) {
        BukkitBanSystemPlugin.translation = translation;
    }

    @Inject
    public BukkitBanSystemPlugin(
            @NonNull JavaPlugin plugin,
            @NonNull PluginManager pluginManager,
            @NonNull ModuleHelper moduleHelper,
            @NonNull EventManager eventManager,
            @NonNull WarnCommand warnCommand) {
        if (plugin == null) {
            throw new NullPointerException("plugin is marked non-null but is null");
        } else if (warnCommand == null) {
            throw new NullPointerException("signsCommand is marked non-null but is null");
        } else if (moduleHelper == null) {
            throw new NullPointerException("moduleHelper is marked non-null but is null");
        } else if (pluginManager == null) {
            throw new NullPointerException("pluginManager is marked non-null but is null");
        } else {
            BukkitBanSystemPlugin.plugin = plugin;
            this.pluginManager = pluginManager;
            this.moduleHelper = moduleHelper;
            this.eventManager = eventManager;
            this.warnCommand = warnCommand;
        }
    }

    public static Data getData() {
        return data;
    }

    public static void setData(Data data) {
        BukkitBanSystemPlugin.data = data;
    }

    public static void registerTranslations() {
        //allrounder
        BukkitBanSystemPlugin.translation.registerTranslations(List.of());
    }

    private void setConfig() {
        Config.createConfig();


        Config.save();


    }

    @Override
    public void onLoad() {

        setConfig();

        PluginCommand pluginCommand = plugin.getCommand("warn");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(this.warnCommand);
        }


        File file = new File("plugins//" + "Odoo", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        setTranslation(new Translation(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password"),
                plugin
        ));
        translation.setCategory("BannSystem");


        setData(new Data(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password")
        ));


        registerTranslations();

    }

}
