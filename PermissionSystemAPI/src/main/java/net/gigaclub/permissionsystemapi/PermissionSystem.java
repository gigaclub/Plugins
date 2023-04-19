package net.gigaclub.permissionsystemapi;

import net.gigaclub.base.odoo.Odoo;
import org.apache.xmlrpc.XmlRpcException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class PermissionSystem {

    private Odoo odoo;

    public PermissionSystem(String hostname, String database, String username, String password) {
        this.odoo = new Odoo(hostname, database, username, password);
    }

    public JSONArray getAllGroups() {
        try {
            return new JSONArray(
                this.odoo.getModels().execute(
                    "execute_kw",
                    Arrays.asList(
                        this.odoo.getDatabase(),
                        this.odoo.getUid(),
                        this.odoo.getPassword(),
                        "gc.permission.group",
                        "get_all_groups",
                        Arrays.asList()
                    )
                )
            );
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

    public JSONArray getGroups(String playerUUID) {
        try {
            return new JSONArray(
                this.odoo.getModels().execute(
                    "execute_kw",
                    Arrays.asList(
                        this.odoo.getDatabase(),
                        this.odoo.getUid(),
                        this.odoo.getPassword(),
                        "gc.permission.group",
                        "get_groups",
                        Arrays.asList(playerUUID)
                    )
                )
            );
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return null;
    }

}