package net.gigaclub.buildersystemplugin.Andere.Guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.gigaclub.buildersystem.BuilderSystem;
import net.gigaclub.buildersystemplugin.Andere.InterfaceAPI.ItemBuilder;
import net.gigaclub.buildersystemplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.gigaclub.buildersystemplugin.Andere.Guis.Navigator.Navigate;

public class WorldGui {

    static ItemStack outlineintem = new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").build();


    public static List<GuiItem> projectItemList(Player player) {


        BuilderSystem builderSystem = Main.getBuilderSystem();
        List<GuiItem> guiItems = new ArrayList<>();
        JSONArray userWorlds = builderSystem.getUserWorlds(player.getUniqueId().toString());

        for (int i = 0; i < userWorlds.length(); i++) {

            JSONObject world = userWorlds.getJSONObject(i);

            //   int taskID = world.getInt("task_id");
            //    JSONObject task = builderSystem.getTask(taskID);
            String ownerID = world.getString("owner_id");
            UUID uuid = UUID.fromString(world.getString("owner_id"));
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            String ownerName = offlinePlayer.getName();
            ArrayList<String> worldlore = new ArrayList<>();
            worldlore.add(ChatColor.GRAY + "ID: " + ChatColor.WHITE + world.getInt("world_id"));
            worldlore.add(ChatColor.GRAY + "World Type" + " " + ChatColor.WHITE + world.getString("world_type"));
            worldlore.add(ChatColor.GRAY + "Projeckt Owner: " + ChatColor.WHITE + ownerName);
            //   if (!(task.getString("description").isEmpty())) {
            //       worldlore.add(ChatColor.GRAY + "Description: " + ChatColor.WHITE + task.getString("description"));
            //    }
            JSONArray teams = world.getJSONArray("team_ids");
            if (!(teams.isEmpty())) {
                List<String> stringList = new ArrayList<String>();
                for (int j = 0; j < teams.length(); j++) {
                    JSONObject team = teams.getJSONObject(j);
                    String teamname = team.getString("name");
                    stringList.add(teamname);
                }
                String joinedString = String.join(", ", stringList);
                worldlore.add(ChatColor.GRAY + "Builder Teams: " + ChatColor.WHITE + joinedString);
            }

            JSONArray user = world.getJSONArray("user_ids");
            if (!(user.isEmpty())) {
                List<String> stringList2 = new ArrayList<String>();
                for (int i1 = 0; i1 < user.length(); i1++) {
                    JSONObject uuid2 = user.getJSONObject(i1);
                    String pid = uuid2.getString("mc_uuid");
                    String user_name = Bukkit.getOfflinePlayer(UUID.fromString(pid)).getName();
                    stringList2.add(user_name);
                }
                String joinedString1 = String.join(", ", stringList2);
                worldlore.add(ChatColor.GRAY + "Builder: " + ChatColor.WHITE + joinedString1);
            }
            if (!(uuid.equals(player.getUniqueId()))) {
                ItemStack project = new ItemBuilder(Material.PAPER).setDisplayName(ChatColor.GRAY + "Name: " + ChatColor.WHITE + world.getString("name")).setLore(worldlore).build();
                GuiItem guiItem = new GuiItem(project, event -> {
                    projeckt(player, world.getInt("world_id"), world.getString("name"));
                    event.setCancelled(true);
                });
                guiItems.add(guiItem);
            } else {
                ItemStack project = new ItemBuilder(Material.MAP).setDisplayName(ChatColor.GRAY + "Name: " + ChatColor.WHITE + world.getString("name")).setLore(worldlore).build();
                GuiItem guiItem = new GuiItem(project, event -> {
                    projeckt(player, world.getInt("world_id"), world.getString("name"));
                    event.setCancelled(true);
                });
                guiItems.add(guiItem);

            }

        }

        return guiItems;
    }

    public static void projecktList(Player player) {
        ChestGui taskList = new ChestGui(6, "Project List Page 1");
        StaticPane outline = new StaticPane(0, 0, 9, 1);
        StaticPane outline2 = new StaticPane(0, 1, 1, 4);
        StaticPane outline3 = new StaticPane(8, 1, 1, 4);

        outline.setOnClick(event -> event.setCancelled(true));
        outline2.setOnClick(event -> event.setCancelled(true));
        outline3.setOnClick(event -> event.setCancelled(true));
        outline.fillWith(outlineintem);
        outline2.fillWith(outlineintem);
        outline3.fillWith(outlineintem);
        taskList.addPane(outline);
        taskList.addPane(outline2);
        taskList.addPane(outline3);

        taskList.setOnBottomClick(event -> event.setCancelled(true));

        PaginatedPane taskPages = new PaginatedPane(1, 1, 7, 4);
        taskPages.populateWithGuiItems(projectItemList(player));

        StaticPane navigation = new StaticPane(0, 5, 9, 1);
        navigation.setOnClick(event -> event.setCancelled(true));
        navigation.setPriority(Pane.Priority.HIGHEST);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8784).setDisplayName(ChatColor.GRAY + "Back").build(), event -> {
            if (taskPages.getPage() > 0) {
                taskPages.setPage(taskPages.getPage() - 1);
                taskList.setTitle("Task List Page " + (taskPages.getPage() + 1));
                taskList.update();
            } else event.setCancelled(true);
        }), 1, 0);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8782).setDisplayName(ChatColor.GRAY + "Next").build(), event -> {
            if (taskPages.getPage() < taskPages.getPages() - 1) {
                taskPages.setPage(taskPages.getPage() + 1);
                taskList.setTitle("Task List Page " + (taskPages.getPage() + 1));
                taskList.update();
            } else event.setCancelled(true);
        }), 7, 0);


        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD)
                .setHeadDatabase(10298)
                .setDisplayName(ChatColor.DARK_GRAY + "Back to Main Menu")
                .build(), event -> Navigate(player)), 4, 0);

        navigation.fillWith(outlineintem, event -> event.setCancelled(true));

        if (taskPages.getPages() == 1) {
            taskList.setTitle("Project List");
            StaticPane outline4 = new StaticPane(0, 5, 9, 1);
            outline4.fillWith(outlineintem);
            outline4.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(10298).setDisplayName(ChatColor.DARK_GRAY + "Back to Main Menu").build(), event -> Navigate(player)), 4, 0);
            taskList.addPane(outline4);
        } else {
            taskList.addPane(navigation);
        }

        taskList.addPane(taskPages);
        taskList.show(player);
    }

    public static void projeckt(Player player, int projecktID, String projecktName) {

        ChestGui projecktManage = new ChestGui(3, "Manage " + projecktName);
        StaticPane pane = new StaticPane(0, 0, 9, 3);
        pane.fillWith(outlineintem, event -> event.setCancelled(true));

        ItemStack editWorldTyp = new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(24064).setDisplayName("edit WorldTyp").build();
        GuiItem worldTyp = new GuiItem(editWorldTyp, event -> {
            player.sendPlainMessage("edit worldtyp");
            player.sendPlainMessage("warn");
            event.setCancelled(true);
        });
        pane.addItem(worldTyp, 1, 1);


        //  ToDo Add server Info
        //  and join on klick
        ItemStack serverStatus = new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(15962).setDisplayName("Server Info").build();
        GuiItem serverinfo = new GuiItem(serverStatus, event -> {

            player.sendMessage("Server Info");
            event.setCancelled(true);
        });
        pane.addItem(serverinfo, 7, 1);

        ItemStack playermanager = new ItemBuilder(Material.PLAYER_HEAD).setDisplayName("Player Manager").build();
        GuiItem playerms = new GuiItem(playermanager, event -> {
            player.sendMessage("manage Players");
            event.setCancelled(true);
        });
        pane.addItem(playerms, 3, 1);
        ItemStack teamManager = new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9386).setDisplayName("Team Manager").build();
        GuiItem teammanage = new GuiItem(teamManager, event -> {
            player.sendMessage("manage Teams");
            event.setCancelled(true);
        });
        pane.addItem(teammanage, 5, 1);
        projecktManage.addPane(pane);
        projecktManage.show(player);
    }

    public static void editWorldTyp(Player player, int projecktID, String projecktName) {


    }


}
