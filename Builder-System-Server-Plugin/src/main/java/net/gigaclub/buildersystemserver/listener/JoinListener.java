package net.gigaclub.buildersystemserver.listener;

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
    }

}
