package net.gigaclub.buildersystemserver;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.LoadOrder;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.UnmodifiableView;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.provider.CloudServiceProvider;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;

@Plugin(name = "BuilderSystemServer", version = "1.20.1.1.0.0")
@ApiVersion(ApiVersion.Target.v1_20)
@Dependency("Translation")
@LoadOrder(PluginLoadOrder.POSTWORLD)
public final class Main extends JavaPlugin {

    public static Main plugin;
    public int worldId;
    public CloudServiceProvider cloudServiceProvider;
    public ServiceInfoSnapshot serviceInfoSnapshot;
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        setPlugin(this);
        this.cloudServiceProvider = InjectionLayer.boot().instance(CloudServiceProvider.class);
        this.setCurrentServiceInfoSnapshot();
        this.setWorldId();
        System.out.println(this.worldId);
    }

    public static void setPlugin(Main plugin) {
        Main.plugin = plugin;
    }

    public static Main getPlugin() {
        return Main.plugin;
    }

    public void setCurrentServiceInfoSnapshot() {
        String ip = this.getServer().getIp();
        int port = this.getServer().getPort();
        @UnmodifiableView @NonNls Collection<ServiceInfoSnapshot> serviceInfoSnapshots = this.cloudServiceProvider.runningServices();
        for (ServiceInfoSnapshot sis : serviceInfoSnapshots) {
            if (sis.address().toString().equals(String.format("%s:%d", ip, port))) {
                this.serviceInfoSnapshot = sis;
                break;
            }
        }
    }

    public void setWorldId() {
        // TODO the delimitter needs to be configurable
        String pattern = "(\\d+)-\\d+";
        Pattern regexPattern = Pattern.compile(pattern);
        String serverName = this.serviceInfoSnapshot.name();
        Matcher worldIdMatcher = regexPattern.matcher(serverName);
        if (worldIdMatcher.find()) {
            String worldIdString = worldIdMatcher.group(1);
            this.worldId = Integer.parseInt(worldIdString);
        }
    }

}
