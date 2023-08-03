package net.gigaclub.buildersystemplugin.Andere;

import com.github.stefvanschie.inventoryframework.adventuresupport.TextHolder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.gigaclub.base.odoo.Odoo;
import net.gigaclub.buildersystemplugin.Main;
import net.gigaclub.translation.Translation;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

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

    public @NotNull TextHolder setGuiName(String name, Player player, TagResolver... tagResolvers) {
        Translation t = Main.getTranslation();

        final String legacy = LegacyComponentSerializer.legacyAmpersand().serialize(t.t("BuilderSystem.navigator.gui.name", player));

        return TextHolder.deserialize(legacy);
    }

}