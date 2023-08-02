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
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import static net.gigaclub.banproxy.BanProxy.TimeDifferenceExample;


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
            if (banSystem == null) {
                return;
            }

            Set<String> bannedUUIDsCache = BanProxy.getBannedUUIDsCache();

            String playerName = event.getConnection().getName();
            String[] playerData = getMCPlayerInfo(playerName);
            if (playerData != null) {
                String playerUUID = playerData[0];
                if (bannedUUIDsCache.contains(playerUUID)) {
                    JsonObject bannedUser = getBannedUser(banSystem, playerUUID);
                    if (bannedUser != null) {
                        String banDate = bannedUser.get("ban_expiration_datetime").getAsString();
                        List<Integer> bannTime = TimeDifferenceExample(banDate);
                        int Jahr = bannTime.get(0);
                        int Monate = bannTime.get(1);
                        int Tage = bannTime.get(2);
                        int Stunden = bannTime.get(3);
                        int Minuten = bannTime.get(4);
                        int Sekunden = bannTime.get(5);

                        int warnIP = bannedUser.get("current_warning_id").getAsInt();
                        Map<Integer, String> warnTyps = BanProxy.getWarntyps();
                        String reason = warnTyps.get(warnIP);

                        Translation t = BanProxy.getTranslation();
                        Component bann = t.t("bann.disconnect", playerUUID, Placeholder.parsed("reason", reason), Placeholder.parsed("jahr", String.valueOf(Jahr)), Placeholder.parsed("monat", String.valueOf(Monate)), Placeholder.parsed("tag", String.valueOf(Tage)), Placeholder.parsed("stunde", String.valueOf(Stunden)), Placeholder.parsed("minute", String.valueOf(Minuten)), Placeholder.parsed("sekunde", String.valueOf(Sekunden)));
                        String json = GsonComponentSerializer.gson().serialize(bann);
                        BaseComponent[] bungeeComponent = ComponentSerializer.parse(json);
                        event.setCancelReason(bungeeComponent);
                        event.setCancelled(true);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private JsonObject getBannedUser(BanSystem banSystem, String playerUUID) {
        JsonArray bannedUUIDs = banSystem.getBannedPlayerUUIDs();
        if (bannedUUIDs != null) {
            for (JsonElement jsonElement : bannedUUIDs) {
                JsonObject bannedUser = jsonElement.getAsJsonObject();
                String uuid = bannedUser.get("mc_uuid").getAsString();
                if (playerUUID.equals(uuid)) {
                    return bannedUser;
                }
            }
        }
        return null;
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
