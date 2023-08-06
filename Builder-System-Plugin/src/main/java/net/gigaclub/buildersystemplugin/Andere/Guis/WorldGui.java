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
import net.gigaclub.buildersystem.BuilderSystem;
import net.gigaclub.buildersystemplugin.Andere.CreateServer;
import net.gigaclub.buildersystemplugin.Andere.Data;
import net.gigaclub.buildersystemplugin.Andere.InterfaceAPI.ItemBuilder;
import net.gigaclub.buildersystemplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
            ArrayList<String> worldlore = new ArrayList<>();
            worldlore.add(ChatColor.GRAY + "ID: " + ChatColor.WHITE + world.getInt("world_id"));
            worldlore.add(ChatColor.GRAY + "World Type" + " " + ChatColor.WHITE + world.getString("world_type"));
            worldlore.add(ChatColor.GRAY + "Projeckt Owner: " + ChatColor.WHITE + ownerName);
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
                worldlore.add(ChatColor.GRAY + "Builder Teams: " + ChatColor.WHITE + joinedString);
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
                worldlore.add(ChatColor.GRAY + "Builder: " + ChatColor.WHITE + joinedString1);
            }
            if (!(uuid.equals(player.getUniqueId()))) {

                ItemStack project = new ItemBuilder(Material.PAPER).setDisplayName(ChatColor.GRAY + "Name: " + ChatColor.WHITE + world.getString("name")).setLore(worldlore).build();
                GuiItem guiItem = new GuiItem(project, event -> {
                    asyncload(world.getInt("world_id"), player);
                    projeckt(player, world.getInt("world_id"), world.getString("name"));
                    event.setCancelled(true);
                });
                guiItems.add(guiItem);
            } else {
                ItemStack project = new ItemBuilder(Material.MAP).setDisplayName(ChatColor.GRAY + "Name: " + ChatColor.WHITE + world.getString("name")).setLore(worldlore).build();
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
        ChestGui taskList = new ChestGui(6, "Project List Page 1");
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

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8784).setDisplayName(ChatColor.GRAY + "Back").build(), event -> {
            if (taskPages.getPage() > 0) {
                taskPages.setPage(taskPages.getPage() - 1);
                taskList.setTitle("Projeckt List Page " + (taskPages.getPage() + 1));
                taskList.update();
            } else event.setCancelled(true);
        }), 1, 0);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8782).setDisplayName(ChatColor.GRAY + "Next").build(), event -> {
            if (taskPages.getPage() < taskPages.getPages() - 1) {
                taskPages.setPage(taskPages.getPage() + 1);
                taskList.setTitle("Projeckt List Page " + (taskPages.getPage() + 1));
                taskList.update();
            } else event.setCancelled(true);
        }), 7, 0);


        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD)
                .setHeadDatabase(10298)
                .setDisplayName(ChatColor.DARK_GRAY + "Back to Main Menu")
                .build(), event -> Navigate(player)), 4, 0);

        navigation.fillWith(outlineintem, event -> event.setCancelled(true));

        if (taskPages.getPages() == 1) {
            taskList.setTitle("Project List");
            StaticPane outline4 = new StaticPane(0, 5, 9, 1);
            outline4.fillWith(outlineintem);
            outline4.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(10298).setDisplayName(ChatColor.DARK_GRAY + "Back to Main Menu").build(), event -> Navigate(player)), 4, 0);
            taskList.addPane(outline4);
        } else {
            taskList.addPane(navigation);
        }

        taskList.addPane(taskPages);
        taskList.show(player);
    }

    public static void projeckt(Player player, int projectID, String projectName) {
        ChestGui projectManage = new ChestGui(4, "Manage " + projectName);
        StaticPane pane = new StaticPane(0, 0, 9, 4);
        pane.fillWith(outlineintem, event -> event.setCancelled(true));


        ItemStack editWorldTyp = new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(24064).setDisplayName("edit WorldTyp").build();
        GuiItem worldTyp = new GuiItem(editWorldTyp, event -> {
            editWorldTyp(player, projectID, projectName);
            event.setCancelled(true);
        });
        pane.addItem(worldTyp, 1, 1);
        pane.setOnClick(event -> event.setCancelled(true));

        //  ToDo Add server Info
        //  and join on klick
        ItemStack serverStatus = new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(15962).setDisplayName("Server Info").build();
        GuiItem serverinfo = new GuiItem(serverStatus, event -> {

            player.sendMessage("Server Info");
            event.setCancelled(true);
        });
        pane.addItem(serverinfo, 7, 1);

        ItemStack playermanager = new ItemBuilder(Material.PLAYER_HEAD).setDisplayName("Player Manager").build();
        GuiItem playerms = new GuiItem(playermanager, event -> {
            ProjecktPlayer(player, projectID);
            event.setCancelled(true);
        });
        pane.addItem(playerms, 3, 1);
        ItemStack teamManager = new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9386).setDisplayName("Team Manager").build();
        GuiItem teammanage = new GuiItem(teamManager, event -> {
            player.sendMessage("manage Teams");
            event.setCancelled(true);
        });
        pane.addItem(teammanage, 5, 1);
        ItemStack serverButton = null;
        CreateServer createServer = new CreateServer(projectID);
        if (createServer.serviceInfoSnapshot.connected()) {
            serverButton = new ItemBuilder(Material.GRASS_BLOCK).setDisplayName(Main.getTranslation().t("buildersystem.worldgui.manage.join", player)).build();
        } else {
            serverButton = new ItemBuilder(Material.GRASS_BLOCK).setDisplayName(Main.getTranslation().t("buildersystem.worldgui.manage.start", player)).build();
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


        JSONObject projeckt = projecktUserArray.getJSONObject(player.getName());

        JSONArray players = projeckt.getJSONArray("user_ids");
        List<String> stringList = new ArrayList<String>();

        String owner = projeckt.getString("owner_id");


        ArrayList<String> loreList1 = new ArrayList<>();
        loreList1.add(ChatColor.GOLD + "--------------");
        loreList1.add(ChatColor.GOLD + "Owner");
        String ownerName = Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName();
        ItemStack TaskItem1 = new ItemBuilder(Material.PLAYER_HEAD).setHead(ownerName).setDisplayName(ChatColor.GOLD + ownerName).setLore(loreList1).addID(projecktID).build();
        GuiItem guiItem1 = new GuiItem(TaskItem1, event -> {
            player.sendMessage("The owner cannot be managed");
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

            ArrayList<String> loreList = new ArrayList<>();
            if ((ownerName.equals(stringList.get(i)))) {
                JSONObject user = players.getJSONObject(i);
                JSONArray perms = user.getJSONArray("permissions");
                loreList.add(ChatColor.GOLD + "--------------");
                ItemStack TaskItem = new ItemBuilder(Material.PLAYER_HEAD).setHead(stringList.get(i)).setDisplayName(ChatColor.WHITE + stringList.get(i)).setLore(loreList).addID(projecktID).build();
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
        BuilderSystem builderSystem = Main.getBuilderSystem();
        ChestGui playermanager = new ChestGui(3, managetPlayer.getName() + " Manager");
        StaticPane pane = new StaticPane(9, 3);
        pane.fillWith(outlineintem);
        pane.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHead(managetPlayer.getName()).setDisplayName(managetPlayer.getName()).build()), 0, 0);
        GuiItem adduserperms = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(997).setDisplayName("User Perm").build(), event -> {
            player.sendMessage("ser player perms");
            event.setCancelled(true);
        });
        pane.addItem(adduserperms, 2, 1);

        GuiItem kickUser = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9348).setDisplayName("Kick " + managetPlayer.getName() + " from Projeckt").build(), event -> {
            DispenserGui confirm = new DispenserGui("Confirm Kick " + managetPlayer.getName());
            StaticPane cpane = new StaticPane(3, 3);
            cpane.fillWith(outlineintem);
            GuiItem check = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(21774).setDisplayName("Kick " + managetPlayer.getName()).build(), event1 -> {
                builderSystem.kickMember(String.valueOf(player.getUniqueId()), projecktID, String.valueOf(managetPlayer.getUniqueId()));
                confirm.getInventory().close();
                player.sendMessage(managetPlayer.getName() + " got kicked");
                event1.setCancelled(true);
            });
            cpane.addItem(check, Slot.fromIndex(5));
            GuiItem stop = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9382).setDisplayName("don't kick " + managetPlayer.getName()).build(), event1 -> {
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
        ChestGui taskList = new ChestGui(6, "Projeckt Player List Page 1");
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

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8784).setDisplayName(ChatColor.GRAY + "Back").build(), event -> {
            if (taskPages.getPage() > 0) {
                taskPages.setPage(taskPages.getPage() - 1);
                taskList.setTitle("Projeckt Player List Page " + (taskPages.getPage() + 1));
                taskList.update();
            } else event.setCancelled(true);
        }), 2, 0);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8782).setDisplayName(ChatColor.GRAY + "Next").build(), event -> {
            if (taskPages.getPage() < taskPages.getPages() - 1) {
                taskPages.setPage(taskPages.getPage() + 1);
                taskList.setTitle("Projeckt Player List Page " + (taskPages.getPage() + 1));
                taskList.update();
            } else event.setCancelled(true);
        }), 6, 0);
        GuiItem playerInvite = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(21771).setDisplayName(ChatColor.GRAY + "Player Invite").build(), event -> {
            // To DO
            BuilderSystem builderSystem = Main.getBuilderSystem();
            Data data = Main.getData();
            AnvilGui invitePlayer = new AnvilGui("Invite Player");
            GuiItem slot1 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName("Player Name").build(), event1 -> {
                event1.setCancelled(true);
            });
            StaticPane pane1 = new StaticPane(1, 1);
            pane1.addItem(slot1, 0, 0);
            invitePlayer.getFirstItemComponent().addPane(pane1);

            invitePlayer.setCost((short) 0);
            StaticPane pane2 = new StaticPane(1, 1);


            GuiItem slot3 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName("Click to Invite Player").build(), event1 -> {


                try {

                    if (data.MCplayerExists(invitePlayer.getRenameText())) {
                        String[] User = data.getMCPlayerInfo(invitePlayer.getRenameText());
                        if (data.checkIfPlayerExists(User[1])) {
                            builderSystem.inviteUserToWorld(String.valueOf(player.getUniqueId()), String.valueOf(Bukkit.getOfflinePlayer(invitePlayer.getRenameText()).getUniqueId()), projecktID);
                            player.sendMessage(User[1] + "was sent a request");
                            ProjecktPlayer(player, projecktID);
                            event1.setCancelled(true);

                        } else {

                            data.createPlayer(User[1], String.valueOf(UUID.fromString(User[0])));
                            builderSystem.inviteUserToWorld(String.valueOf(player.getUniqueId()), String.valueOf(UUID.fromString(User[0])), projecktID);

                            player.sendMessage("Your request will be sent to the player (" + User[1] + ") when he joins the server");
                            ProjecktPlayer(player, projecktID);
                            event1.setCancelled(true);
                        }
                    }
                } catch (IOException e) {
                    player.sendMessage("input is not a player");
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
                .setDisplayName(ChatColor.DARK_GRAY + "Back to Team Select")
                .build(), event -> projecktList(player)), 4, 0);

        if (taskPages.getPages() == 1) {
            taskList.setTitle("Projeckt Player");
            StaticPane outline4 = new StaticPane(0, 5, 9, 1);
            outline4.fillWith(outlineintem);
            outline4.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(10298).setDisplayName(ChatColor.DARK_GRAY + "Back to Projeckt List").build(), event -> projecktList(player)), 4, 0);

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
        JSONObject objeckt = projecktUserArray.getJSONObject(player.getName());
        JSONArray users = objeckt.getJSONArray("user_ids");

        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            String uuid = user.getString("mc_uuid");
            if (uuid.equals(String.valueOf(player.getUniqueId()))) {
                JSONArray perms = user.getJSONArray("permissions");


                final String[] worldTyp = {"normal_flat"};
                final ChestGui[] worldTypes = {new ChestGui(1, "World Typs Select")};
                StaticPane selector = new StaticPane(9, 1);
                selector.addItem(new GuiItem(new ItemBuilder(Material.GRASS_BLOCK).setDisplayName("Normal").build(), event -> EditWorldTyp(projecktID, event, "normal", player, projecktName, perms)), 0, 0);
                selector.addItem(new GuiItem(new ItemBuilder(Material.GRASS_BLOCK).setDisplayName("Normal Flat").build(), event -> EditWorldTyp(projecktID, event, "normal_flat", player, projecktName, perms)), 1, 0);
                selector.addItem(new GuiItem(new ItemBuilder(Material.GRASS_BLOCK).setDisplayName("Normal Void").build(), event -> EditWorldTyp(projecktID, event, "normal_void", player, projecktName, perms)), 2, 0);

                selector.addItem(new GuiItem(new ItemBuilder(Material.NETHERRACK).setDisplayName("Nether").build(), event -> EditWorldTyp(projecktID, event, "nether", player, projecktName, perms)), 3, 0);
                selector.addItem(new GuiItem(new ItemBuilder(Material.NETHERRACK).setDisplayName("Nether Flat").build(), event -> EditWorldTyp(projecktID, event, "nether_flat", player, projecktName, perms)), 4, 0);
                selector.addItem(new GuiItem(new ItemBuilder(Material.NETHERRACK).setDisplayName("Nether Void").build(), event -> EditWorldTyp(projecktID, event, "nether_void", player, projecktName, perms)), 5, 0);

                selector.addItem(new GuiItem(new ItemBuilder(Material.END_STONE).setDisplayName("End").build(), event -> EditWorldTyp(projecktID, event, "end", player, projecktName, perms)), 6, 0);
                selector.addItem(new GuiItem(new ItemBuilder(Material.END_STONE).setDisplayName("End Flat").build(), event -> EditWorldTyp(projecktID, event, "end_flat", player, projecktName, perms)), 7, 0);
                selector.addItem(new GuiItem(new ItemBuilder(Material.END_STONE).setDisplayName("End Void").build(), event -> EditWorldTyp(projecktID, event, "end_void", player, projecktName, perms)), 8, 0);

                worldTypes[0].addPane(selector);
                worldTypes[0].show(player);
            }
        }
    }

    public static void EditWorldTyp(int projecktID, InventoryClickEvent event, String worldType, Player player, String projecktName, JSONArray perms) {
        BuilderSystem builderSystem = Main.getBuilderSystem();
        DropperGui gui = new DropperGui("warning");
        StaticPane pane = new StaticPane(3, 3);
        ItemStack warn = new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9132).setDisplayName("warning").build();
        pane.fillWith(warn);
        pane.setOnClick(event1 -> projeckt(player, projecktID, projecktName));
        ItemStack check = new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(21771).setDisplayName("Save WorldTyp").setLore(ChatColor.RED + "changing the world type can cause problems").build();
        GuiItem Check = new GuiItem(check, event1 -> {
            int res = builderSystem.editWorldType(player.getUniqueId().toString(), projecktID, worldType);
            player.sendPlainMessage("edit successful");
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



