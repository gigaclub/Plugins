package net.gigaclub.bannsystem.Commads;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.gigaclub.bannsystem.Anderes.Data;
import net.gigaclub.bannsystem.Anderes.ItemBuilder;
import net.gigaclub.bannsystem.Main;
import net.gigaclub.bansystemapi.BanSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class WarnCommand implements CommandExecutor {
    public static List<GuiItem> taskItemList(Player player, Player wantUser) {
        List<GuiItem> guiItems = new ArrayList<>();
        GuiItem guiItem = new GuiItem(new ItemStack(Material.KNOWLEDGE_BOOK), event -> event.setCancelled(true));
        guiItems.add(guiItem);

        return guiItems;
    }

    //    0     1        2
    // /warn <Player_Name> <Grund_ID>
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        BanSystem banSystem = Main.getBanSystemAPI();

        Data data = Main.getData();

        if (!(sender instanceof Player player)) {
            return false;
        }
        if (args.length == 0) {

            player.sendMessage("Warn Grund");
        } else if (args.length == 1) {
            Player player1 = Bukkit.getPlayerExact(args[0]);
            ChestGui warnUser = new ChestGui(4, "Warn " + player1.getName());
            StaticPane backround = new StaticPane(0, 0, 9, 4, Pane.Priority.LOWEST);

            backround.fillWith(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName(" ").build(), event -> event.setCancelled(true));

            warnUser.addPane(backround);


            ArrayList<String> loreList = new ArrayList<>();
            loreList.add("Warn Points:" + banSystem.getPlayerWarningPoints(args[0]));
            ItemStack wantuserhead = new ItemBuilder(Material.PLAYER_HEAD).setHead(player1.getName()).setDisplayName(player1.getName()).setLore(loreList).build();
            StaticPane panehead = new StaticPane(0, 0, 1, 1, Pane.Priority.HIGHEST);
            panehead.addItem(new GuiItem(wantuserhead, event -> event.setCancelled(true)), 0, 0);
            warnUser.addPane(panehead);
            PaginatedPane warnsPane = new PaginatedPane(1, 1, 7, 2, Pane.Priority.HIGHEST);
            warnsPane.populateWithGuiItems(taskItemList(player, player1));
            warnUser.addPane(warnsPane);

            warnUser.show(player);

        } else if (args.length == 2) {
            String username = args[0];
            try {
                if (data.MCplayerExists(username)) {
                    String[] User = data.getMCPlayerInfo(username);
                    if (data.checkIfPlayerExists(User[0])) {
                        //ADD USER uuid und ip TO BANN LISTs
                        String previousPlayerIpHash = Main.getData().getLastIpHash(User[0]);
                        String playerUUID = User[0];
                        int warnreson = Integer.parseInt(args[2]);


                    } else {
                        player.sendMessage("Spieler war noch nie auf dem netzwerk");

                    }
                }
            } catch (IOException e) {
                player.sendMessage("input is not a player");

            }


        }


        return false;
    }

    private String generateHash(String ipString) {
        try {
            // Create MessageDigest instance with SHA-256 algorithm
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Convert IP string to byte array
            byte[] ipBytes = ipString.getBytes();

            // Update the message digest with the IP bytes
            md.update(ipBytes);

            // Get the hash value as a byte array
            byte[] hashBytes = md.digest();

            // Convert the byte array to a hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
