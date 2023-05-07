package net.gigaclub.base.listener;

import com.destroystokyo.paper.event.server.ServerExceptionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;

public class OnServerException implements Listener {
    @EventHandler
    public void onServerException(ServerExceptionEvent event) {
        System.out.println("TEST");
        System.out.println(event.getException());
        System.out.println(Arrays.stream(event.getException().getStackTrace()).toList());
        System.out.println("TEST END");
    }
}
