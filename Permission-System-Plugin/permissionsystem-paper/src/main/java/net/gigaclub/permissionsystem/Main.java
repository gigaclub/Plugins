package net.gigaclub.permissionsystem;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.permission.PermissionManagement;
import net.gigaclub.permissionsystem.commands.GroupCommand;
import net.gigaclub.permissionsystem.commands.SyncCommand;
import net.gigaclub.permissionsystemapi.PermissionSystem;
import net.gigaclub.translation.Translation;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

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

        JSONArray groups = permissionSystem.getAllGroups();
        for (int i = 0; i < groups.length(); i++) {
            JSONObject group = groups.getJSONObject(i);
            String groupName = group.getString("name");
            JSONArray permissions = group.getJSONArray("permissions");
            // TODO refactor this with google json
            String suffix = "";
            try {
                suffix = group.getString("suffix");
            } catch (Exception e) {
            }
            String prefix = "";
            try {
                prefix = group.getString("prefix");
            } catch (Exception e) {
            }
            String color = "";
            try {
                color = group.getString("color");
            } catch (Exception e) {
            }
            String display = "";
            try {
                display = group.getString("display");
            } catch (Exception e) {
            }

            permissionManagement.group(groupName);
            String finalSuffix = suffix;
            String finalPrefix = prefix;
            String finalColor = color;
            String finalDisplay = display;
            permissionManagement.modifyGroup(groupName, (permissionGroup, permissionGroupBuilder) -> {
                permissionGroupBuilder.suffix(finalSuffix).prefix(finalPrefix).color(finalColor).display(finalDisplay);
                for (int j = 0; j < permissions.length(); j++) {
                    permissionGroup.addPermission(permissions.getString(j));
                }
            });
        }
        //grepper minecraft paper get a list of all online players
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        //end grepper
        for (Player player : players) {
            JSONArray groupsOfPlayer = permissionSystem.getGroups(player.getUniqueId().toString());
            permissionManagement.modifyUser(player.getUniqueId(), (user, userBuilder) -> {
                user.groups().forEach(groupOfUser -> user.removeGroup(groupOfUser.group()));
                for (int i = 0; i < groupsOfPlayer.length(); i++) {
                    JSONObject group = groupsOfPlayer.getJSONObject(i);
                    String groupName = group.getString("name");
                    user.addGroup(groupName);
                }
            });
        }

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
