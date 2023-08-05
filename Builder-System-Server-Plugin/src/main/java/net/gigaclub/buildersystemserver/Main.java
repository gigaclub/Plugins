package net.gigaclub.buildersystemserver;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.LoadOrder;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

@Plugin(name = "Auth", version = "1.20.1.1.0.0")
@ApiVersion(ApiVersion.Target.v1_20)
@Dependency("Translation")
@LoadOrder(PluginLoadOrder.POSTWORLD)
public final class Main extends JavaPlugin {
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
    }

}
