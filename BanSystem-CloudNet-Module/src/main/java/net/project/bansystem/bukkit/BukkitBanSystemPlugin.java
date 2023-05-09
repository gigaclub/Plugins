package net.project.bansystem.bukkit;

import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.util.ModuleHelper;
import eu.cloudnetservice.ext.platforminject.api.PlatformEntrypoint;
import eu.cloudnetservice.ext.platforminject.api.stereotype.PlatformPlugin;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

@Singleton
@PlatformPlugin(
        platform = "bukkit",
        name = "BanSystem",
        authors = "GigaClub",
        version = "1.0.0"
)
public final class BukkitBanSystemPlugin implements PlatformEntrypoint {

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

    @Override
    public void onLoad() {
        System.out.println("TESTBUKKIT");
    }

    @Override
    public void onDisable() {
        this.moduleHelper.unregisterAll(this.getClass().getClassLoader());
    }

}
