package net.gigaclub.team;

import net.gigaclub.team.commands.TeamCommand;
import net.gigaclub.teamapi.Team;
import net.gigaclub.translation.Translation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class Main extends JavaPlugin {

    private static Main plugin;
    private static Translation translation;
    private static Team team;

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
        Main.translation.setCategory("team");

        setTeam(new Team(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password")
        ));

        this.registerCommands();

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

    public static Team getTeam() {
        return Main.team;
    }

    public static void setTeam(Team team) {
        Main.team = team;
    }

    public void registerCommands() {
        Objects.requireNonNull(getCommand("team")).setExecutor(new TeamCommand());
    }

}
