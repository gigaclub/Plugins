package net.gigaclub.auth.data;

import net.gigaclub.base.odoo.Odoo;
import org.apache.xmlrpc.XmlRpcException;

import java.util.Arrays;
import java.util.Collections;

public class Data {

    private final Odoo odoo;

    public Data(String hostname, String database, String username, String password) {
        this.odoo = new Odoo(hostname, database, username, password);
    }

    public String generateAuthToken(String playerUUID) {
        try {
            return (String) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(), this.odoo.getUid(), this.odoo.getPassword(),
                    "gc.user", "generate_auth_token", Collections.singletonList(playerUUID)
            ));
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return null;
    }

}
