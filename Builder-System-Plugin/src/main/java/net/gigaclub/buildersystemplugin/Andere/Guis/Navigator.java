package net.gigaclub.buildersystemplugin.Andere.Guis;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.gigaclub.buildersystem.BuilderSystem;
import net.gigaclub.buildersystemplugin.Andere.InterfaceAPI.GuiLayoutBuilder;
import net.gigaclub.buildersystemplugin.Andere.InterfaceAPI.ItemBuilder;
import net.gigaclub.buildersystemplugin.Main;
import net.gigaclub.translation.Translation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;

public class Navigator implements Listener {

    HeadDatabaseAPI api = new HeadDatabaseAPI();
    Translation t = Main.getTranslation();

    BuilderSystem builderSystem = Main.getBuilderSystem();

    TeamGui teamGui;
    TaskGui taskGui;
    WorldGui worldGui;
    JSONArray tasks;

    public Navigator() {
        this.teamGui = new TeamGui();
        this.worldGui = new WorldGui();
        this.taskGui = new TaskGui();
    }

    public void mainGui(Player player) {
        HeadDatabaseAPI api = new HeadDatabaseAPI();
        String playerUUID = player.getUniqueId().toString();
        int size = 9 * 3;
        Inventory inventory = Bukkit.createInventory(null, size, (ChatColor.DARK_AQUA + "Builder System Gui"));
        GuiLayoutBuilder guiLayout = new GuiLayoutBuilder();
        inventory = guiLayout.guiFullBuilder(inventory, size);


        ItemStack TeamGui = new ItemBuilder(api.getItemHead("9386")).setDisplayName((ChatColor.RED + "Team")).setLore(this.teamGui.teamloreList()).setGui(true).addIdentifier("Team_Opener").build();
        ItemStack TaskList = new ItemBuilder(api.getItemHead("10142")).setGui(true).addIdentifier("task_list").setDisplayName((ChatColor.AQUA) + "Task List").setLore(this.taskGui.taskloreList()).build();
        ItemStack ProjectList = new ItemBuilder(api.getItemHead("32442")).setGui(true).addIdentifier("World_Opener").setDisplayName((ChatColor.BLUE + " your Project List")).setLore(this.worldGui.worldloreList()).build();

        inventory.setItem(10, TeamGui);
        inventory.setItem(13, TaskList);
        inventory.setItem(16, ProjectList);
        player.openInventory(inventory);
    }



}
