package net.gigaclub.buildersystemserver.listener;

import com.google.gson.JsonObject;
import net.gigaclub.buildersystemserver.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void joinListener(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.teleport(Main.plugin.world.getSpawnLocation());
        JsonObject builderSystemWorld = Main.builderSystem.getWorld(Main.worldId);
        // TODO check if center is already set
        if (!builderSystemWorld.get("world_type").getAsString().contains("_"))
            Main.getPlugin().translation.sendMessage("builder.system.server.join.message.set.center", player);
    }

}
