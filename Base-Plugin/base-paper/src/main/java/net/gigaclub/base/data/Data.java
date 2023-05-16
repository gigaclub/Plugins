package net.gigaclub.base.data;

import com.google.gson.JsonArray;
import net.gigaclub.base.odoo.Odoo;
import org.apache.xmlrpc.XmlRpcException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Data {

    private final Odoo odoo;

    public Data(String hostname, String database, String username, String password) {
        this.odoo = new Odoo(hostname, database, username, password);
    }

    public int getPlayer(String playerUUID) {
        List players = this.odoo.search(
                "gc.user",
                List.of(
                        List.of(
                                Arrays.asList("mc_uuid", "=", playerUUID)
                        )
                ),
                new HashMap() {{ put("limit", 1); }}
        );
        if (players.size() > 0) {
            return (int) players.get(0);
        }
        return 0;
    }

    public boolean checkIfPlayerExists(String playerUUID) {
        return this.odoo.search_count(
            "gc.user",
                List.of(
                        List.of(
                                Arrays.asList("mc_uuid", "=", playerUUID)
                        )
                )
        ) > 0;
    }

    public boolean createPlayer(String name, String playerUUID) {
        return this.odoo.create(
                "gc.user",
                List.of(
                        new HashMap() {{
                            put("name", name);
                            put("mc_uuid", playerUUID);
                        }}
                )
        ) > 0;
    }

    public boolean createException(String name, String traceback) {
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

    public boolean checkName(String name, String playerUUID) {
        return this.odoo.search_count(
                "gc.user",
                List.of(
                        Arrays.asList(
                                Arrays.asList("name", "=", name), Arrays.asList("mc_uuid", "=", playerUUID)
                        )
                )
        ) > 0;
    }

    public void updateName(String name, String playerUUID) {
        this.odoo.write(
            "gc.user",
            Arrays.asList(
                    List.of(this.getPlayer(playerUUID)),
                    new HashMap() {{ put("name", name); }}
            )
        );
    }

    public void updateStatus(String playerUUID, String status) {
        this.odoo.write(
                "gc.user",
                Arrays.asList(
                        List.of(this.getPlayer(playerUUID)),
                        new HashMap() {{
                            put("state", status);
                        }}
                )
        );
    }

    public void makeIpEntry(String playerUUID, String hashedIpAddress) {
        try {
            this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(),
                    this.odoo.getUid(),
                    this.odoo.getPassword(),
                    "gc.user",
                    "make_ip_entry",
                    Arrays.asList(playerUUID, hashedIpAddress)
            ));
        } catch (XmlRpcException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLastIpHash(String playerUUID) {
        try {
            return (String) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(),
                    this.odoo.getUid(),
                    this.odoo.getPassword(),
                    "gc.user",
                    "get_last_ip_hash",
                    Collections.singletonList(playerUUID)
            ));
        } catch (XmlRpcException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Object> getMinecraftStatsTypes() {
        try {
            return Arrays.asList(
                    (Object[]) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                            this.odoo.getDatabase(),
                            this.odoo.getUid(),
                            this.odoo.getPassword(),
                            "gc.minecraft.stats",
                            "get_minecraft_stats_types",
                            Collections.emptyList()
                    )));
        } catch (XmlRpcException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerPlayerStats(JsonArray data) {
        try {
            this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(),
                    this.odoo.getUid(),
                    this.odoo.getPassword(),
                    "gc.minecraft.player.stats",
                    "register_player_stats",
                    Collections.singletonList(data)
            ));
        } catch (XmlRpcException e) {
            throw new RuntimeException(e);
        }
    }

}
