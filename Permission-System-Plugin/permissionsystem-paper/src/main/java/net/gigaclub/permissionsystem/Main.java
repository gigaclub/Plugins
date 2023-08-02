package net.gigaclub.permissionsystem;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.permission.PermissionGroup;
import eu.cloudnetservice.driver.permission.PermissionManagement;
import eu.cloudnetservice.driver.permission.PermissionUser;
import net.gigaclub.permissionsystem.commands.GroupCommand;
import net.gigaclub.permissionsystem.commands.SyncCommand;
import net.gigaclub.permissionsystemapi.PermissionSystem;
import net.gigaclub.translation.Translation;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public final class Main extends JavaPlugin {

    private static Main plugin;
    private static Translation translation;
    private static PermissionSystem permissionSystem;

    @Override
    public void onEnable() {
        // Plugin startup logic
        setPlugin(this);

        File file = new File("plugins//" + "Odoo", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        setTranslation(new Translation(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password"),
                getPlugin()
        ));
        translation.setCategory("permissionsystem");

        setPermissionSystem(new PermissionSystem(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password")
        ));

        Main.setupGroups();

        this.registerCommands();

        registerTranslations();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getPlugin() {
        return plugin;
    }

    public static void setPlugin(Main plugin) {
        Main.plugin = plugin;
    }

    public static Translation getTranslation() {
        return Main.translation;
    }

    public static void setTranslation(Translation translation) {
        Main.translation = translation;
    }

    public static PermissionSystem getPermissionSystem() {
        return Main.permissionSystem;
    }

    public static void setPermissionSystem(PermissionSystem permissionSystem) {
        Main.permissionSystem = permissionSystem;
    }

    public static void setupGroups() {
        PermissionManagement permissionManagement = InjectionLayer.boot().instance(PermissionManagement.class);
        PermissionSystem permissionSystem = Main.getPermissionSystem();

        JsonArray groups = permissionSystem.getAllGroups();
        for (JsonElement jsonElement : groups) {
            JsonObject group = jsonElement.getAsJsonObject();
            String groupName = group.get("name").getAsString();
            JsonArray permissions = group.get("permissions").getAsJsonArray();
            // TODO refactor this with google json
            String suffix = "";
            try {
                suffix = group.get("suffix").getAsString();
            } catch (Exception e) {
            }
            String prefix = "";
            try {
                prefix = group.get("prefix").getAsString();
            } catch (Exception e) {
            }
            String color = "";
            try {
                color = group.get("color").getAsString();
            } catch (Exception e) {
            }
            String display = "";
            try {
                display = group.get("display").getAsString();
            } catch (Exception e) {
            }
            String finalSuffix = suffix;
            String finalPrefix = prefix;
            String finalColor = color;
            String finalDisplay = display;
            var permissionGroup = PermissionGroup.builder().potency(100).name(groupName).suffix(finalSuffix).prefix(finalPrefix).color(finalColor).display(finalDisplay).build();
            for (JsonElement permission : permissions) {
                permissionGroup.addPermission(permission.getAsString());
            }
            permissionManagement.updateGroup(permissionGroup);
        }
        //grepper minecraft paper get a list of all online players
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        //end grepper
        for (Player player : players) {
            JsonArray groupsOfPlayer = permissionSystem.getGroups(player.getUniqueId().toString());
            PermissionUser permissionUser = permissionManagement.user(player.getUniqueId());
            permissionUser.groups().clear();
            for (JsonElement jsonElement : groupsOfPlayer) {
                JsonObject group = jsonElement.getAsJsonObject();
                String groupName = group.get("name").getAsString();
                permissionUser.addGroup(groupName);
                permissionManagement.updateUser(permissionUser);
            }
        }
        permissionManagement.close();
    }

    public void registerCommands() {
        //grepper minecraft paper register commands
        Objects.requireNonNull(getCommand("syncgroups")).setExecutor(new SyncCommand());
        Objects.requireNonNull(getCommand("group")).setExecutor(new GroupCommand());
        //end grepper
    }

    public static void registerTranslations() {
        Main.translation.registerTranslations(Arrays.asList(
                "group.no.parameters",
                "group.list",
                "group.no.parameters",
                "group.add.success",
                "group.remove.success",
                "group.sync.success"
        ));
    }

}
