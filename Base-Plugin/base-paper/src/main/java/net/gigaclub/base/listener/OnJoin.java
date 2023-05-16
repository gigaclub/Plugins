package net.gigaclub.base.listener;

import net.gigaclub.base.Base;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class OnJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();
        String playerName = player.getName();
        if (Base.getData().checkIfPlayerExists(playerUUID)) {
            if (Base.getData().checkName(playerName, playerUUID)) {
                Base.getData().updateName(playerName, playerUUID);
            }
        } else {
            Base.getData().createPlayer(playerName, playerUUID);
        }
        Base.getData().updateStatus(playerUUID, "online");
        String playerIpHash = this.generateHash(player.getAddress().getHostString());
        Base.getData().makeIpEntry(playerUUID, playerIpHash);
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
