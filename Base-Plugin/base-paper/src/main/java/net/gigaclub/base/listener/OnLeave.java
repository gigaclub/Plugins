package net.gigaclub.base.listener;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.gigaclub.base.Base;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class OnLeave implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        String playerUUID = player.getUniqueId().toString();
        Base.getData().updateStatus(playerUUID, "offline");
        List<Object> minecraftStatsTypes = Base.getData().getMinecraftStatsTypes();
        JsonArray data = new JsonArray();
        String server = "test"; // TODO find a way to get a unique server name
        for (Object minecraftStatsType : minecraftStatsTypes) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("mc_uuid", playerUUID);
            jsonObject.addProperty("server_name", server);
            jsonObject.addProperty("stats_name", minecraftStatsType.toString());
            jsonObject.addProperty("value", player.getStatistic(Statistic.valueOf(minecraftStatsType.toString())));
            data.add(jsonObject);
        }
        System.out.println(data);
        Base.getData().registerPlayerStats(data);
    }

}
