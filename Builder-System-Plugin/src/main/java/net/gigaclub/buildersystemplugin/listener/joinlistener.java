package net.gigaclub.buildersystemplugin.listener;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.events.service.CloudServiceEvent;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.driver.service.*;
import eu.cloudnetservice.modules.bridge.player.CloudPlayer;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import net.gigaclub.buildersystem.BuilderSystem;
import net.gigaclub.buildersystemplugin.Andere.Guis.TeamGui;
import net.gigaclub.buildersystemplugin.Andere.Guis.WorldGui;
import net.gigaclub.buildersystemplugin.Andere.InterfaceAPI.ItemBuilder;
import net.gigaclub.buildersystemplugin.Main;
import net.gigaclub.translation.Translation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static net.gigaclub.buildersystemplugin.Config.Config.getConfig;

public class joinlistener implements Listener {
    private final PlayerManager playerManager = InjectionLayer.boot().instance(ServiceRegistry.class)
            .firstProvider(PlayerManager.class);
    ItemStack GuiOpener = new ItemBuilder(Material.NETHER_STAR).setDisplayName((ChatColor.BLUE + "BuilderGui")).setLore((ChatColor.AQUA + "Open The BuilderGui")).setGui(true).addIdentifier("Gui_Opener").build();
    private int serviceId;
    private Player player;
    private @NotNull BukkitTask taskID;

    @EventListener
    public void handleServiceConnected(CloudServiceEvent event) {
        String playerUUID = player.getUniqueId().toString();
        Translation t = Main.getTranslation();

        ServiceInfoSnapshot serviceInfoSnapshot = event.serviceInfo(); //The serviceInfoSnapshot with all important information from a service

        ServiceLifeCycle serviceLifeCycle = serviceInfoSnapshot.lifeCycle();
        ServiceId serviceId = serviceInfoSnapshot.serviceId();

        if (this.serviceId == serviceId.taskServiceId()) {

            if (serviceLifeCycle == ServiceLifeCycle.RUNNING) {

                if (player != null) {
                    System.out.println(4);
                    List<? extends CloudPlayer> cloudPlayers = this.playerManager.onlinePlayers(this.player.getName());
                    if (!cloudPlayers.isEmpty()) {
                        CloudPlayer entry = cloudPlayers.get(0);
                        PlayerManager playerManager = this.playerManager;
                        int serviceId1 = this.serviceId;
                        Player player2 = player;
                        @NotNull BukkitScheduler scheduler = Bukkit.getServer().getScheduler();


                        taskID = scheduler.runTaskTimer(Main.getPlugin(), new Runnable() {
                            int countdown = 10;

                            public void run() {
                                player2.sendMessage(t.t("BuilderSystem.countdown_begin", player));
                                if (countdown > 0) {
                                    player2.sendMessage(String.valueOf(countdown));
                                } else {
                                    playerManager.playerExecutor(entry.uniqueId()).connect(event.serviceInfo().serviceId().taskName() + "-" + serviceId1);
                                    scheduler.cancelTask(taskID.getTaskId());
                                    return;
                                }
                                countdown--;
                            }
                        }, 0, 20);
                    }
                }
            }
        }
    }

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


}

