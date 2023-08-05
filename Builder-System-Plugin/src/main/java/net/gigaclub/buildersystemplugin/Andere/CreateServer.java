package net.gigaclub.buildersystemplugin.Andere;


import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.document.property.DocProperty;
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

import io.leangen.geantyref.TypeFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;


public class CreateServer {

    private int worldId;
    private CloudServiceProvider cloudServiceProvider;
    public ServiceInfoSnapshot serviceInfoSnapshot;
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
                // TODO make node configurable
                .node("Node-1")
                .autoDeleteOnStop(true)
                .staticService(false)
                // TODO make templates configurable
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
                // TODO make group name list configurable
                .groups(List.of("Builder"))
                // TODO make memory configurable
                .maxHeapMemory(1525)
                .environment(ServiceEnvironmentType.MINECRAFT_SERVER)
                .build()
                .createNewService().serviceInfo();
    }

    public void joinServer(Player player) {
        PlayerManager playerManager = InjectionLayer.boot().instance(ServiceRegistry.class).firstProvider(PlayerManager.class);
        Translation t = Main.getTranslation();
        if (this.serviceInfoSnapshot != null) {
            var stringType = TypeFactory.parameterizedClass(String.class);
            DocProperty<String> docProperty = DocProperty.genericProperty("fetchPlayerUUID", stringType);
            Document doc = this.serviceInfoSnapshot.propertyHolder();
            Document.Mutable docMut = doc.mutableCopy();
            docMut.writeProperty(docProperty, player.getUniqueId().toString());
            this.serviceInfoSnapshot.provider().updateProperties(docMut);
            Function<Void, Void> joinPlayer = result -> {
                // if server not started wait...
                t.sendMessage("builder.system.player.send.to.server", player);
                CloudPlayer cloudPlayer = playerManager.firstOnlinePlayer(player.getName());
                playerManager.playerExecutor(cloudPlayer.uniqueId()).connect(this.serviceInfoSnapshot.name());
                return null;
            };
            this.serviceInfoSnapshot.provider().startAsync().thenApply(joinPlayer);
        }
    }

    public boolean serverStarted() {
        return serviceInfoSnapshot != null;
    }

}



