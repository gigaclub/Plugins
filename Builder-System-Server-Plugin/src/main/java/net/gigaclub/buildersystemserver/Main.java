package net.gigaclub.buildersystemserver;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.cloudnetservice.driver.document.property.DocProperty;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.provider.CloudServiceProvider;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.cloudnetservice.modules.bridge.player.CloudPlayer;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import io.leangen.geantyref.TypeFactory;
import net.gigaclub.buildersystem.BuilderSystem;
import net.gigaclub.buildersystemserver.listener.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.LoadOrder;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.util.Collection;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Plugin(name = "BuilderSystemServer", version = "1.20.1.1.0.0")
@ApiVersion(ApiVersion.Target.v1_20)
@Dependency("Translation")
@LoadOrder(PluginLoadOrder.POSTWORLD)
public final class Main extends JavaPlugin {

    public static Main plugin;
    public static int worldId;
    public CloudServiceProvider cloudServiceProvider;
    public static ServiceInfoSnapshot serviceInfoSnapshot;
    public static BuilderSystem builderSystem;

    @lombok.Getter
    public World world;

    public void setWorld() {
        JsonObject world = Main.builderSystem.getWorld(Main.worldId);
        String worldType = world.get("world_type").getAsString();
        if (worldType.contains("nether")) {
            for (World netherWorld : Bukkit.getWorlds()) {
                if (netherWorld.getName().contains("nether")) {
                    this.world = netherWorld;
                    break;
                }
            }
        } else if (worldType.contains("end")) {
            for (World endWorld : Bukkit.getWorlds()) {
                if (endWorld.getName().contains("end")) {
                    this.world = endWorld;
                    break;
                }
            }
        } else {
            for (World normalWorld : Bukkit.getWorlds()) {
                if (!normalWorld.getName().contains("end") && !normalWorld.getName().contains("nether")) {
                    this.world = normalWorld;
                    break;
                }
            }
        }
    }

    public static BuilderSystem getBuilderSystem() {
        return Main.builderSystem;
    }

    public static void setBuilderSystem(BuilderSystem builderSystem) {
        Main.builderSystem = builderSystem;
    }

    public static void setPlugin(Main plugin) {
        Main.plugin = plugin;
    }

    public static Main getPlugin() {
        return Main.plugin;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.saveWorld();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        setPlugin(this);
        File file = new File("plugins//" + "Odoo", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        setBuilderSystem(new BuilderSystem(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password")
        ));
        this.cloudServiceProvider = InjectionLayer.boot().instance(CloudServiceProvider.class);
        this.setCurrentServiceInfoSnapshot();
        this.setWorldId();
        this.setWorld();
        this.loadWorld();
        var stringType = TypeFactory.parameterizedClass(String.class);
        DocProperty<String> docProperty = DocProperty.genericProperty("fetchPlayerUUID", stringType);
        PlayerManager playerManager = InjectionLayer.boot().instance(ServiceRegistry.class).firstProvider(PlayerManager.class);
        CloudPlayer cloudPlayer = playerManager.onlinePlayer(UUID.fromString(Main.serviceInfoSnapshot.readProperty(docProperty)));
        if (cloudPlayer != null)
            playerManager.playerExecutor(cloudPlayer.uniqueId()).connect(Main.serviceInfoSnapshot.name());

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                PlayerManager playerManager = InjectionLayer.boot().instance(ServiceRegistry.class).firstProvider(PlayerManager.class);
                JsonObject world = Main.builderSystem.getWorld(Main.worldId);
                CloudPlayer cloudPlayer = playerManager.onlinePlayer(UUID.fromString(world.get("owner_id").getAsString()));
                if (cloudPlayer != null) return;
                for (JsonElement userElement : world.get("user_ids").getAsJsonArray()) {
                    JsonObject user = userElement.getAsJsonObject();
                    cloudPlayer = playerManager.onlinePlayer(UUID.fromString(user.get("mc_uuid").getAsString()));
                    if (cloudPlayer != null) return;
                }
                Main.serviceInfoSnapshot.provider().stop();
            }
        }, 0, 2400);

        this.registerListener();
    }

    public void setCurrentServiceInfoSnapshot() {
        String ip = this.getServer().getIp();
        int port = this.getServer().getPort();
        @UnmodifiableView @NonNls Collection<ServiceInfoSnapshot> serviceInfoSnapshots = this.cloudServiceProvider.runningServices();
        for (ServiceInfoSnapshot sis : serviceInfoSnapshots) {
            if (sis.address().toString().equals(String.format("%s:%d", ip, port))) {
                Main.serviceInfoSnapshot = sis;
                break;
            }
        }
    }

    public void setWorldId() {
        // TODO the delimitter needs to be configurable
        String pattern = "(\\d+)-\\d+";
        Pattern regexPattern = Pattern.compile(pattern);
        String serverName = Main.serviceInfoSnapshot.name();
        Matcher worldIdMatcher = regexPattern.matcher(serverName);
        if (worldIdMatcher.find()) {
            String worldIdString = worldIdMatcher.group(1);
            Main.worldId = Integer.parseInt(worldIdString);
        }
    }

    public void loadWorld() {
        JsonObject builderSystemWorld = Main.builderSystem.getWorld(Main.worldId);
        JsonObject task = Main.builderSystem.getTask(builderSystemWorld.get("task_id").getAsInt());
        // just get one parameter because both should always be the same in the current logic
        int worldSize = task.get("build_width").getAsInt();
        if (worldSize <= 1)
            worldSize = 1;
        WorldBorder border = this.world.getWorldBorder();
        border.setSize(worldSize);
        // TODO center needs to be set by user if the world is not flat or void
        border.setCenter(0.5, 0.5);
    }

    public void saveWorld() {

    }

    private void registerListener() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinListener(), this);
    }

}
