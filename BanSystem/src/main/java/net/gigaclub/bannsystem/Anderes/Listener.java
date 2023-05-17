package net.gigaclub.bannsystem.Anderes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.gigaclub.bannsystem.Main;
import net.gigaclub.bansystemapi.BanSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;


public class Listener implements org.bukkit.event.Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        BanSystem banSystem = Main.getBanSystemAPI();
        String UUID = String.valueOf(event.getPlayer().getUniqueId());
        String IP = String.valueOf(event.getAddress().getHostAddress());
        String reason = "Hacking";
        String date = "16.05.2023 12:00";

        JsonArray bannetUUids = banSystem.getBannedPlayerUUIDs();


        for (JsonElement jsonElement : bannetUUids) {
            String uuid = jsonElement.getAsString();
            if (uuid.equals(UUID)) {
                event.disallow(PlayerLoginEvent.Result.valueOf("you got banned"), Component.text("Ban Reason").color(NamedTextColor.DARK_RED).appendNewline()
                        .append(Component.text("<reason>", (Style) Placeholder.component("reason", Component.text(reason, NamedTextColor.RED)))).appendNewline()
                        .append(Component.text("banned until")).color(NamedTextColor.DARK_RED).appendNewline()
                        .append(Component.text("<date>", (Style) Placeholder.component("date", Component.text(date, NamedTextColor.RED)))));
                return;
            }
        }
    }


}
