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
import net.gigaclub.buildersystemplugin.Andere.Data;
import net.gigaclub.buildersystemplugin.Andere.InterfaceAPI.ItemBuilder;
import net.gigaclub.buildersystemplugin.Main;
import net.gigaclub.translation.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
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
        Translation t = Main.getTranslation();
        Data data = Main.getData();

        List<GuiItem> guiItems = new ArrayList<>();


        JSONArray tasks = taskArray;
        for (int i = 0; i < tasks.length(); i++) {
            JSONObject task = tasks.getJSONObject(i);
            JSONArray worlds = task.getJSONArray("world_ids");

            ArrayList<Component> loreList = new ArrayList<>();
            loreList.add(t.t("BuilderSystem.task.task.item.line.separator", player));
            // ChatColor.GRAY + "Size: " + ChatColor.WHITE + task.getInt("build_width") + " x " + task.getInt("build_length")
            loreList.add(t.t("BuilderSystem.task.task.size", player, Placeholder.parsed("buildwidth", String.valueOf(task.getInt("build_width"))), Placeholder.parsed("buildlength", String.valueOf(task.getInt("build_length")))));

            if (task.getString("description").isEmpty()) {

            } else {
                loreList.add(t.t("BuilderSystem.task.task.description", player, Placeholder.parsed("description", task.getString("description"))));
            }

            if (worlds.length() > 0) { // existing Projects  worlds.length()
                loreList.add(t.t("BuilderSystem.task.task.projeckt.count", player, Placeholder.parsed("projecktcount", String.valueOf(worlds.length()))));
            }
            loreList.add(t.t("BuilderSystem.task.task.item.line.separator", player));
            if (worlds.length() == 0) {
                //  ChatColor.GRAY + "ID: " + ChatColor.WHITE + task.getInt("id") + ChatColor.GRAY + " Name: " + ChatColor.WHITE + task.getString("name")
                ItemStack TaskItem = new ItemBuilder(Material.PAPER).setDisplayName(t.t("BuilderSystem.task.task.item.id.and.name", player, Placeholder.parsed("taskid", String.valueOf(task.getInt("taskID"))), Placeholder.parsed("taskname", task.getString("name")))).setLoreComponents(loreList).addID(task.getInt("id")).build();
                GuiItem guiItem = new GuiItem(TaskItem, event -> {
                    if (player.hasPermission("gigaclub_builder_system.create_world") || player.hasPermission("gigaclub_team.create_world_as_team")) {
                        taskSelect(player, task.getInt("id"));
                    } else player.sendMessage(t.t("builder_team.no_permission", player));
                    event.setCancelled(true);
                });
                guiItems.add(guiItem);
            } else {
                ItemStack TaskItem = new ItemBuilder(Material.MAP).setDisplayName(t.t("BuilderSystem.task.task.item.id.and.name", player, Placeholder.parsed("taskid", String.valueOf(task.getInt("taskID"))), Placeholder.parsed("taskname", task.getString("name")))).setLoreComponents(loreList).addID(task.getInt("id")).build();
                GuiItem guiItem = new GuiItem(TaskItem, event -> {
                    if (player.hasPermission("gigaclub_builder_system.create_world") || player.hasPermission("gigaclub_team.create_world_as_team")) {
                        taskSelect(player, task.getInt("id"));
                    } else player.sendMessage(t.t("builder_team.no_permission", player));
                    event.setCancelled(true);
                });
                guiItems.add(guiItem);
            }


        }

        return guiItems;
    }

    public static void TaskList(Player player) {
        Translation t = Main.getTranslation();
        Data data = Main.getData();
        //"Task List Page 1"
        ChestGui taskList = new ChestGui(6, data.setGuiName("BuilderSystem.task.list.gui", player, Placeholder.parsed("page", String.valueOf(1))));
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

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8784).setDisplayName(t.t("BuilderSystem.page.list.back", player)).build(), event -> {
            if (taskPages.getPage() > 0) {
                taskPages.setPage(taskPages.getPage() - 1);
                taskList.setTitle(data.setGuiName("BuilderSystem.task.list.gui", player, Placeholder.parsed("page", String.valueOf(1))));
                taskList.update();
            } else event.setCancelled(true);
        }), 1, 0);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8782).setDisplayName(t.t("BuilderSystem.page.list.next", player)).build(), event -> {
            if (taskPages.getPage() < taskPages.getPages() - 1) {
                taskPages.setPage(taskPages.getPage() + 1);
                taskList.setTitle(data.setGuiName("BuilderSystem.task.list.gui", player, Placeholder.parsed("page", String.valueOf(1))));
                taskList.update();
            } else event.setCancelled(true);
        }), 7, 0);
        navigation.fillWith(outlineintem);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD)
                .setHeadDatabase(10298)
                .setDisplayName(t.t("BuilderSystem.back.to.main", player))
                .build(), event -> Navigate(player)), 4, 0);

        if (taskPages.getPages() == 1) {

            taskList.setTitle(data.setGuiName("BuilderSystem.task.list.one.page.gui", player));
            StaticPane outline4 = new StaticPane(0, 5, 9, 1);
            outline4.fillWith(outlineintem);
            outline4.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(10298).setDisplayName(t.t("BuilderSystem.back.to.main", player)).build(), event -> Navigate(player)), 4, 0);
            taskList.addPane(outline4);
        } else {
            taskList.addPane(navigation);
        }


        taskList.addPane(taskPages);
        taskList.show(player);
    }


    public static void taskSelect(Player player, int ID) {
        Translation t = Main.getTranslation();
        Data data = Main.getData();

        DispenserGui taskSelect = new DispenserGui(data.setGuiName("BuilderSystem.task.select.gui", player));
        taskSelect.setOnBottomClick(event -> event.setCancelled(true));
        StaticPane panel = new StaticPane(0, 0, 3, 3);
        panel.fillWith(outlineintem);
        panel.setOnClick(event -> event.setCancelled(true));
        panel.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9386).setDisplayName(t.t("BuilderSystem.task.select.create.projeckt.as.team", player)).build(), event -> {
            if (player.hasPermission("gigaclub_team.create_world_as_team")) {
                teamSelect(player, ID);
            } else player.sendMessage(t.t("builder_team.no_permission", player));
        }), 0, 1);
        panel.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8958).setDisplayName(t.t("BuilderSystem.task.select.create.projeckt.as.player", player)).build(), event -> {
            if (player.hasPermission("gigaclub_builder_system.create_world")) {
                worldType(player, ID, 0);
            } else player.sendMessage(t.t("builder_team.no_permission", player));
        }), 2, 1);

        panel.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD)
                .setHeadDatabase(10299)
                .setDisplayName(t.t("BuilderSystem.back.to.task.list", player))
                .build(), event -> TaskList(player)), 1, 2);
        taskSelect.getContentsComponent().addPane(panel);

        taskSelect.show(player);
    }

    public static void teamSelect(Player player, int ID) {
        BuilderSystem builderSystem = Main.getBuilderSystem();
        Translation t = Main.getTranslation();
        Data data = Main.getData();

        List<GuiItem> guiItems = new ArrayList<>();
        ChestGui teamSelect = new ChestGui(2, data.setGuiName("BuilderSystem.task.create.projeckt.select.team.gui", player));
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 2);
        pane.setOnClick(event -> event.setCancelled(true));
        teamSelect.setOnBottomClick(event -> event.setCancelled(true));
        JsonArray teamsUser = builderSystem.getTeamsByMember(player.getUniqueId().toString());

        for (JsonElement jsonElement : teamsUser) {

            JsonObject team = jsonElement.getAsJsonObject();
            String owner = builderSystem.getTeam(team.get("id").getAsInt()).get("owner_id").getAsString();
            String ownerName = Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName();
            guiItems.add(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHead(ownerName).setDisplayName(t.t("BuilderSystem.task.create.projeckt.select.team.name", player, Placeholder.parsed("name", team.get("name").getAsString()))).build(), event -> worldType(player, ID, team.get("id").getAsInt())));

        }
        pane.populateWithGuiItems(guiItems);
        teamSelect.addPane(pane);
        teamSelect.show(player);

    }


    public static void worldType(Player player, int ID, int teamID) {
        Translation t = Main.getTranslation();
        Data data = Main.getData();

        final String[] worldTyp = {"normal_flat"};
        final ChestGui[] worldTypes = {new ChestGui(1, data.setGuiName("BuilderSystem.task.create.type.select.gui", player))};
        StaticPane selector = new StaticPane(9, 1);
        selector.addItem(new GuiItem(new ItemBuilder(Material.GRASS_BLOCK).setDisplayName(t.t("BuilderSystem.task.create.type.select.normal", player)).build(), event -> createWorld(teamID, ID, event, "normal")), 0, 0);
        selector.addItem(new GuiItem(new ItemBuilder(Material.GRASS_BLOCK).setDisplayName(t.t("BuilderSystem.task.create.type.select.normal.flat", player)).build(), event -> createWorld(teamID, ID, event, "normal_flat")), 1, 0);
        selector.addItem(new GuiItem(new ItemBuilder(Material.GRASS_BLOCK).setDisplayName(t.t("BuilderSystem.task.create.type.select.normal.void", player)).build(), event -> createWorld(teamID, ID, event, "normal_void")), 2, 0);

        selector.addItem(new GuiItem(new ItemBuilder(Material.NETHERRACK).setDisplayName(t.t("BuilderSystem.task.create.type.select.nether", player)).build(), event -> createWorld(teamID, ID, event, "nether")), 3, 0);
        selector.addItem(new GuiItem(new ItemBuilder(Material.NETHERRACK).setDisplayName(t.t("BuilderSystem.task.create.type.select.nether.flat", player)).build(), event -> createWorld(teamID, ID, event, "nether_flat")), 4, 0);
        selector.addItem(new GuiItem(new ItemBuilder(Material.NETHERRACK).setDisplayName(t.t("BuilderSystem.task.create.type.select.nether.void", player)).build(), event -> createWorld(teamID, ID, event, "nether_void")), 5, 0);

        selector.addItem(new GuiItem(new ItemBuilder(Material.END_STONE).setDisplayName(t.t("BuilderSystem.task.create.type.select.end", player)).build(), event -> createWorld(teamID, ID, event, "end")), 6, 0);
        selector.addItem(new GuiItem(new ItemBuilder(Material.END_STONE).setDisplayName(t.t("BuilderSystem.task.create.type.select.end.flat", player)).build(), event -> createWorld(teamID, ID, event, "end_flat")), 7, 0);
        selector.addItem(new GuiItem(new ItemBuilder(Material.END_STONE).setDisplayName(t.t("BuilderSystem.task.create.type.select.end.void", player)).build(), event -> createWorld(teamID, ID, event, "end_void")), 8, 0);

        worldTypes[0].addPane(selector);
        worldTypes[0].show(player);

    }

    public static void createWorld(int teamID, int ID, InventoryClickEvent event, String worldType) {
        BuilderSystem builderSystem = Main.getBuilderSystem();
        JsonObject task = builderSystem.getTask(ID);
        Player player = (Player) event.getWhoClicked();
        String name = task.get("name").getAsString();


        if (teamID != 0) {
            JsonObject team = builderSystem.getTeam(teamID);
            int res = builderSystem.createWorldAsTeam(player.getUniqueId().toString(), teamID, ID, name + "_" + team.get("name").getAsString(), worldType);
        } else
            builderSystem.createWorldAsUser(player.getUniqueId().toString(), ID, name + "_" + event.getWhoClicked().getName(), worldType);


        player.closeInventory();
    }


}



