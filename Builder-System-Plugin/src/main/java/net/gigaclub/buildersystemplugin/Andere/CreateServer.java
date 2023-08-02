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


    public static void startServer(int world_id, Player player) {
        BuilderSystem builderSystem = Main.getBuilderSystem();
        Translation t = Main.getTranslation();
        String playerUUID = player.getUniqueId().toString();
        JSONObject world = builderSystem.getWorld(world_id);


        String world_name = world.getString("name");
        int task_id = world.getInt("task_id");
        JSONObject task = builderSystem.getTask(task_id);

        String task_name = task.getString("name");

        String worlds_typ = world.getString("world_type");
        //  world_name, task_name, task_id, worlds_typ, word_id, team_name
        player.sendMessage(t.t("bsc.Command.CreateServer", player));
        ServiceInfoSnapshot serviceInfoSnapshot = ServiceConfiguration.builder()
                .taskName(String.valueOf(world_id))
                .node("Node-1")
                .autoDeleteOnStop(true)
                .staticService(false)
                .templates(Arrays.asList(ServiceTemplate.builder().prefix("Builder").name(worlds_typ).storage("local").build(), ServiceTemplate.builder().prefix("Builder").name("Plugins").storage("local").build()))
                .groups(List.of("Builder"))
                .maxHeapMemory(1525)
                .environment(ServiceEnvironmentType.MINECRAFT_SERVER)
                .build()
                .createNewService().serviceInfo();
    }
}



