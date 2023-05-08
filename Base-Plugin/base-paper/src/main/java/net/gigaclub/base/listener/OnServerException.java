package net.gigaclub.base.listener;

import com.destroystokyo.paper.event.server.ServerExceptionEvent;
import net.gigaclub.base.Base;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

public class OnServerException implements Listener {
    @EventHandler
    public void onServerException(ServerExceptionEvent event) {
        String name = event.getException().getMessage();
        String traceback = event.getException().getCause().getMessage() + "\n";
        List<String> stackTraceStrings = Arrays.stream(event.getException().getCause().getStackTrace())
                .map(Object::toString)
                .toList();
        traceback += String.join("\n", stackTraceStrings);
        Base.getData().createException(name, traceback);
    }
}
