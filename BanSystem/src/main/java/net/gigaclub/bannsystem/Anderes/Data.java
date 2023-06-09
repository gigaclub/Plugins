package net.gigaclub.bannsystem.Anderes;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.gigaclub.base.odoo.Odoo;
import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Data {


    private final Odoo odoo;

    public Data(String hostname, String database, String username, String password) {
        this.odoo = new Odoo(hostname, database, username, password);
    }

    public static String formatUUID(String uuid) {
        if (uuid.length() == 32) {
            uuid = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" +
                    uuid.substring(16, 20) + "-" + uuid.substring(20);
        }
        return uuid;
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

    public String getplayerlang(String playerUUID) {
        String modelName = "gc.user";

        List<Object> searchResult = odoo.search("mc_uuid", List.of(List.of(Arrays.asList("mc_uuid", "=", playerUUID)), List.of(List.of(List.of("lang")))));
        return String.valueOf(searchResult);

    }

    public boolean MCplayerExists(String playerName) throws IOException {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + playerName;
        URL obj = new URL(url);
        Scanner scanner = new Scanner(obj.openStream());
        String response = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return !response.contains("error");
    }

    public String[] getMCPlayerInfo(String playerName) throws IOException {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + playerName;
        URL obj = new URL(url);
        Scanner scanner = new Scanner(obj.openStream());
        String response = scanner.useDelimiter("\\Z").next();
        scanner.close();
        if (!response.contains("error")) {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(response).getAsJsonObject();
            String uuid = jsonObject.get("id").getAsString();
            String name = jsonObject.get("name").getAsString();
            String uuid2 = formatUUID(uuid);
            return new String[]{uuid2, name};
        } else {
            return null;
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


}