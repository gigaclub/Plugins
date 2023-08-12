package net.gigaclub.buildersystemplugin.Andere.Guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.DispenserGui;
import com.github.stefvanschie.inventoryframework.gui.type.DropperGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import com.google.gson.JsonObject;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.provider.CloudServiceProvider;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import lombok.NonNull;
import net.gigaclub.buildersystem.BuilderSystem;
import net.gigaclub.buildersystemplugin.Andere.CreateServer;
import net.gigaclub.buildersystemplugin.Andere.Data;
import net.gigaclub.buildersystemplugin.Andere.InterfaceAPI.ItemBuilder;
import net.gigaclub.buildersystemplugin.Main;
import net.gigaclub.translation.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static net.gigaclub.buildersystemplugin.Andere.Guis.Navigator.Navigate;

public class WorldGui {

    public static JSONObject worldObject;
    public static JSONObject projecktUserArray;
    static ItemStack outlineintem = new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").build();

    public static void saveProjecktUsers(int projecktID, Player player) {
        // Erstelle den JsonArray
        BuilderSystem builderSystem = Main.getBuilderSystem();

        JSONObject jsonObjeckt = new JSONObject();
        JsonObject team = builderSystem.getWorld(projecktID);
        jsonObjeckt.put(player.getName(), team);

        // Speichere den JsonArray in einer Variablen
        WorldGui.projecktUserArray = jsonObjeckt;

    }

    private static void asyncload(int projecktID, Player player) {
        // Erstelle eine neue Instanz von BukkitRunnable
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                saveProjecktUsers(projecktID, player);
            }
        };
        task.runTaskAsynchronously(Main.getPlugin());
    }


    public static List<GuiItem> projectItemList(Player player) {
        Translation t = Main.getTranslation();

        List<GuiItem> guiItems = new ArrayList<>();
        JSONArray worldArray = worldObject.getJSONArray(player.getName());
        JSONArray userWorlds = worldArray;

        for (int i = 0; i < userWorlds.length(); i++) {

            JSONObject world = userWorlds.getJSONObject(i);

            //   int taskID = world.getInt("task_id");
            //    JSONObject task = builderSystem.getTask(taskID);
            String ownerID = world.getString("owner_id");
            UUID uuid = UUID.fromString(world.getString("owner_id"));
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            String ownerName = offlinePlayer.getName();
            ArrayList<Component> worldlore = new ArrayList<>();

            worldlore.add(t.t("BuilderSystem.worldgui.list.item.id", player, Placeholder.parsed("id", String.valueOf(world.getInt("world_id")))));

            worldlore.add(t.t("BuilderSystem.worldgui.list.item.world.type", player, Placeholder.parsed("worldtype", world.getString("world_type"))));

            worldlore.add(t.t("BuilderSystem.worldgui.list.item.projeckt.owner", player, Placeholder.parsed("owner", ownerName)));
            //   if (!(task.getString("description").isEmpty())) {
            //       worldlore.add(ChatColor.GRAY + "Description: " + ChatColor.WHITE + task.getString("description"));
            //    }
            JSONArray teams = world.getJSONArray("team_ids");
            if (!(teams.isEmpty())) {
                List<String> stringList = new ArrayList<String>();
                for (int j = 0; j < teams.length(); j++) {
                    JSONObject team = teams.getJSONObject(j);
                    String teamname = team.getString("name");
                    stringList.add(teamname);
                }
                String joinedString = String.join(", ", stringList);
                // ChatColor.GRAY + "Builder Teams: " + ChatColor.WHITE + joinedString
                worldlore.add(t.t("BuilderSystem.worldgui.list.item.builder.teams", player, Placeholder.parsed("teamslist", joinedString)));
            }

            JSONArray user = world.getJSONArray("user_ids");
            if (!(user.isEmpty())) {
                List<String> stringList2 = new ArrayList<String>();
                for (int i1 = 0; i1 < user.length(); i1++) {
                    JSONObject uuid2 = user.getJSONObject(i1);
                    String pid = uuid2.getString("mc_uuid");
                    String user_name = Bukkit.getOfflinePlayer(UUID.fromString(pid)).getName();
                    stringList2.add(user_name);
                }
                String joinedString1 = String.join(", ", stringList2);
                // ChatColor.GRAY + "Builder: " + ChatColor.WHITE + joinedString1
                worldlore.add(t.t("BuilderSystem.worldgui.list.item.builder.users", player, Placeholder.parsed("userlist", joinedString1)));
            }
            if (!(uuid.equals(player.getUniqueId()))) {
                // world.getString("name")
                System.out.println("test1 " + player.getName());
                ItemStack project = new ItemBuilder(Material.PAPER).setDisplayName(t.t("BuilderSystem.worldgui.list.item.projeckt.name", player, Placeholder.parsed("name", world.getString("name")))).setLoreComponents(worldlore).build();
                GuiItem guiItem = new GuiItem(project, event -> {
                    asyncload(world.getInt("world_id"), player);
                    projeckt(player, world.getInt("world_id"), world.getString("name"));
                    event.setCancelled(true);
                });
                guiItems.add(guiItem);
            } else {
                System.out.println("test2 " + player.getName());
                ItemStack project = new ItemBuilder(Material.MAP).setDisplayName(t.t("BuilderSystem.worldgui.list.item.projeckt.name", player, Placeholder.parsed("name", world.getString("name")))).setLoreComponents(worldlore).build();
                GuiItem guiItem = new GuiItem(project, event -> {
                    asyncload(world.getInt("world_id"), player);
                    projeckt(player, world.getInt("world_id"), world.getString("name"));
                    event.setCancelled(true);
                });
                guiItems.add(guiItem);

            }

        }

        return guiItems;
    }

    public static void projecktList(Player player) {
        Translation t = Main.getTranslation();
        Data data = Main.getData();
        // "Project List Page 1"
        ChestGui taskList = new ChestGui(6, data.setGuiName("BuilderSystem.worldgui.list.gui", player, Placeholder.parsed("page", String.valueOf(1))));
        StaticPane outline = new StaticPane(0, 0, 9, 1);
        StaticPane outline2 = new StaticPane(0, 1, 1, 4);
        StaticPane outline3 = new StaticPane(8, 1, 1, 4);

        outline.setOnClick(event -> event.setCancelled(true));
        outline2.setOnClick(event -> event.setCancelled(true));
        outline3.setOnClick(event -> event.setCancelled(true));
        outline.fillWith(outlineintem);
        outline2.fillWith(outlineintem);
        outline3.fillWith(outlineintem);
        taskList.addPane(outline);
        taskList.addPane(outline2);
        taskList.addPane(outline3);

        taskList.setOnBottomClick(event -> event.setCancelled(true));

        PaginatedPane taskPages = new PaginatedPane(1, 1, 7, 4);
        taskPages.populateWithGuiItems(projectItemList(player));

        StaticPane navigation = new StaticPane(0, 5, 9, 1);
        navigation.setOnClick(event -> event.setCancelled(true));
        navigation.setPriority(Pane.Priority.HIGHEST);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8784).setDisplayName(t.t("BuilderSystem.page.list.back", player)).build(), event -> {
            if (taskPages.getPage() > 0) {
                taskPages.setPage(taskPages.getPage() - 1);
                taskList.setTitle(data.setGuiName("BuilderSystem.worldgui.list.gui", player, Placeholder.parsed("page", String.valueOf(taskPages.getPage() + 1))));
                taskList.update();
            } else event.setCancelled(true);
        }), 1, 0);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8782).setDisplayName(t.t("BuilderSystem.page.list.next", player)).build(), event -> {
            if (taskPages.getPage() < taskPages.getPages() - 1) {
                taskPages.setPage(taskPages.getPage() + 1);
                taskList.setTitle(data.setGuiName("BuilderSystem.worldgui.list.gui", player, Placeholder.parsed("page", String.valueOf(taskPages.getPage() + 1))));
                taskList.update();
            } else event.setCancelled(true);
        }), 7, 0);


        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD)
                .setHeadDatabase(10298)
                .setDisplayName(t.t("BuilderSystem.back.to.main", player))
                .build(), event -> Navigate(player)), 4, 0);

        navigation.fillWith(outlineintem, event -> event.setCancelled(true));

        if (taskPages.getPages() == 1) {
            taskList.setTitle(data.setGuiName("BuilderSystem.worldgui.list.gui.first.page", player));
            StaticPane outline4 = new StaticPane(0, 5, 9, 1);
            outline4.fillWith(outlineintem);
            outline4.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(10298).setDisplayName(t.t("BuilderSystem.back.to.main", player)).build(), event -> Navigate(player)), 4, 0);
            taskList.addPane(outline4);
        } else {
            taskList.addPane(navigation);
        }

        taskList.addPane(taskPages);
        taskList.show(player);
    }

    public static void projeckt(Player player, int projectID, String projectName) {
        Translation t = Main.getTranslation();
        Data data = Main.getData();

        ChestGui projectManage = new ChestGui(4, data.setGuiName("BuilderSystem.worldgui.manage.project", player, Placeholder.parsed("projectname", projectName)));
        StaticPane pane = new StaticPane(0, 0, 9, 4);
        pane.fillWith(outlineintem, event -> event.setCancelled(true));

        // "edit WorldTyp"
        ItemStack editWorldTyp = new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(24064).setDisplayName(t.t("BuilderSystem.worldgui.manage.project.edit.worldTyp", player)).build();
        GuiItem worldTyp = new GuiItem(editWorldTyp, event -> {
            editWorldTyp(player, projectID, projectName);
            event.setCancelled(true);
        });
        pane.addItem(worldTyp, 1, 1);
        pane.setOnClick(event -> event.setCancelled(true));

        //  ToDo Add server Info
        //  and join on klick
        ItemStack serverStatus = new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(15962).setDisplayName(t.t("BuilderSystem.worldgui.manage.project.server.info", player)).build();
        GuiItem serverinfo = new GuiItem(serverStatus, event -> {

            player.sendMessage("Server Info");
            event.setCancelled(true);
        });
        pane.addItem(serverinfo, 7, 1);

        ItemStack playermanager = new ItemBuilder(Material.PLAYER_HEAD).setDisplayName(t.t("BuilderSystem.worldgui.manage.project.players.manage", player)).build();
        GuiItem playerms = new GuiItem(playermanager, event -> {
            ProjecktPlayer(player, projectID);
            event.setCancelled(true);
        });
        pane.addItem(playerms, 3, 1);
        ItemStack teamManager = new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9386).setDisplayName(t.t("BuilderSystem.worldgui.manage.project.teams.manage", player)).build();
        GuiItem teammanage = new GuiItem(teamManager, event -> {
            player.sendMessage("manage Teams");
            event.setCancelled(true);
        });
        pane.addItem(teammanage, 5, 1);
        ItemStack serverButton = null;
        CreateServer createServer = new CreateServer(projectID);
        if (createServer.serviceInfoSnapshot.connected()) {
            serverButton = new ItemBuilder(Material.GRASS_BLOCK).setDisplayName(t.t("buildersystem.worldgui.manage.join", player)).build();
        } else {
            serverButton = new ItemBuilder(Material.GRASS_BLOCK).setDisplayName(t.t("buildersystem.worldgui.manage.start", player)).build();
        }
        GuiItem joinButtonGuiItem = new GuiItem(serverButton, event -> {
            createServer.joinServer(player);
        });
        pane.addItem(joinButtonGuiItem, 4, 2);
        projectManage.addPane(pane);
        projectManage.show(player);
    }


    public static List<GuiItem> PlayerList(Player player, int projecktID) {
        List<GuiItem> guiItems = new ArrayList<>();
        Translation t = Main.getTranslation();


        JSONObject projeckt = projecktUserArray.getJSONObject(player.getName());

        JSONArray players = projeckt.getJSONArray("user_ids");
        List<String> stringList = new ArrayList<String>();

        String owner = projeckt.getString("owner_id");


        ArrayList<Component> loreList1 = new ArrayList<>();
        loreList1.add(t.t("BuilderSystem.worldgui.player.list.owner.line", player));
        loreList1.add(t.t("BuilderSystem.worldgui.player.list.owner.text", player));
        String ownerName = Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName();
        ItemStack TaskItem1 = new ItemBuilder(Material.PLAYER_HEAD).setHead(ownerName).setDisplayName(t.t("BuilderSystem.worldgui.player.list.owner.name", player, Placeholder.parsed("ownername", ownerName))).setLoreComponents(loreList1).addID(projecktID).build();
        GuiItem guiItem1 = new GuiItem(TaskItem1, event -> {
            player.sendMessage(t.t("BuilderSystem.worldgui.player.list.owner.cant.managet", player));
            event.setCancelled(true);
        });
        guiItems.add(guiItem1);

        for (int i1 = 0; i1 < players.length(); i1++) {
            JSONObject uuid = players.getJSONObject(i1);
            String pid = uuid.getString("mc_uuid");
            if (!(owner.equals(pid))) {
                String user_name = Bukkit.getOfflinePlayer(UUID.fromString(pid)).getName();

                stringList.add(user_name);
            }
        }

        for (int i = 0; i < stringList.size(); i++) {

            ArrayList<Component> loreList = new ArrayList<>();
            if ((ownerName.equals(stringList.get(i)))) {
                JSONObject user = players.getJSONObject(i);
                JSONArray perms = user.getJSONArray("permissions");
                loreList.add(t.t("BuilderSystem.worldgui.player.list.line", player));
                ItemStack TaskItem = new ItemBuilder(Material.PLAYER_HEAD).setHead(stringList.get(i)).setDisplayName(t.t("BuilderSystem.worldgui.player.list.name", player, Placeholder.parsed("playername", stringList.get(i)))).setLoreComponents(loreList).addID(projecktID).build();
                @NotNull OfflinePlayer managetPlayer = Bukkit.getOfflinePlayer(stringList.get(i));
                GuiItem guiItem = new GuiItem(TaskItem, event -> {
                    //  if (player.hasPermission("gigaclub_team.edit_user")) {
                    playerManager(projecktID, player, managetPlayer, perms);
                    //  } else { player.sendMessage("No Permission");}
                    event.setCancelled(true);
                });
                guiItems.add(guiItem);
            }
        }
        return guiItems;
    }

    public static void playerManager(int projecktID, Player player, OfflinePlayer managetPlayer, JSONArray perms) {
        Translation t = Main.getTranslation();
        Data data = Main.getData();

        BuilderSystem builderSystem = Main.getBuilderSystem();
        // " Manager"
        ChestGui playermanager = new ChestGui(3, data.setGuiName("BuilderSystem.worldgui.player.manager.gui", player));
        StaticPane pane = new StaticPane(9, 3);
        pane.fillWith(outlineintem);
        pane.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHead(managetPlayer.getName()).setDisplayName(managetPlayer.getName()).build()), 0, 0);
        GuiItem adduserperms = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(997).setDisplayName(t.t("BuilderSystem.worldgui.player.manager.edit.user.perms", player)).build(), event -> {
            //  ToDo Add world player perms
            player.sendMessage("ser player perms");
            event.setCancelled(true);
        });
        pane.addItem(adduserperms, 2, 1);
        // kick managetPlayer.getName()
        GuiItem kickUser = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9348).setDisplayName(t.t("BuilderSystem.worldgui.player.manager.kick.player", player, Placeholder.parsed("playername", managetPlayer.getName()))).build(), event -> {
            DispenserGui confirm = new DispenserGui(data.setGuiName("BuilderSystem.worldgui.player.manager.kick.player.confirm.gui", player, Placeholder.parsed("playerName", managetPlayer.getName())));
            StaticPane cpane = new StaticPane(3, 3);
            cpane.fillWith(outlineintem);
            GuiItem check = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(21774).setDisplayName(t.t("BuilderSystem.worldgui.player.manager.kick.player.confirm.kick", player, Placeholder.parsed("playername", managetPlayer.getName()))).build(), event1 -> {
                builderSystem.kickMember(String.valueOf(player.getUniqueId()), projecktID, String.valueOf(managetPlayer.getUniqueId()));
                confirm.getInventory().close();
                player.sendMessage(t.t("BuilderSystem.worldgui.player.manager.kick.player.confirm.got.kickt", player, Placeholder.parsed("playername", managetPlayer.getName())));
                event1.setCancelled(true);
            });
            cpane.addItem(check, Slot.fromIndex(5));
            GuiItem stop = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9382).setDisplayName(t.t("BuilderSystem.worldgui.player.manager.kick.player.confirm.dount.kick", player, Placeholder.parsed("playername", managetPlayer.getName()))).build(), event1 -> {
                ProjecktPlayer(player, projecktID);
                event1.setCancelled(true);
            });
            cpane.addItem(stop, Slot.fromIndex(3));
            confirm.getContentsComponent().addPane(cpane);
            confirm.show(player);

        });
        pane.addItem(kickUser, 6, 1);
        playermanager.addPane(pane);
        playermanager.show(player);
    }

    public static void ProjecktPlayer(Player player, int projecktID) {
        Translation t = Main.getTranslation();
        Data data = Main.getData();

        ChestGui taskList = new ChestGui(6, data.setGuiName("BuilderSystem.worldgui.world.player.list.gui", player, Placeholder.parsed("page", String.valueOf(1))));
        StaticPane outline = new StaticPane(0, 0, 9, 1);
        StaticPane outline2 = new StaticPane(0, 1, 1, 4);
        StaticPane outline3 = new StaticPane(8, 1, 1, 4);

        outline.setOnClick(event -> event.setCancelled(true));
        outline2.setOnClick(event -> event.setCancelled(true));
        outline3.setOnClick(event -> event.setCancelled(true));
        outline.fillWith(outlineintem);
        outline2.fillWith(outlineintem);
        outline3.fillWith(outlineintem);
        taskList.addPane(outline);
        taskList.addPane(outline2);
        taskList.addPane(outline3);

        taskList.setOnBottomClick(event -> event.setCancelled(true));

        PaginatedPane taskPages = new PaginatedPane(1, 1, 7, 4);
        taskPages.populateWithGuiItems(PlayerList(player, projecktID));

        StaticPane navigation = new StaticPane(0, 5, 9, 1);
        navigation.setOnClick(event -> event.setCancelled(true));
        navigation.setPriority(Pane.Priority.HIGHEST);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8784).setDisplayName(t.t("BuilderSystem.page.list.back", player)).build(), event -> {
            if (taskPages.getPage() > 0) {
                taskPages.setPage(taskPages.getPage() - 1);
                // taskPages.getPage() + 1
                taskList.setTitle(data.setGuiName("BuilderSystem.worldgui.world.player.list.gui", player, Placeholder.parsed("page", String.valueOf(taskPages.getPage() + 1))));
                taskList.update();
            } else event.setCancelled(true);
        }), 2, 0);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8782).setDisplayName(t.t("BuilderSystem.page.list.next", player)).build(), event -> {
            if (taskPages.getPage() < taskPages.getPages() - 1) {
                taskPages.setPage(taskPages.getPage() + 1);
                taskList.setTitle(data.setGuiName("BuilderSystem.worldgui.world.player.list.gui", player, Placeholder.parsed("page", String.valueOf(taskPages.getPage() + 1))));
                taskList.update();
            } else event.setCancelled(true);
        }), 6, 0);
        GuiItem playerInvite = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(21771).setDisplayName(t.t("BuilderSystem.worldgui.world.player.list.invite.player", player)).build(), event -> {
            // To DO
            BuilderSystem builderSystem = Main.getBuilderSystem();
            // "Invite Player"
            AnvilGui invitePlayer = new AnvilGui(data.setGuiName("BuilderSystem.worldgui.world.invite.player.gui", player));
            GuiItem slot1 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(t.t("BuilderSystem.worldgui.world.invite.player.set.player.name", player)).build(), event1 -> {
                event1.setCancelled(true);
            });
            StaticPane pane1 = new StaticPane(1, 1);
            pane1.addItem(slot1, 0, 0);
            invitePlayer.getFirstItemComponent().addPane(pane1);

            invitePlayer.setCost((short) 0);
            StaticPane pane2 = new StaticPane(1, 1);


            GuiItem slot3 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(t.t("BuilderSystem.worldgui.world.invite.player.click.to.invite", player)).build(), event1 -> {


                try {

                    if (data.MCplayerExists(invitePlayer.getRenameText())) {
                        String[] User = data.getMCPlayerInfo(invitePlayer.getRenameText());
                        if (data.checkIfPlayerExists(User[1])) {
                            builderSystem.inviteUserToWorld(String.valueOf(player.getUniqueId()), String.valueOf(Bukkit.getOfflinePlayer(invitePlayer.getRenameText()).getUniqueId()), projecktID);
                            player.sendMessage(t.t("BuilderSystem.worldgui.world.invite.player.send.invite.to", player, Placeholder.parsed("invitetname", User[1])));
                            ProjecktPlayer(player, projecktID);
                            event1.setCancelled(true);

                        } else {

                            data.createPlayer(User[1], String.valueOf(UUID.fromString(User[0])));
                            builderSystem.inviteUserToWorld(String.valueOf(player.getUniqueId()), String.valueOf(UUID.fromString(User[0])), projecktID);

                            player.sendMessage(t.t("BuilderSystem.worldgui.world.invite.player.send.invite.if.joint", player, Placeholder.parsed("invitetname", User[1])));
                            ProjecktPlayer(player, projecktID);
                            event1.setCancelled(true);
                        }
                    }
                } catch (IOException e) {
                    player.sendMessage(t.t("BuilderSystem.worldgui.world.invite.player.not.exist", player));
                    event1.setCancelled(true);
                }
            });

            pane2.addItem(slot3, 0, 0);

            invitePlayer.getResultComponent().addPane(pane2);
            invitePlayer.show(player);
            // vom player list
            event.setCancelled(true);
        });

        JSONObject objeckt = projecktUserArray.getJSONObject(player.getName());
        JSONArray users = objeckt.getJSONArray("user_ids");

        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            String uuid = user.getString("mc_uuid");
            if (uuid.equals(String.valueOf(player.getUniqueId()))) {
                JSONArray perms = user.getJSONArray("permissions");
                for (int i1 = 0; i1 < perms.length(); i1++) {
                    if (perms.get(i1).equals("gigaclub_builder_system.invite_user"))
                        navigation.addItem(playerInvite, 8, 0);
                }
            }
        }
        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD)
                .setHeadDatabase(10298)
                .setDisplayName(t.t("BuilderSystem.back.to.team.select", player))
                .build(), event -> projecktList(player)), 4, 0);

        if (taskPages.getPages() == 1) {
            // "Projeckt Player"
            taskList.setTitle(data.setGuiName("BuilderSystem.worldgui.world.player.list.gui.one.page", player));
            StaticPane outline4 = new StaticPane(0, 5, 9, 1);
            outline4.fillWith(outlineintem);
            outline4.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(10298).setDisplayName(t.t("BuilderSystem.back.to.project.list", player)).build(), event -> projecktList(player)), 4, 0);

            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                String uuid = user.getString("mc_uuid");
                if (uuid.equals(String.valueOf(player.getUniqueId()))) {
                    JSONArray perms = user.getJSONArray("permissions");

                    for (int i1 = 0; i1 < perms.length(); i1++) {

                        if (perms.get(i1).equals("gigaclub_builder_system.invite_user"))
                            outline4.addItem(playerInvite, 8, 0);
                    }
                }
            }

            taskList.addPane(outline4);
        } else {
            taskList.addPane(navigation);
        }

        navigation.fillWith(outlineintem);
        taskList.addPane(taskPages);
        taskList.show(player);
    }


    public static void editWorldTyp(Player player, int projecktID, String projecktName) {
        Translation t = Main.getTranslation();
        Data data = Main.getData();
        player.sendMessage(String.valueOf(projecktUserArray));
        System.out.print(projecktUserArray);
        System.out.print("  ");
        JSONObject objeckt = projecktUserArray.getJSONObject(player.getName());
        JSONArray users = objeckt.getJSONArray("user_ids");

        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            String uuid = user.getString("mc_uuid");
            if (uuid.equals(String.valueOf(player.getUniqueId()))) {
                JSONArray perms = user.getJSONArray("permissions");


                final String[] worldTyp = {"normal_flat"};
                final ChestGui[] worldTypes = {new ChestGui(1, data.setGuiName("BuilderSystem.task.create.type.select.gui", player))};
                StaticPane selector = new StaticPane(9, 1);
                selector.addItem(new GuiItem(new ItemBuilder(Material.GRASS_BLOCK).setDisplayName(t.t("BuilderSystem.task.create.type.select.normal", player)).build(), event -> EditWorldTyp(projecktID, event, "normal", player, projecktName, perms)), 0, 0);
                selector.addItem(new GuiItem(new ItemBuilder(Material.GRASS_BLOCK).setDisplayName(t.t("BuilderSystem.task.create.type.select.normal.flat", player)).build(), event -> EditWorldTyp(projecktID, event, "normal_flat", player, projecktName, perms)), 1, 0);
                selector.addItem(new GuiItem(new ItemBuilder(Material.GRASS_BLOCK).setDisplayName(t.t("BuilderSystem.task.create.type.select.normal.void", player)).build(), event -> EditWorldTyp(projecktID, event, "normal_void", player, projecktName, perms)), 2, 0);

                selector.addItem(new GuiItem(new ItemBuilder(Material.NETHERRACK).setDisplayName(t.t("BuilderSystem.task.create.type.select.nether", player)).build(), event -> EditWorldTyp(projecktID, event, "nether", player, projecktName, perms)), 3, 0);
                selector.addItem(new GuiItem(new ItemBuilder(Material.NETHERRACK).setDisplayName(t.t("BuilderSystem.task.create.type.select.nether.flat", player)).build(), event -> EditWorldTyp(projecktID, event, "nether_flat", player, projecktName, perms)), 4, 0);
                selector.addItem(new GuiItem(new ItemBuilder(Material.NETHERRACK).setDisplayName(t.t("BuilderSystem.task.create.type.select.nether.void", player)).build(), event -> EditWorldTyp(projecktID, event, "nether_void", player, projecktName, perms)), 5, 0);

                selector.addItem(new GuiItem(new ItemBuilder(Material.END_STONE).setDisplayName(t.t("BuilderSystem.task.create.type.select.end", player)).build(), event -> EditWorldTyp(projecktID, event, "end", player, projecktName, perms)), 6, 0);
                selector.addItem(new GuiItem(new ItemBuilder(Material.END_STONE).setDisplayName(t.t("BuilderSystem.task.create.type.select.end.flat", player)).build(), event -> EditWorldTyp(projecktID, event, "end_flat", player, projecktName, perms)), 7, 0);
                selector.addItem(new GuiItem(new ItemBuilder(Material.END_STONE).setDisplayName(t.t("BuilderSystem.task.create.type.select.end.void", player)).build(), event -> EditWorldTyp(projecktID, event, "end_void", player, projecktName, perms)), 8, 0);

                worldTypes[0].addPane(selector);
                worldTypes[0].show(player);
            }
        }
    }

    public static void EditWorldTyp(int projecktID, InventoryClickEvent event, String worldType, Player player, String projecktName, JSONArray perms) {
        Translation t = Main.getTranslation();
        Data data = Main.getData();

        BuilderSystem builderSystem = Main.getBuilderSystem();
        DropperGui gui = new DropperGui(data.setGuiName("BuilderSystem.worldgui.world.type.select.warning.gui", player));
        StaticPane pane = new StaticPane(3, 3);
        ItemStack warn = new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9132).setDisplayName(t.t("BuilderSystem.worldgui.world.type.select.warning", player)).build();
        pane.fillWith(warn);
        pane.setOnClick(event1 -> projeckt(player, projecktID, projecktName));
        ItemStack check = new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(21771).setDisplayName(t.t("BuilderSystem.worldgui.world.type.select.save.edit", player)).setLoreComponents(t.t("BuilderSystem.worldgui.world.type.select.save.edit.lore", player)).build();
        GuiItem Check = new GuiItem(check, event1 -> {
            int res = builderSystem.editWorldType(player.getUniqueId().toString(), projecktID, worldType);
            // "edit successful"
            player.sendMessage(t.t("BuilderSystem.worldgui.world.type.select.save.edit.successful", player));
            CloudServiceProvider cloudServiceProvider = InjectionLayer.boot().instance(CloudServiceProvider.class);
            @UnmodifiableView @NonNull Collection<ServiceInfoSnapshot> serviceInfoSnapshots = cloudServiceProvider.servicesByTask(String.valueOf(projecktID));
            for (ServiceInfoSnapshot sis : serviceInfoSnapshots) {
                sis.provider().stop();
            }
            projeckt(player, projecktID, projecktName);
            event1.setCancelled(true);
        });
        pane.addItem(Check, Slot.fromIndex(4));
        gui.getContentsComponent().addPane(pane);
        gui.show(player);
    }

    public static void manageteams(Player player, int projecktID, String projecktName) {


    }


}



