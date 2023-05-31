package net.gigaclub.banproxy;

import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetAddress;

public class PlayerPreLoginListener implements Listener {

    @EventHandler
    public void onPlayerPreLogin(PreLoginEvent event) {
        String playerName = event.getConnection().getName();
        InetAddress ip = event.getConnection().getVirtualHost().getAddress();

        if (!(BanProxy.getUUids().isEmpty())) {
            BanProxy.getPlugin().getProxy().broadcast(String.valueOf(BanProxy.getUUids()));
        }
        // event.setCancelled(true);
        // event.setCancelReason("Du bist nicht auf der Whitelist.");
    }
}
