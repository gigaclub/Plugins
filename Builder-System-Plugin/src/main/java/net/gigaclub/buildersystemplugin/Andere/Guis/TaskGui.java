package net.gigaclub.buildersystemplugin.Andere.Guis;


import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.DispenserGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.gigaclub.buildersystem.BuilderSystem;
import net.gigaclub.buildersystemplugin.Andere.InterfaceAPI.ItemBuilder;
import net.gigaclub.buildersystemplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.gigaclub.buildersystemplugin.Andere.Guis.Navigator.Navigate;

public class TaskGui {

    public static JSONArray taskArray;

    static ItemStack outlineintem = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setDisplayName(" ").build();

    public static List<GuiItem> taskItemList(Player player) {

        List<GuiItem> guiItems = new ArrayList<>();


        JSONArray tasks = taskArray;
        for (int i = 0; i < tasks.length(); i++) {
            JSONObject task = tasks.getJSONObject(i);
            JSONArray worlds = task.getJSONArray("world_ids");

            ArrayList<String> loreList = new ArrayList<>();
            loreList.add(ChatColor.GOLD + "--------------");
            loreList.add(ChatColor.GRAY + "Size: " + ChatColor.WHITE + task.getInt("build_width") + " x " + task.getInt("build_length"));

            if (task.getString("description").isEmpty()) {

            } else {
                loreList.add(ChatColor.GRAY + "description: " + task.getString("description"));
            }

            if (worlds.length() > 0) {
                loreList.add(ChatColor.GRAY + "existing Projects " + ChatColor.WHITE + worlds.length());
            }
            loreList.add(ChatColor.GOLD + "--------------");
            if (worlds.length() == 0) {
                ItemStack TaskItem = new ItemBuilder(Material.PAPER).setDisplayName(ChatColor.GRAY + "ID: " + ChatColor.WHITE + task.getInt("id") + ChatColor.GRAY + " Name: " + ChatColor.WHITE + task.getString("name")).setLore(loreList).addID(task.getInt("id")).build();
                GuiItem guiItem = new GuiItem(TaskItem, event -> {
                    if (player.hasPermission("gigaclub_builder_system.create_world") || player.hasPermission("gigaclub_team.create_world_as_team")) {
                        taskSelect(player, task.getInt("id"));
                    } else player.sendMessage("No Permission");
                    event.setCancelled(true);
                });
                guiItems.add(guiItem);
            } else {
                ItemStack TaskItem = new ItemBuilder(Material.MAP).setDisplayName(ChatColor.GRAY + "ID: " + ChatColor.WHITE + task.getInt("id") + ChatColor.GRAY + " Name: " + ChatColor.WHITE + task.getString("name")).setLore(loreList).addID(task.getInt("id")).build();
                GuiItem guiItem = new GuiItem(TaskItem, event -> {
                    if (player.hasPermission("gigaclub_builder_system.create_world") || player.hasPermission("gigaclub_team.create_world_as_team")) {
                        taskSelect(player, task.getInt("id"));
                    } else player.sendMessage("No Permission");
                    event.setCancelled(true);
                });
                guiItems.add(guiItem);
            }


        }

        return guiItems;
    }

    public static void TaskList(Player player) {
        ChestGui taskList = new ChestGui(6, "Task List Page 1");
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
        taskPages.populateWithGuiItems(taskItemList(player));

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
        navigation.fillWith(outlineintem);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD)
                .setHeadDatabase(10298)
                .setDisplayName(ChatColor.DARK_GRAY + "Back to Main Menu")
                .build(), event -> Navigate(player)), 4, 0);

        if (taskPages.getPages() == 1) {
            taskList.setTitle("Task List");
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


    public static void taskSelect(Player player, int ID) {
        BuilderSystem builderSystem = Main.getBuilderSystem();


        DispenserGui taskSelect = new DispenserGui("select");
        taskSelect.setOnBottomClick(event -> event.setCancelled(true));
        StaticPane panel = new StaticPane(0, 0, 3, 3);
        panel.fillWith(outlineintem);
        panel.setOnClick(event -> event.setCancelled(true));
        panel.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9386).setDisplayName((ChatColor.RED + "Create Projeckt as Team")).build(), event -> {
            if (player.hasPermission("gigaclub_team.create_world_as_team")) {
                teamSelect(player, ID);
            } else player.sendMessage("No Permission");
        }), 0, 1);
        panel.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8958).setDisplayName((ChatColor.BLUE + "Create Projeckt as Player")).build(), event -> {
            if (player.hasPermission("gigaclub_builder_system.create_world")) {
                worldType(player, ID, 0);
            } else player.sendMessage("No Permission");
        }), 2, 1);

        panel.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD)
                .setHeadDatabase(10299)
                .setDisplayName(ChatColor.DARK_GRAY + "Back to task List")
                .build(), event -> TaskList(player)), 1, 2);
        taskSelect.getContentsComponent().addPane(panel);

        taskSelect.show(player);
    }

    public static void teamSelect(Player player, int ID) {
        BuilderSystem builderSystem = Main.getBuilderSystem();

        List<GuiItem> guiItems = new ArrayList<>();
        ChestGui teamSelect = new ChestGui(2, "Team Select");
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 2);
        pane.setOnClick(event -> event.setCancelled(true));
        teamSelect.setOnBottomClick(event -> event.setCancelled(true));
        JsonArray teamsUser = builderSystem.getTeamsByMember(player.getUniqueId().toString());

        for (JsonElement jsonElement : teamsUser) {

            JsonObject team = jsonElement.getAsJsonObject();
            String owner = builderSystem.getTeam(team.get("id").getAsInt()).get("owner_id").getAsString();
            String ownerName = Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName();
            guiItems.add(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHead(ownerName).setDisplayName(team.get("name").getAsString()).build(), event -> worldType(player, ID, team.get("id").getAsInt())));

        }
        pane.populateWithGuiItems(guiItems);
        teamSelect.addPane(pane);
        teamSelect.show(player);

    }


    public static void worldType(Player player, int ID, int teamID) {
        final String[] worldTyp = {"normal_flat"};
        final ChestGui[] worldTypes = {new ChestGui(1, "World Typs Select")};
        StaticPane selector = new StaticPane(9, 1);
        selector.addItem(new GuiItem(new ItemBuilder(Material.GRASS_BLOCK).setDisplayName("Normal").build(), event -> createWorld(teamID, ID, event, "normal")), 0, 0);
        selector.addItem(new GuiItem(new ItemBuilder(Material.GRASS_BLOCK).setDisplayName("Normal Flat").build(), event -> createWorld(teamID, ID, event, "normal_flat")), 1, 0);
        selector.addItem(new GuiItem(new ItemBuilder(Material.GRASS_BLOCK).setDisplayName("Normal Void").build(), event -> createWorld(teamID, ID, event, "normal_void")), 2, 0);

        selector.addItem(new GuiItem(new ItemBuilder(Material.NETHERRACK).setDisplayName("Nether").build(), event -> createWorld(teamID, ID, event, "nether")), 3, 0);
        selector.addItem(new GuiItem(new ItemBuilder(Material.NETHERRACK).setDisplayName("Nether Flat").build(), event -> createWorld(teamID, ID, event, "nether_flat")), 4, 0);
        selector.addItem(new GuiItem(new ItemBuilder(Material.NETHERRACK).setDisplayName("Nether Void").build(), event -> createWorld(teamID, ID, event, "nether_void")), 5, 0);

        selector.addItem(new GuiItem(new ItemBuilder(Material.END_STONE).setDisplayName("End").build(), event -> createWorld(teamID, ID, event, "end")), 6, 0);
        selector.addItem(new GuiItem(new ItemBuilder(Material.END_STONE).setDisplayName("End Flat").build(), event -> createWorld(teamID, ID, event, "end_flat")), 7, 0);
        selector.addItem(new GuiItem(new ItemBuilder(Material.END_STONE).setDisplayName("End Void").build(), event -> createWorld(teamID, ID, event, "end_void")), 8, 0);

        worldTypes[0].addPane(selector);
        worldTypes[0].show(player);

    }

    public static void createWorld(int teamID, int ID, InventoryClickEvent event, String worldType) {
        BuilderSystem builderSystem = Main.getBuilderSystem();
        JSONObject task = builderSystem.getTask(ID);


        if (teamID != 0) {
            JsonObject team = builderSystem.getTeam(teamID);
            int res = builderSystem.createWorldAsTeam(event.getWhoClicked().getUniqueId().toString(), teamID, ID, task.getString("name") + "_" + team.get("name").getAsString(), worldType);
            event.getWhoClicked().sendMessage(String.valueOf(res));
        } else
            builderSystem.createWorldAsUser(event.getWhoClicked().getUniqueId().toString(), ID, task.getString("name") + "_" + event.getWhoClicked().getName(), worldType);


        event.getWhoClicked().closeInventory();
    }


}



