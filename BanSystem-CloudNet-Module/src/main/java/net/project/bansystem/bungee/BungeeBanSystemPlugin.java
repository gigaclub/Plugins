package net.project.bansystem.bungee;

import eu.cloudnetservice.driver.util.ModuleHelper;
import eu.cloudnetservice.ext.platforminject.api.PlatformEntrypoint;
import eu.cloudnetservice.ext.platforminject.api.stereotype.PlatformPlugin;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import org.checkerframework.checker.nullness.qual.NonNull;

@Singleton
@PlatformPlugin(
        platform = "bungeecord",
        name = "BanSystem",
        authors = "GigaClub",
        version = "1.0.0"
)
public class BungeeBanSystemPlugin implements PlatformEntrypoint {

    private final Plugin plugin;
    private final ModuleHelper moduleHelper;
    private final PluginManager pluginManager;

    @Inject
    public BungeeBanSystemPlugin(
            @NonNull Plugin plugin,
            @NonNull ModuleHelper moduleHelper,
            @NonNull PluginManager pluginManager
    ) {
        this.plugin = plugin;
        this.moduleHelper = moduleHelper;
        this.pluginManager = pluginManager;
    }

    @Override
    public void onLoad() {
        System.out.println("TESTBUNGEE");
    }

    @Override
    public void onDisable() {
        this.moduleHelper.unregisterAll(this.getClass().getClassLoader());
    }

}
