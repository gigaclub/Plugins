package net.gigaclub.buildersystemplugin.listener;


import com.google.gson.JsonObject;
import net.gigaclub.buildersystemplugin.Andere.Guis.TeamGui;
import net.gigaclub.buildersystemplugin.Andere.Guis.WorldGui;
import net.gigaclub.buildersystemplugin.Andere.InterfaceAPI.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import static net.gigaclub.buildersystemplugin.Config.Config.getConfig;

public class joinlistener implements Listener {
    ItemStack GuiOpener = new ItemBuilder(Material.NETHER_STAR).setDisplayName((ChatColor.BLUE + "BuilderGui")).setLore((ChatColor.AQUA + "Open The BuilderGui")).setGui(true).addIdentifier("Gui_Opener").build();
    private @NotNull BukkitTask taskID;

    @EventHandler
    public void joinListener(PlayerJoinEvent event) {
        FileConfiguration config = getConfig();
        Player player = event.getPlayer();
        if (config.getBoolean("Gui.Remove_items_on_Join")) {
            player.getInventory().clear();
            player.getInventory().clear();
        }
        if (player.hasPermission("gigaclub_builder_system.Gui")) {
            player.getInventory().setItem(0, GuiOpener);
        }
    }

    @EventHandler
    public void leaveListener(PlayerQuitEvent event) {

        JsonObject teamObjeckt = TeamGui.teamObjeckt;
        JSONObject worldUser = WorldGui.projecktUserArray;
        JsonObject teamUser = TeamGui.teamUserArray;
        Player player = event.getPlayer();
        String name = player.getName();

        JSONObject worldObject = WorldGui.worldObject;

        if (!(worldObject.isEmpty()) || !(worldObject == null)) {
            worldObject.remove(name);
        }
        if (!(teamObjeckt.size() == 0 || !(teamObjeckt == null))) {
            teamObjeckt.remove(name);
        }
        if (!(worldUser.isEmpty()) || !(worldUser == null)) {
            worldUser.remove(name);
        }
        if (!(teamUser.size() == 0) || !(teamUser == null)) {
            teamUser.remove(name);
        }

        try {
            worldObject.get(name);
        } catch (JSONException e) {
            System.out.println("remove");
        }

    }

    @EventHandler
    public void DeahtListener(PlayerDeathEvent event) {
        event.getDrops().remove(GuiOpener);
    }

    @EventHandler
    public void RespawnListner(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        player.getInventory().setItem(0, GuiOpener);

    }
}

