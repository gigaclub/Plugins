package net.gigaclub.bannsystem.Commads;

import com.github.stefvanschie.inventoryframework.adventuresupport.TextHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.gigaclub.bannsystem.Anderes.Data;
import net.gigaclub.bannsystem.Anderes.ItemBuilder;
import net.gigaclub.bannsystem.Main;
import net.gigaclub.bansystemapi.BanSystem;
import net.gigaclub.translation.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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


    public static List<GuiItem> taskItemList(Player player, Player warnPlayer) {
        Translation t = Main.getTranslation();

        List<GuiItem> guiItems = new ArrayList<>();
        BanSystem banSystem = Main.getBanSystemAPI();

        JsonArray warntyps = banSystem.getWarningTypes();

        for (JsonElement jsonElement : warntyps) {
            JsonObject object = jsonElement.getAsJsonObject();
            String name = object.get("name").getAsString();
            String description = object.get("description").getAsString();
            //  int ID = object.get("id").getAsInt();
            Float bannTime = object.get("ban_time").getAsFloat();
            Float expirationTime = object.get("expiration_time").getAsFloat();
            int points = object.get("points").getAsInt();

            ArrayList<Component> loreList = new ArrayList<>();

            loreList.add(t.t("warn.type.id", player, Placeholder.parsed("warntypeid", "NULL"/*ID*/)));
            //  ChatColor.GRAY+"Description "+ChatColor.WHITE+description
            loreList.add(t.t("warn.type.description", player, Placeholder.parsed("description", description)));
            if (bannTime == 0) {
                // ChatColor.GRAY + "Type: " + ChatColor.WHITE + "Kick"
                loreList.add(t.t("warn.type.type", player));
            } else {
                // ChatColor.GRAY + "Type: " + ChatColor.WHITE + "Bann"
                loreList.add(t.t("warn.type.type", player));
                //  ChatColor.GRAY + "Bann Time: " + ChatColor.WHITE + formatiereZeit(bannTime)
                loreList.add(t.t("warn.type.bann.time", player, Placeholder.parsed("banntime", formatiereZeit(bannTime))));
            }
            // ChatColor.GRAY+"Expiration Time: "+ChatColor.WHITE+formatiereZeit(expirationTime)
            loreList.add(t.t("warn.type.expiration.time", player, Placeholder.parsed("expirationtime", formatiereZeit(expirationTime))));
            //   ChatColor.GRAY+"Points: "+ChatColor.WHITE+points
            loreList.add(t.t("warn.type.points", player, Placeholder.parsed("typepoints", String.valueOf(points))));
            ItemStack item = new ItemBuilder(Material.KNOWLEDGE_BOOK).setDisplayName(t.t("warn.type.name", player, Placeholder.parsed("typename", name))).setLoreComponents(loreList).build();
            GuiItem guiitem = new GuiItem(item, event -> {
                event.setCancelled(true);
                // set user bannet oder warn oder kick
            });
            guiItems.add(guiitem);
        }


        return guiItems;
    }

    public static String formatiereZeit(float zeitInStunden) {
        int jahre = (int) (zeitInStunden / (24 * 365));
        int restStunden = (int) (zeitInStunden % (24 * 365));
        int tage = restStunden / 24;
        int stunden = restStunden % 24;
        int minuten = (int) ((zeitInStunden * 60) % 60);
        int sekunden = (int) ((zeitInStunden * 3600) % 60);

        StringBuilder zeitangabe = new StringBuilder();
        if (jahre != 0) {
            zeitangabe.append(jahre).append(" Jahre, ");
        }
        if (tage != 0) {
            zeitangabe.append(tage).append(" Tage, ");
        }
        if (stunden != 0) {
            zeitangabe.append(stunden).append(" Stunden, ");
        }
        if (minuten != 0) {
            zeitangabe.append(minuten).append(" Minuten, ");
        }
        if (sekunden != 0) {
            zeitangabe.append(sekunden).append(" Sekunden");
        }

        return zeitangabe.toString();
    }

    //    0     1        2
    // /warn <Player_Name> <Grund_ID>
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        BanSystem banSystem = Main.getBanSystemAPI();
        Translation t = Main.getTranslation();
        Data data = Main.getData();


        if (!(sender instanceof Player player)) {
            return false;
        }
        String lang = data.getplayerlang(String.valueOf(player.getUniqueId()));
        player.sendMessage(lang);
        if (args.length == 0) {
            //  /warn <Player>
            t.sendMessage("warn.not.enough", player);
        } else if (args.length == 1) {
            try {
                if (data.MCplayerExists(args[0])) {
                    String[] User = data.getMCPlayerInfo(args[0]);
                    if (data.checkIfPlayerExists(User[0])) {
                        String user = User[1];
                    } else {
                        player.sendMessage(t.t("Player.never.on.the.network", player));
                        return false;
                    }
                }
            } catch (IOException e) {
                player.sendMessage(t.t("not.player", player));
                return false;
            }

            Player player1 = Bukkit.getPlayerExact(args[0]);
            String player1name = player1.getName();

            final String legacy = LegacyComponentSerializer.legacyAmpersand().serialize(t.t("warn.gui.name", player, Placeholder.parsed("towarnuser", player1name)));
            ChestGui warnUser = new ChestGui(4, TextHolder.deserialize(legacy));
            StaticPane backround = new StaticPane(0, 0, 9, 4, Pane.Priority.LOWEST);

            backround.fillWith(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName(" ").build(), event -> event.setCancelled(true));

            warnUser.addPane(backround);


            ArrayList<Component> loreList = new ArrayList<>();
            // ChatColor.DARK_RED+"Warn Points: " + banSystem.getPlayerWarningPoints(args[0])
            String userwarnpoints = String.valueOf(banSystem.getPlayerWarningPoints(args[0]));
            loreList.add(t.t("warn.gui.userpoints", player, Placeholder.parsed("userwarnpoints", userwarnpoints)));
            ItemStack wantuserhead = new ItemBuilder(Material.PLAYER_HEAD).setHead(player1name).setDisplayName(t.t("warn.gui.username", player, Placeholder.parsed("username", player1name))).setLoreComponents(loreList).build();
            StaticPane panehead = new StaticPane(0, 0, 1, 1, Pane.Priority.HIGHEST);
            panehead.addItem(new GuiItem(wantuserhead, event -> event.setCancelled(true)), 0, 0);
            warnUser.addPane(panehead);
            PaginatedPane warnsPane = new PaginatedPane(1, 1, 7, 2, Pane.Priority.HIGHEST);
            warnsPane.populateWithGuiItems(taskItemList(player, player1));
            warnUser.addPane(warnsPane);

            warnUser.show(player);

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
