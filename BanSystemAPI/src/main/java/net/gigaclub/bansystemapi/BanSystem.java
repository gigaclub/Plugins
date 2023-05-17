package net.gigaclub.bansystemapi;

import com.google.gson.JsonArray;
import net.gigaclub.base.odoo.Odoo;
import org.apache.xmlrpc.XmlRpcException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BanSystem {
    protected Odoo odoo;

    public BanSystem(String hostname, String database, String username, String password) {
        this.odoo = new Odoo(hostname, database, username, password);
    }

    public int getPlayerWarningPoints(String playerUUID) {
        try {
            return (int) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(),
                    this.odoo.getUid(),
                    this.odoo.getPassword(),
                    "gc.user",
                    "get_player_warning_points",
                    Collections.singletonList(playerUUID)
            ));
        } catch (XmlRpcException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Object> getBlockedIPv4Hashes() {
        try {
            return (List<Object>) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(),
                    this.odoo.getUid(),
                    this.odoo.getPassword(),
                    "gc.ip",
                    "get_blocked_ipv4_hashes",
                    Collections.emptyList()
            ));
        } catch (XmlRpcException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonArray getBannedPlayerUUIDs() {
        try {
            return this.odoo.gson.toJsonTree(this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(),
                    this.odoo.getUid(),
                    this.odoo.getPassword(),
                    "gc.user",
                    "get_banned_players",
                    Collections.emptyList()
            ))).getAsJsonArray();
        } catch (XmlRpcException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonArray getWarningTypes() {
        try {
            return this.odoo.gson.toJsonTree(this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(),
                    this.odoo.getUid(),
                    this.odoo.getPassword(),
                    "gc.warning.type",
                    "get_warning_types",
                    Collections.emptyList()
            ))).getAsJsonArray();
        } catch (XmlRpcException e) {
            throw new RuntimeException(e);
        }
    }

}
