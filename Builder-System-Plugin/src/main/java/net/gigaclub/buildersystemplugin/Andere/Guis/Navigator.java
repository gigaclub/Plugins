package net.gigaclub.buildersystemplugin.Andere.Guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.gigaclub.buildersystem.BuilderSystem;
import net.gigaclub.buildersystemplugin.Andere.InterfaceAPI.ItemBuilder;
import net.gigaclub.buildersystemplugin.Main;
import net.gigaclub.translation.Translation;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Objects;

import static net.gigaclub.buildersystemplugin.Andere.Guis.TaskGui.TaskList;
import static net.gigaclub.buildersystemplugin.Andere.Guis.TeamGui.teams;
import static net.gigaclub.buildersystemplugin.Andere.Guis.WorldGui.projecktList;


public class Navigator implements Listener {


    HeadDatabaseAPI api = new HeadDatabaseAPI();
    Translation t = Main.getTranslation();

    BuilderSystem builderSystem = Main.getBuilderSystem();

    public static void Navigate(Player player) {


        ChestGui navigator = new ChestGui(3, "GcGui");

        navigator.setOnBottomClick(event -> event.setCancelled(true));
        StaticPane outline = new StaticPane(0, 0, 9, 3);
        ItemStack outlineintem = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" ").build();
        outline.setOnClick(event -> event.setCancelled(true));
        outline.fillWith(outlineintem);
        navigator.addPane(outline);


        ItemStack teamGui = new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9386).setDisplayName(ChatColor.RED + "Team").setLore(teamloreList()).build();
        GuiItem teamItem = new GuiItem(teamGui, event -> teams(player));
        outline.addItem(teamItem, 1, 1);


        ItemStack TaskGui = new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(10142).setDisplayName((ChatColor.AQUA) + "Tasks").setLore(taskloreList()).build();
        GuiItem taskItem = new GuiItem(TaskGui, event -> TaskList(player));
        outline.addItem(taskItem, 4, 1);


        ItemStack projectGui = new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(32442).setDisplayName((ChatColor.BLUE + "Project")).setLore(worldloreList()).build();
        GuiItem prjectItem = new GuiItem(projectGui, event -> projecktList(player));
        outline.addItem(prjectItem, 7, 1);


        navigator.show(player);
    }


    public static ArrayList<String> teamloreList() {
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(ChatColor.GOLD + "--------------");
        loreList.add(ChatColor.GOLD + "Open Team Menu");
        loreList.add(ChatColor.GOLD + "--------------");
        return loreList;
    }

    public static ArrayList<String> worldloreList() {
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(ChatColor.GOLD + "-----------------");
        loreList.add(ChatColor.GOLD + "Open Project Menu");
        loreList.add(ChatColor.GOLD + "-----------------");
        return loreList;
    }

    public static ArrayList<String> taskloreList() {
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(ChatColor.GOLD + "--------------");
        loreList.add(ChatColor.GOLD + "Open Task List");
        loreList.add(ChatColor.GOLD + "--------------");
        return loreList;
    }

    @EventHandler
    public static void moveGui(InventoryClickEvent event) {
        int slot = event.getSlot();
        try {
            ItemStack item = event.getClickedInventory().getItem(slot);
        } catch (Exception e) {
            return;
        }
        ItemStack item = event.getClickedInventory().getItem(slot);
        if (!(event.getWhoClicked().getGameMode().equals(GameMode.CREATIVE))) {
            PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
            if (data.has(new NamespacedKey(Main.getPlugin(), "identifie"))) {
                String identifie = data.get(new NamespacedKey(Main.getPlugin(), "identifie"), PersistentDataType.STRING);
                if (Objects.equals(identifie, "Gui_Opener")) {
                    event.setCancelled(true);

                }
            }
        }
    }

    @EventHandler
    public void antiGuiQ(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (item == null) {
            return;
        } else {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return;
            }
        }
        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
        if (data.has(new NamespacedKey(Main.getPlugin(), "identifie"))) {
            String identifie = data.get(new NamespacedKey(Main.getPlugin(), "identifie"), PersistentDataType.STRING);
            if (Objects.equals(identifie, "Gui_Opener")) {
                Navigate(event.getPlayer());
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void handleGuiOpener(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item == null) {
                return;
            } else {
                ItemMeta meta = item.getItemMeta();
                if (meta == null) {
                    return;
                }
            }

            PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
            if (data.has(new NamespacedKey(Main.getPlugin(), "identifie"))) {
                String identifie = data.get(new NamespacedKey(Main.getPlugin(), "identifie"), PersistentDataType.STRING);
                if (Objects.equals(identifie, "Gui_Opener")) {
                    Navigate(event.getPlayer());
                }
            }
        }
    }

}

