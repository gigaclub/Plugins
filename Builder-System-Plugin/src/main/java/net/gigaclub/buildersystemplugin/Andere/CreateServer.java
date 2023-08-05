package net.gigaclub.buildersystemplugin.Andere;


import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.provider.CloudServiceProvider;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.driver.service.ServiceConfiguration;
import eu.cloudnetservice.driver.service.ServiceEnvironmentType;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.cloudnetservice.driver.service.ServiceTemplate;
import eu.cloudnetservice.modules.bridge.player.CloudPlayer;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import lombok.NonNull;
import net.gigaclub.buildersystem.BuilderSystem;
import net.gigaclub.buildersystemplugin.Main;
import net.gigaclub.translation.Translation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.UnmodifiableView;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class CreateServer {

    private int worldId;
    private CloudServiceProvider cloudServiceProvider;
    private ServiceInfoSnapshot serviceInfoSnapshot;
    private String taskName;

    public CreateServer(int worldId) {
        this.worldId = worldId;
        this.taskName = String.valueOf(this.worldId);
        this.cloudServiceProvider = InjectionLayer.boot().instance(CloudServiceProvider.class);
        this.setServiceInfoSnapshot();
    }

    private void setServiceInfoSnapshot() {
        @UnmodifiableView @NonNull Collection<ServiceInfoSnapshot> serviceInfoSnapshots = this.cloudServiceProvider.servicesByTask(this.taskName);
        if (serviceInfoSnapshots.isEmpty()) {
            this.createServiceSnapshot();
        }
        for (ServiceInfoSnapshot sis : serviceInfoSnapshots) {
            this.serviceInfoSnapshot = sis;
            break;
        }
    }

    private void createServiceSnapshot() {
        BuilderSystem builderSystem = Main.getBuilderSystem();
        JSONObject world = builderSystem.getWorld(this.worldId);
        String worldTyp = world.getString("world_type");
        this.serviceInfoSnapshot = ServiceConfiguration.builder()
                .taskName(this.taskName)
                .node("Node-1")
                .autoDeleteOnStop(true)
                .staticService(false)
                .templates(
                    Arrays.asList(
                        ServiceTemplate.builder()
                            .prefix("Builder")
                            .name(worldTyp)
                            .storage("local")
                            .build(), 
                        ServiceTemplate.builder()
                            .prefix("Builder")
                            .name("Plugins")
                            .storage("local")
                            .build()
                    )
                )
                .groups(List.of("Builder"))
                .maxHeapMemory(1525)
                .environment(ServiceEnvironmentType.MINECRAFT_SERVER)
                .build()
                .createNewService().serviceInfo();
    }

    public void joinServer(Player player) {
        PlayerManager playerManager = InjectionLayer.boot().instance(ServiceRegistry.class)
            .firstProvider(PlayerManager.class);
        Translation t = Main.getTranslation();
        //  world_name, task_name, task_id, worlds_typ, word_id, team_name
        t.sendMessage("bsc.Command.CreateServer", player);
        if (this.serviceInfoSnapshot != null) {
            this.serviceInfoSnapshot.provider().start();
            CloudPlayer cloudPlayer = playerManager.firstOnlinePlayer(player.getName());
            playerManager.playerExecutor(cloudPlayer.uniqueId()).connect(this.serviceInfoSnapshot.name());
        }
    }

    public boolean serverStarted() {
        return serviceInfoSnapshot != null;
    }

}



