package net.gigaclub.buildersystemplugin.Andere;


import eu.cloudnetservice.driver.service.ServiceConfiguration;
import eu.cloudnetservice.driver.service.ServiceEnvironmentType;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.cloudnetservice.driver.service.ServiceTemplate;
import net.gigaclub.buildersystem.BuilderSystem;
import net.gigaclub.buildersystemplugin.Main;
import net.gigaclub.translation.Translation;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class CreateServer {

    public static void startServer(int worldId, Player player) {
        BuilderSystem builderSystem = Main.getBuilderSystem();
        Translation t = Main.getTranslation();
        JSONObject world = builderSystem.getWorld(worldId);
        String worldTyp = world.getString("world_type");
        //  world_name, task_name, task_id, worlds_typ, word_id, team_name
        t.sendMessage("bsc.Command.CreateServer", player);
        ServiceInfoSnapshot serviceInfoSnapshot = ServiceConfiguration.builder()
                .taskName(String.valueOf(worldId))
                .node("Node-1")
                .autoDeleteOnStop(true)
                .staticService(false)
                .templates(Arrays.asList(ServiceTemplate.builder().prefix("Builder").name(worldTyp).storage("local").build(), ServiceTemplate.builder().prefix("Builder").name("Plugins").storage("local").build()))
                .groups(List.of("Builder"))
                .maxHeapMemory(1525)
                .environment(ServiceEnvironmentType.MINECRAFT_SERVER)
                .build()
                .createNewService().serviceInfo();
        if (serviceInfoSnapshot != null)
            serviceInfoSnapshot.provider().start();
    }

    public static void stopServer(int worldId) {
        ServiceInfoSnapshot serviceInfoSnapshot = ServiceConfiguration.builder().taskName(String.valueOf(worldId)).build().createNewService().serviceInfo();
        if (serviceInfoSnapshot != null) {
            serviceInfoSnapshot.provider().stop();
        }
    }

}



