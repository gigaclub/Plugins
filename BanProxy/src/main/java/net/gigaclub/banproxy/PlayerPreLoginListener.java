package net.gigaclub.banproxy;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.UUID;


public class PlayerPreLoginListener implements Listener {

    @EventHandler
    public void onPlayerPreLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        UUID playerUUId = player.getUniqueId();
        SocketAddress ip = player.getPendingConnection().getSocketAddress();
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ip;
        String ipAddress = inetSocketAddress.getAddress().getHostAddress();

        ProxyServer.getInstance().getLogger().info(String.valueOf(playerUUId));
        ProxyServer.getInstance().getLogger().info(String.valueOf(ipAddress));


        if (!(BanProxy.getUUids() == null)) {

            JsonArray banns = BanProxy.getUUids();

            for (JsonElement jsonElement : banns) {
                JsonObject banntUser = jsonElement.getAsJsonObject();
                UUID uuid = UUID.fromString(banntUser.get("mc_uuid").getAsString());
                if (playerUUId.equals(uuid)) {
                    String bann_date = banntUser.get("ban_expiration_datetime").getAsString();
                    int warnIP = banntUser.get("current_warning_id").getAsInt();
                    Map<Integer, String> warnTyps = BanProxy.getWarntyps();

                    String reason = warnTyps.get(warnIP);
                    String result = "\n" +
                            "<dark_red><b>Du wurdes Gebannt</b></dark_red>\n" +
                            "<gold>Grund: </gold><white></white><reason>\n" +
                            "<gold>Dauer: </gold><date>";

                    Component bann = MiniMessage.miniMessage().deserialize(result, Placeholder.parsed("date", bann_date), Placeholder.parsed("reason", reason));
                    String json = GsonComponentSerializer.gson().serialize(bann);
                    BaseComponent[] bungeeComponent = ComponentSerializer.parse(json);
                    player.disconnect(bungeeComponent);
                }
            }


        }


    }

    @EventHandler
    public void ProxyPingEvent(ProxyPingEvent event) {
    }

}
