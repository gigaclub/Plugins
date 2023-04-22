package net.gigaclub.buildersystemplugin.Andere.Guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.HopperGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.gigaclub.buildersystem.BuilderSystem;
import net.gigaclub.buildersystemplugin.Andere.InterfaceAPI.ItemBuilder;
import net.gigaclub.buildersystemplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class TeamGui implements Listener {

    static ItemStack outlineintem = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName(" ").build();

    public static void teams(Player player) {
        ChestGui teams = new ChestGui(3, "Team");
        StaticPane pane = new StaticPane(0, 0, 9, 3);
        pane.fillWith(outlineintem);
        pane.setOnClick(event -> event.setCancelled(true));
        pane.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9943).setDisplayName("Create Team").setGlow(true).build(), event -> {
            player.sendMessage("create team");
            event.setCancelled(true);
        }), 2, 1);
        pane.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9386).setDisplayName("Your Teams").setGlow(true).build(), event -> {
            TeamList(player);
            event.setCancelled(true);
        }), 6, 1);
        teams.addPane(pane);
        teams.show(player);
    }

    public static List<String> jsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            String value = jsonArray.getString(i);
            list.add(value);
        }
        return list;
    }

    public static List<GuiItem> teamItemList(Player player) {
        BuilderSystem builderSystem = Main.getBuilderSystem();
        List<GuiItem> guiItems = new ArrayList<>();
        JSONArray teams = builderSystem.getTeamsByMember(player.getUniqueId().toString());

        for (int i = 0; i < teams.length(); i++) {
            JSONObject team = teams.getJSONObject(i);

            int ID = team.getInt("id");
            String name = team.getString("name");
            String description = team.getString("description");
            String owner = String.valueOf(Bukkit.getOfflinePlayer(UUID.fromString(team.getString("owner_id"))).getName());

            JSONArray players = team.getJSONArray("user_ids");

            List<String> stringList = new ArrayList<String>();
            for (int i1 = 0; i1 < players.length(); i1++) {
                JSONObject uuid = players.getJSONObject(i1);
                String pid = uuid.getString("mc_uuid");
                String user_name = Bukkit.getOfflinePlayer(UUID.fromString(pid)).getName();

                stringList.add(user_name);
            }


            String joinedString = String.join(", ", stringList);

            ArrayList<String> loreList = new ArrayList<>();
            loreList.add(ChatColor.GRAY + "ID: " + ChatColor.WHITE + ID);
            loreList.add(ChatColor.GRAY + "Projeckt Owner: " + ChatColor.WHITE + owner);
            loreList.add(ChatColor.GRAY + "Description: " + ChatColor.WHITE + description);
            loreList.add(ChatColor.GRAY + "Team Member: " + ChatColor.WHITE + joinedString);


            ItemStack project = new ItemBuilder(Material.PAPER).setDisplayName(ChatColor.GRAY + "Name: " + ChatColor.WHITE + name).setLore(loreList).build();

            GuiItem guiItem = new GuiItem(project, event -> {
                event.setCancelled(true);
                TeamMenu(ID, player);
            });
            guiItems.add(guiItem);

        }
        return guiItems;
    }

    public static void TeamList(Player player) {
        ChestGui taskList = new ChestGui(5, "Teams");
        StaticPane outline = new StaticPane(0, 0, 9, 1);
        StaticPane outline2 = new StaticPane(0, 1, 1, 3);
        StaticPane outline3 = new StaticPane(8, 1, 1, 3);
        StaticPane outline4 = new StaticPane(0, 4, 9, 1);

        outline.setOnClick(event -> event.setCancelled(true));
        outline2.setOnClick(event -> event.setCancelled(true));
        outline3.setOnClick(event -> event.setCancelled(true));
        outline4.setOnClick(event -> event.setCancelled(true));
        outline.fillWith(outlineintem);
        outline2.fillWith(outlineintem);
        outline3.fillWith(outlineintem);
        outline4.fillWith(outlineintem);
        taskList.addPane(outline);
        taskList.addPane(outline2);
        taskList.addPane(outline3);
        taskList.addPane(outline4);

        taskList.setOnBottomClick(event -> event.setCancelled(true));

        PaginatedPane taskPages = new PaginatedPane(1, 1, 7, 3);
        taskPages.populateWithGuiItems(teamItemList(player));

        taskList.addPane(taskPages);
        taskList.show(player);
    }

    public static void TeamMenu(int TeamID, Player player) {
        HopperGui teamMenu = new HopperGui("         Team Manager");
        StaticPane pane = new StaticPane(0, 0, 5, 1);
        pane.setOnClick(event -> event.setCancelled(true));
        pane.fillWith(outlineintem);
        pane.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setDisplayName("Player Manager").build(), event -> {
            player.sendMessage("open Player Manager");
            event.setCancelled(true);
        }), 1, 0);
        pane.addItem(new GuiItem(new ItemBuilder(Material.BOOK).setDisplayName("Edit Team").build(), event -> {
            player.sendMessage("open Team Edit");
            event.setCancelled(true);

        }), 3, 0);
        teamMenu.getSlotsComponent().addPane(pane);
        teamMenu.show(player);
    }

    public static void TeamEdit(int TeamID, Player player) {
        HopperGui teamEdit = new HopperGui("Team Edit");
        StaticPane pane = new StaticPane(0, 0, 5, 1);


        teamEdit.getSlotsComponent().addPane(pane);
        teamEdit.show(player);
    }

}
