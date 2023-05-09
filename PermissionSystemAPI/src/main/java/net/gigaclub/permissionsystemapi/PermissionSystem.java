package net.gigaclub.permissionsystemapi;

import com.google.gson.JsonArray;
import net.gigaclub.base.odoo.Odoo;
import org.apache.xmlrpc.XmlRpcException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PermissionSystem {

    private final Odoo odoo;

    public PermissionSystem(String hostname, String database, String username, String password) {
        this.odoo = new Odoo(hostname, database, username, password);
    }

    public JsonArray getAllGroups() {
        try {
            return this.odoo.gson.toJsonTree(this.odoo.getModels().execute(
                    "execute_kw",
                    Arrays.asList(
                            this.odoo.getDatabase(),
                            this.odoo.getUid(),
                            this.odoo.getPassword(),
                            "gc.permission.group",
                            "get_all_groups",
                            List.of()
                    )
            )).getAsJsonArray();
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int setGroups(String playerUUID, List<Integer> groupIds) {
        try {
            return (int) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(), this.odoo.getUid(), this.odoo.getPassword(),
                    "gc.permission.group", "set_groups", Arrays.asList(playerUUID, groupIds)
            ));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public int removeGroups(String playerUUID, List<Integer> groupIds) {
        try {
            return (int) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(), this.odoo.getUid(), this.odoo.getPassword(),
                    "gc.permission.group", "remove_groups", Arrays.asList(playerUUID, groupIds)
            ));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return 4;
    }

    public JsonArray getGroups(String playerUUID) {
        try {
            return this.odoo.gson.toJsonTree(this.odoo.getModels().execute(
                    "execute_kw",
                    Arrays.asList(
                            this.odoo.getDatabase(),
                            this.odoo.getUid(),
                            this.odoo.getPassword(),
                            "gc.permission.group",
                            "get_groups",
                            Collections.singletonList(playerUUID)
                    )
            )).getAsJsonArray();
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return null;
    }

}