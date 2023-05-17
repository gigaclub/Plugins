package net.gigaclub.base.listener;

import net.gigaclub.base.Base;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OnLeave implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        String playerUUID = player.getUniqueId().toString();
        Base.getData().updateStatus(playerUUID, "offline");
        List<Object> minecraftStatsTypes = Base.getData().getMinecraftStatsTypes();
        List<HashMap<String, String>> data = new ArrayList();
        String server = "test"; // TODO find a way to get a unique server name
        for (Object minecraftStatsType : minecraftStatsTypes) {
            HashMap<String, String> values = new HashMap<>();
            values.put("mc_uuid", playerUUID);
            values.put("server_name", server);
            values.put("stats_name", minecraftStatsType.toString());
            values.put("value", String.valueOf(player.getStatistic(Statistic.valueOf(minecraftStatsType.toString()))));
            data.add(values);
        }
        Base.getData().registerPlayerStats(data);
    }

}
