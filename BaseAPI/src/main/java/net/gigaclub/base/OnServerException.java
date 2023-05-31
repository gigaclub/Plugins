package net.gigaclub.base;

import com.destroystokyo.paper.event.server.ServerExceptionEvent;
import net.gigaclub.base.odoo.Odoo;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class OnServerException implements Listener {

    private Odoo odoo;

    @EventHandler
    public void onServerException(ServerExceptionEvent event) {

        String name = event.getException().getMessage();
        String traceback = event.getException().getCause().getMessage() + "\n";
        List<String> stackTraceStrings = Arrays.stream(event.getException().getCause().getStackTrace())
                .map(Object::toString)
                .toList();
        traceback += String.join("\n", stackTraceStrings);
        createException(name, traceback);

    }


    public boolean createException(String name, String traceback) {
        File file = new File("plugins//" + "Odoo", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        odoo = new Odoo(config.getString("Odoo.Host"), config.getString("Odoo.Database"), config.getString("Odoo.Username"), config.getString("Odoo.Password"));
        return this.odoo.create(
                "gc.exception",
                List.of(
                        new HashMap() {{
                            put("name", name);
                            put("traceback", traceback);
                        }}
                )
        ) > 0;
    }


}
