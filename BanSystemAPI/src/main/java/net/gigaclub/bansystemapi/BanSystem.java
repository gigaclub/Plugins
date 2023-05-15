package net.gigaclub.bansystemapi;

import net.gigaclub.base.odoo.Odoo;

public class BanSystem {
    protected Odoo odoo;

    public BanSystem(String hostname, String database, String username, String password) {
        this.odoo = new Odoo(hostname, database, username, password);
    }


}
