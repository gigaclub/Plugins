package net.gigaclub.buildersystemplugin.Andere.Guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.gigaclub.buildersystem.BuilderSystem;
import net.gigaclub.buildersystemplugin.Andere.InterfaceAPI.ItemBuilder;
import net.gigaclub.buildersystemplugin.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static net.gigaclub.buildersystemplugin.Andere.Guis.Navigator.Navigate;

public class WorldGui {


    static ItemStack outlineintem = new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").build();

    public static List<GuiItem> projectItemList(Player player) {
        BuilderSystem builderSystem = Main.getBuilderSystem();
        List<GuiItem> guiItems = new ArrayList<>();
        JSONArray userWorlds = builderSystem.getUserWorlds(player.getUniqueId().toString());

        for (int i = 0; i < userWorlds.length(); i++) {
            JSONObject world = userWorlds.getJSONObject(i);
            int taskID = world.getInt("task_id");
            JSONObject task = builderSystem.getTask(taskID);

            ArrayList<String> worldlore = new ArrayList<>();
            worldlore.add(ChatColor.GRAY + "ID: " + ChatColor.WHITE + world.getInt("world_id"));

            worldlore.add(ChatColor.GRAY + "Project Name:" + " " + ChatColor.WHITE + world.getString("name"));
            worldlore.add(ChatColor.GRAY + "World Type" + " " + ChatColor.WHITE + world.getString("world_type"));
            worldlore.add(ChatColor.WHITE + " ");
            worldlore.add(ChatColor.WHITE + "Teams");
            JSONArray teams = world.getJSONArray("team_ids");

            for (int j = 0; j < teams.length(); j++) {
                JSONObject team = teams.getJSONObject(j);
                String teamname = team.getString("name");
                StringBuilder res = new StringBuilder();
                res.append(ChatColor.GRAY);
                res.append(teamname).append(ChatColor.WHITE + " , " + ChatColor.GRAY);

                res.toString();
                worldlore.add(ChatColor.WHITE + res.toString());


            }
            worldlore.add(ChatColor.WHITE + " ");


            ItemStack project = new ItemBuilder(Material.PAPER).setDisplayName(ChatColor.GRAY + "Name: " + ChatColor.GREEN + world.getString("name")).setLore(worldlore).build();

            GuiItem guiItem = new GuiItem(project);
            guiItems.add(guiItem);

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

        navigation.fillWith(outlineintem);
        taskList.addPane(navigation);
        taskList.addPane(taskPages);
        taskList.show(player);
    }

}
