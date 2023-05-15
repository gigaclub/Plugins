package net.gigaclub.bansystem.bukkit;

import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.util.ModuleHelper;
import eu.cloudnetservice.ext.platforminject.api.PlatformEntrypoint;
import eu.cloudnetservice.ext.platforminject.api.stereotype.PlatformPlugin;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.gigaclub.bansystem.bukkit.Anderes.Data;
import net.gigaclub.translation.Translation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;


@Singleton
@PlatformPlugin(
        platform = "bukkit",
        name = "BanSystem",
        authors = "GigaClub",
        version = "1.19.4.1.0.0"
)
public final class BukkitBanSystemPlugin implements PlatformEntrypoint {


    private static Translation translation;
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;
    private final ModuleHelper moduleHelper;
    private final EventManager eventManager;


    @Inject
    public BukkitBanSystemPlugin(
            @NonNull JavaPlugin plugin,
            @NonNull PluginManager pluginManager,
            @NonNull ModuleHelper moduleHelper,
            @NonNull EventManager eventManager
    ) {
        this.plugin = plugin;
        this.pluginManager = pluginManager;
        this.moduleHelper = moduleHelper;
        this.eventManager = eventManager;
    }
    private static Data data;

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

    @Override
    public void onLoad() {
        System.out.println("TESTBUKKIT");
        File file = new File("plugins//" + "Odoo", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        InjectionLayer.boot().instance(EventManager.class).registerListener(" ");
        setTranslation(new Translation(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password"),
                this.plugin
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
