package net.gigaclub.banproxy;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.gigaclub.bansystemapi.BanSystem;
import net.gigaclub.translation.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;


public class PlayerPreLoginListener implements Listener {

    public static String formatUUID(String uuid) {
        if (uuid.length() == 32) {
            uuid = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" +
                    uuid.substring(16, 20) + "-" + uuid.substring(20);
        }
        return uuid;
    }

    @EventHandler
    public void onPlayerPreLogin(PreLoginEvent event) {
        try {
            BanSystem banSystem = BanProxy.getBanSystemAPI();
            JsonArray bannedUUIDs = banSystem.getBannedPlayerUUIDs();
            if (bannedUUIDs != null) {
                String playerName = event.getConnection().getName();
                String[] playerData = getMCPlayerInfo(playerName);
                if (playerData != null) {
                    String playerUUID = playerData[0];

                    for (JsonElement jsonElement : bannedUUIDs) {
                        JsonObject bannedUser = jsonElement.getAsJsonObject();
                        String uuid = bannedUser.get("mc_uuid").getAsString();

                        if (playerUUID.equals(uuid)) {
                            String banDate = bannedUser.get("ban_expiration_datetime").getAsString();
                            int warnIP = bannedUser.get("current_warning_id").getAsInt();
                            Map<Integer, String> warnTypes = BanProxy.getWarntyps();
                            String reason = warnTypes.get(warnIP);

                            Translation translation = BanProxy.getTranslation();
                            Component banDisconnectMessage = translation.t("bann.disconnect", playerUUID, Placeholder.parsed("reason", reason), Placeholder.parsed("date", banDate));
                            String json = GsonComponentSerializer.gson().serialize(banDisconnectMessage);
                            BaseComponent[] bungeeComponent = ComponentSerializer.parse(json);
                            event.setCancelReason(bungeeComponent);
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        // Placeholder for ProxyPingEvent handler
    }

    public String[] getMCPlayerInfo(String playerName) throws IOException {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + playerName;
        URL obj = new URL(url);
        Scanner scanner = new Scanner(obj.openStream());
        String response = scanner.useDelimiter("\\Z").next();
        scanner.close();
        if (!response.contains("error")) {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(response).getAsJsonObject();
            String uuid = jsonObject.get("id").getAsString();
            String name = jsonObject.get("name").getAsString();
            String formattedUUID = formatUUID(uuid);
            return new String[]{formattedUUID, name};
        } else {
            return null;
        }
    }
}
