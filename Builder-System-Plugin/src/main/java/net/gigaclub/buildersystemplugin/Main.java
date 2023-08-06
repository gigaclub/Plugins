package net.gigaclub.buildersystemplugin;

import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import lombok.Getter;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import net.gigaclub.buildersystem.BuilderSystem;
import net.gigaclub.buildersystemplugin.Andere.Data;
import net.gigaclub.buildersystemplugin.Andere.Guis.Navigator;
import net.gigaclub.buildersystemplugin.Commands.Tasks;
import net.gigaclub.buildersystemplugin.Commands.Worlds;
import net.gigaclub.buildersystemplugin.Commands.getgui;
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
import java.util.HashMap;


public final class Main extends JavaPlugin implements Listener {

    final public static String PREFIX = "[GC-BSP]: ";
    @Getter
    private static Main plugin;
    @Getter
    private static Translation translation;
    private static BuilderSystem builderSystem;

    public static void setTranslation(Translation translation) {
        Main.translation = translation;
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

    @Getter
    private static Data data;

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
                "BuilderSystem.countdown_begin",

                //      status msg´s

                // Navigator Gui
                "BuilderSystem.navigator.gui.name",
                // Navigator team
                "BuilderSystem.navigator.team.item",
                "BuilderSystem.navigator.team.lore1",
                "BuilderSystem.navigator.team.lore2",
                "BuilderSystem.navigator.team.lore3",
                // Navigator Projeckt
                "BuilderSystem.navigator.world.item",
                "BuilderSystem.navigator.world.lore1",
                "BuilderSystem.navigator.world.lore2",
                "BuilderSystem.navigator.world.lore3",

                // gui List and shortcuts
                "BuilderSystem.back.to.main",
                "BuilderSystem.page.list.back",
                "BuilderSystem.page.list.next",

                // Navigator Task
                "BuilderSystem.navigator.task.item",
                "BuilderSystem.navigator.task.lore1",
                "BuilderSystem.navigator.task.lore2",
                "BuilderSystem.navigator.task.lore3",
                // Team Gui

                "BuilderSystem.team.gui.name",
                "BuilderSystem.team.gui.item.create",
                "BuilderSystem.team.gui.item.yourteams",
                "BuilderSystem.team.gui.item.invits",

                //
                "BuilderSystem.back.to.team.gui",

                // Team create
                "BuilderSystem.team.create.setname.gui.name",
                "BuilderSystem.team.create.name.item",
                "BuilderSystem.team.create.setname.gui.name",
                "BuilderSystem.team.create.name.accept",
                "BuilderSystem.team.create.teamname",
                "BuilderSystem.team.create.description.item",
                "BuilderSystem.team.create.set.description.gui.name",
                "BuilderSystem.team.create.description.accept",

                "BuilderSystem.team.create.succses",

                new HashMap<String, String>() {{
                    put("translationName", "BuilderSystem.team.create.succses");
                    put("teamName", "Name");
                }},

                new HashMap<String, String>() {{
                    put("translationName", "BuilderSystem.team.list.team.id");
                    put("ID", "1");
                }},

                new HashMap<String, String>() {{
                    put("translationName", "BuilderSystem.team.list.team.owner");
                    put("Owner", "GigaClub");
                }},

                new HashMap<String, String>() {{
                    put("translationName", "BuilderSystem.team.list.team.description");
                    put("Description", "Description");
                }},

                new HashMap<String, String>() {{
                    put("translationName", "BuilderSystem.team.list.team.member");
                    put("teamMember", "player1 ,player2");
                }},

                new HashMap<String, String>() {{
                    put("translationName", "BuilderSystem.team.list.team.name");
                    put("Name", "Name");
                }},

                "BuilderSystem.team.team.list",
                "BuilderSystem.team.team.menu.team.mangae.gui",
                "BuilderSystem.team.team.menu.player.Manager",
                "BuilderSystem.team.team.menu.edit.team",
                "BuilderSystem.team.team.edit.gui",
                "BuilderSystem.team.team.edit.name",
                "BuilderSystem.team.team.edit.description",
                "BuilderSystem.team.team.player.list.owner.lore1",
                "BuilderSystem.team.team.player.list.owner.lore2",

                new HashMap<String, String>() {{
                    put("translationName", "BuilderSystem.team.team.player.list.owner.name");
                    put("OwnerName", "GigaClub");
                }},

                "BuilderSystem.team.team.player.list.player.lore1",
                "BuilderSystem.team.team.player.list.player.lore2",


                new HashMap<String, String>() {{
                    put("translationName", "BuilderSystem.team.player.manager.gui");
                    put("managetPlayer", "GigaClub");
                }},

                "BuilderSystem.team.player.manager.player",
                "BuilderSystem.team.player.manager.player.set.group",
                "BuilderSystem.team.player.manager.player.set.perms",

                new HashMap<String, String>() {{
                    put("translationName", "BuilderSystem.team.player.manager.kick.player");
                    put("kicktPlayer", "GigaClub");
                }},

                new HashMap<String, String>() {{
                    put("translationName", "BuilderSystem.team.player.manager.kick.player.confirm");
                    put("kicktPlayer", "GigaClub");
                }},

                new HashMap<String, String>() {{
                    put("translationName", "BuilderSystem.team.player.manager.kick.player.got.kickt");
                    put("kicktPlayer", "GigaClub");
                }},

                new HashMap<String, String>() {{
                    put("translationName", "BuilderSystem.team.player.manager.kick.player.confirm.dont.kick");
                    put("kicktPlayer", "GigaClub");
                }},

                "BuilderSystem.team.player.list.gui",

                new HashMap<String, String>() {{
                    put("translationName", "BuilderSystem.team.player.list.gui.pages");
                    put("page", "1");
                }},


                "BuilderSystem.team.player.list.invite.player",
                "BuilderSystem.team.player.list.invite.player.gui",
                "BuilderSystem.team.player.list.invite.player.tipe.player.name",
                "BuilderSystem.team.player.list.invite.player.click.to.invite",

                new HashMap<String, String>() {{
                    put("translationName", "BuilderSystem.team.player.list.invite.player.invite.sent");
                    put("invitetPlayer", "Gigaclub");
                }},

                "BuilderSystem.team.player.list.invite.player.is.not.player",
                "BuilderSystem.back.to.team.select",
                "BuilderSystem.team.player.list.players.teams.gui",
                "BuilderSystem.team.edit.team.name.gui",
                "BuilderSystem.team.edit.team.name.old",
                "BuilderSystem.team.edit.team.description",
                "BuilderSystem.team.edit.team.description.empty",

                new HashMap<String, String>() {{
                    put("translationName", "BuilderSystem.team.edit.team.description.old");
                    put("description", "Gigaclub");
                }},

                "BuilderSystem.team.edit.team.name.save",
                "BuilderSystem.team.invits.item.lore1",
                "BuilderSystem.team.invits.item.lore2",

                new HashMap<String, String>() {{
                    put("translationName", "BuilderSystem.team.invits.item.invite.from");
                    put("inviteFrom", "Gigaclub");
                }},

                "BuilderSystem.team.invits.list.gui"

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

        getCommand("getgui").setExecutor(new getgui());

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