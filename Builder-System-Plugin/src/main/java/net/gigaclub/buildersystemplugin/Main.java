package net.gigaclub.buildersystemplugin;

import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import net.gigaclub.buildersystem.BuilderSystem;
import net.gigaclub.buildersystemplugin.Andere.Data;
import net.gigaclub.buildersystemplugin.Andere.Guis.Navigator;
import net.gigaclub.buildersystemplugin.Commands.Tasks;
import net.gigaclub.buildersystemplugin.Commands.Worlds;
import net.gigaclub.buildersystemplugin.Config.Config;
import net.gigaclub.buildersystemplugin.Config.ConfigTeams;
import net.gigaclub.buildersystemplugin.listener.joinlistener;
import net.gigaclub.translation.Translation;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;


public final class Main extends JavaPlugin implements Listener {

    final public static String PREFIX = "[GC-BSP]: ";
    private static Main plugin;
    private static Translation translation;
    private static BuilderSystem builderSystem;

    public static Translation getTranslation() {
        return translation;
    }

    public static void setTranslation(Translation translation) {
        Main.translation = translation;
    }

    public static Main getPlugin() {
        return plugin;
    }

    public static void setPlugin(Main plugin) {
        Main.plugin = plugin;
    }

    public static BuilderSystem getBuilderSystem() {
        return Main.builderSystem;
    }

    public static void setBuilderSystem(BuilderSystem builderSystem) {
        Main.builderSystem = builderSystem;
    }

    private static Data data;

    public static Data getData() {
        return data;
    }

    public static void setData(Data data) {
        Main.data = data;
    }

    public static void registerTranslations() {
        Main.translation.registerTranslations(Arrays.asList(
                //allrounder
                "builder_team.to_less_arguments",
                "builder_team.wrong_arguments",
                "BuilderSystem.toomany_Arguments",

                // tab
                "builder_team.edit.tab_teamname",
                "builder_team.edit.tab_description",
                "builder_team.tab_task_id",

                //Team Command output
                "builder_team.leave",
                "builder_team.kick",
                "builder_team.add_manager",
                "builder_team.add",
                "builder_team.no_permission",
                "builder_team.TimerTimeLeft",
                //Team Command Create
                "builder_team.create.only_name",
                "builder_team.create.name_desc",
                //Team Command Invite
                "builder_team.invite.sender",
                "builder_team.invite.receiver",
                //Team Command Edit
                "builder_team.edit.name",
                "builder_team.edit.description",

                //Team Command Create
                "builder_team.create.tab_teamname",
                "builder_team.create.tab_description",


                //Task Command output
                "builder_team.task.remove_succses",
                "builder_team.task.create.task_name_succses",
                "builder_team.task.create.task_name_desc_succses",
                "builder_team.task_id",
                //Task Command List
                "builder_team.task.list.Description",
                "builder_team.task.list.build_size",
                "builder_team.task.list.projeckt_count",
                //Task tab

                "builder_team.task.create.tab_task_name",
                "builder_team.task.create.tab_task_x_size",
                "builder_team.task.create.tab_task_y_size",


                //World Command output
                "BuilderSystem.world.create_team_succses",
                "BuilderSystem.world.create_user_succses",
                "BuilderSystem.world.remove_succses",
                "BuilderSystem.world.addteam_succses",
                "BuilderSystem.world.adduser_succses",
                //World list Command
                "BuilderSystem.world.id_list",
                "BuilderSystem.world.name_list",
                "BuilderSystem.world.world_typ_list",
                "BuilderSystem.world.team_list",
                "BuilderSystem.world.user_list",
                //World tab
                "builder_team.world.tab_world_name",
                "builder_team.world.tab_team_id",


                // countdown
                "BuilderSystem.countdown_begin"

                //      status msg´s


        ));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void setConfig() {
        Config.createConfig();

        ConfigTeams.setConfigTeams();
        Config.save();

        getLogger().info(PREFIX + "Config files set.");
    }

    @Override
    public void onEnable() {
        plugin = this;
        setPlugin(this);

        setConfig();

        this.getServer().getPluginManager().registerEvents(this, this);

        getCommand("gctask").setExecutor(new Tasks());
        getCommand("gctask").setTabCompleter(new Tasks());

        Worlds projeckt = new Worlds();
        getCommand("gcprojekt").setExecutor(projeckt);
        getCommand("gcprojekt").setTabCompleter(projeckt);


        File file = new File("plugins//" + "Odoo", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        InjectionLayer.boot().instance(EventManager.class).registerListener(projeckt);
        setTranslation(new Translation(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password"),
                getPlugin()
        ));
        translation.setCategory("builderSystem");
        setBuilderSystem(new BuilderSystem(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password")
        ));


        setData(new Data(
                config.getString("Odoo.Host"),
                config.getString("Odoo.Database"),
                config.getString("Odoo.Username"),
                config.getString("Odoo.Password")
        ));


        registerTranslations();


    }


    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent e) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new joinlistener(), this);
        pluginManager.registerEvents(new Navigator(), this);
    }

}
