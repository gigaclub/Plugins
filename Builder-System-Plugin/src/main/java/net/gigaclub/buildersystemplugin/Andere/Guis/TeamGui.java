package net.gigaclub.buildersystemplugin.Andere.Guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.DispenserGui;
import com.github.stefvanschie.inventoryframework.gui.type.HopperGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.gigaclub.buildersystem.BuilderSystem;
import net.gigaclub.buildersystemplugin.Andere.Data;
import net.gigaclub.buildersystemplugin.Andere.InterfaceAPI.ItemBuilder;
import net.gigaclub.buildersystemplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.gigaclub.buildersystemplugin.Andere.Guis.Navigator.Navigate;


public class TeamGui implements Listener {

    static ItemStack outlineintem = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName(" ").build();

    public static void teams(Player player) {
        ChestGui teams = new ChestGui(4, "Team");
        StaticPane pane = new StaticPane(0, 0, 9, 4);
        pane.fillWith(outlineintem);
        pane.setOnClick(event -> event.setCancelled(true));
        pane.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9943).setDisplayName("Create Team").setGlow(true).build(), event -> {
            teamCreate(player, null, null);
            event.setCancelled(true);
        }), 2, 1);
        pane.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9386).setDisplayName("Your Teams").setGlow(true).build(), event -> {
            TeamList(player);
            event.setCancelled(true);
        }), 6, 1);
        pane.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9937).setDisplayName("Team Invites").build(), event -> {
            TeamInvits(player);
        }), 4, 2);
        teams.addPane(pane);
        teams.show(player);
    }

    public static void teamCreate(Player player, String name, String description) {
        BuilderSystem builderSystem = Main.getBuilderSystem();
        HopperGui teamCreate = new HopperGui("         Team Create");
        StaticPane pane = new StaticPane(0, 0, 5, 1);
        pane.fillWith(outlineintem);


        ArrayList<String> loreList = new ArrayList<>();
        if (!(name == null)) {
            loreList.add(ChatColor.GRAY + "Team Name: " + ChatColor.WHITE + name);
        }
        pane.addItem(new GuiItem(new ItemBuilder(Material.BOOK).setDisplayName("Name").setLore(loreList).build(), event -> {
            event.setCancelled(true);
            AnvilGui setName = new AnvilGui("Set Name");

            StaticPane pane1 = new StaticPane(1, 1);
            if (description == null) {
                GuiItem slot1 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName("Name").build(), event1 -> {
                    event1.setCancelled(true);
                });
                pane1.addItem(slot1, 0, 0);
                event.setCancelled(true);

            } else {

                GuiItem slot1 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(description).build(), event1 -> {
                    event1.setCancelled(true);
                });
                pane1.addItem(slot1, 0, 0);

                event.setCancelled(true);
            }
            setName.getFirstItemComponent().addPane(pane1);
            StaticPane pane2 = new StaticPane(1, 1);
            GuiItem slot3 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName("Click to Save Name").build(), event1 -> {
                teamCreate(player, setName.getRenameText(), description);
                event1.setCancelled(true);

            });
            pane2.addItem(slot3, 0, 0);
            setName.getResultComponent().addPane(pane2);

            setName.show(player);

        }), 0, 0);

        loreList.clear();

        if (!(description == null)) {
            loreList.add(ChatColor.GRAY + "Team Description: " + ChatColor.WHITE + description);
        }
        pane.addItem(new GuiItem(new ItemBuilder(Material.BOOK).setDisplayName("Description").setLore(loreList).build(), event -> {
            event.setCancelled(true);
            AnvilGui setDescription = new AnvilGui("Set Description");

            StaticPane pane1 = new StaticPane(1, 1);
            if (description == null) {
                GuiItem slot1 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName("Description").build(), event1 -> {
                    event1.setCancelled(true);
                });
                pane1.addItem(slot1, 0, 0);
                event.setCancelled(true);

            } else {

                GuiItem slot1 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(description).build(), event1 -> {
                    event1.setCancelled(true);
                });
                pane1.addItem(slot1, 0, 0);

                event.setCancelled(true);
            }
            setDescription.getFirstItemComponent().addPane(pane1);
            StaticPane pane2 = new StaticPane(1, 1);
            GuiItem slot3 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName("Click to Save Description").build(), event1 -> {
                teamCreate(player, name, setDescription.getRenameText());
                event1.setCancelled(true);

            });
            pane2.addItem(slot3, 0, 0);
            setDescription.getResultComponent().addPane(pane2);

            setDescription.show(player);
        }), 2, 0);

        loreList.clear();

        pane.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(21771).setDisplayName("Create Team").setLore(loreList).build(), event -> {
            if (name == null) {
                player.sendMessage("the team name is still missing");
            } else {
                if (description == null) {
                    builderSystem.createTeam(String.valueOf(player.getUniqueId()), name);
                    teamCreate.getInventory().close();
                    player.sendMessage("Team " + name + " Wurde erstellt");
                } else {
                    builderSystem.createTeam(String.valueOf(player.getUniqueId()), name, description);
                    teamCreate.getInventory().close();
                    player.sendMessage("Team " + name + " Wurde erstellt");
                }
            }


            event.setCancelled(true);

        }), 4, 0);


        teamCreate.getSlotsComponent().addPane(pane);
        teamCreate.show(player);
    }



    public static List<GuiItem> teamItemList(Player player) {
        BuilderSystem builderSystem = Main.getBuilderSystem();
        List<GuiItem> guiItems = new ArrayList<>();
        JsonArray teams = builderSystem.getTeamsByMember(player.getUniqueId().toString());

        Data data = Main.getData();

        for (JsonElement jsonElement : teams) {
            JsonObject team = jsonElement.getAsJsonObject();

            int ID = team.get("id").getAsInt();
            String name = team.get("name").getAsString();
            String description = team.get("description").getAsString();
            String owner = String.valueOf(Bukkit.getOfflinePlayer(UUID.fromString(team.get("owner_id").getAsString())).getName());

            JsonArray players = team.getAsJsonArray("user_ids");

            List<String> stringList = new ArrayList<String>();
            for (JsonElement jsonElement2 : players) {

                JsonObject uuid = jsonElement2.getAsJsonObject();
                String pid = uuid.get("mc_uuid").getAsString();
                String user_name = Bukkit.getOfflinePlayer(UUID.fromString(pid)).getName();

                stringList.add(user_name);
            }


            String joinedString = String.join(", ", stringList);

            ArrayList<String> loreList = new ArrayList<>();
            loreList.add(ChatColor.GRAY + "ID: " + ChatColor.WHITE + ID);
            loreList.add(ChatColor.GRAY + "Team Owner: " + ChatColor.WHITE + owner);
            if (!(description.isEmpty())) {
                loreList.add(ChatColor.GRAY + "Description: " + ChatColor.WHITE + description);
            }
            loreList.add(ChatColor.GRAY + "Team Member: " + ChatColor.WHITE + joinedString);


            ItemStack project = new ItemBuilder(Material.PAPER).setDisplayName(ChatColor.GRAY + "Name: " + ChatColor.WHITE + name).setLore(loreList).build();

            GuiItem guiItem = new GuiItem(project, event -> {
                event.setCancelled(true);
                TeamMenu(ID, player);
            });
            guiItems.add(guiItem);

        }
        return guiItems;
    }

    public static void TeamList(Player player) {
        ChestGui taskList = new ChestGui(5, "Teams");
        StaticPane outline = new StaticPane(0, 0, 9, 1);
        StaticPane outline2 = new StaticPane(0, 1, 1, 3);
        StaticPane outline3 = new StaticPane(8, 1, 1, 3);
        StaticPane outline4 = new StaticPane(0, 4, 9, 1);

        outline.setOnClick(event -> event.setCancelled(true));
        outline2.setOnClick(event -> event.setCancelled(true));
        outline3.setOnClick(event -> event.setCancelled(true));
        outline4.setOnClick(event -> event.setCancelled(true));
        outline.fillWith(outlineintem);
        outline2.fillWith(outlineintem);
        outline3.fillWith(outlineintem);
        outline4.fillWith(outlineintem);
        taskList.addPane(outline);
        taskList.addPane(outline2);
        taskList.addPane(outline3);
        taskList.addPane(outline4);

        taskList.setOnBottomClick(event -> event.setCancelled(true));

        PaginatedPane taskPages = new PaginatedPane(1, 1, 7, 3);
        taskPages.populateWithGuiItems(teamItemList(player));

        outline4.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD)
                .setHeadDatabase(10298)
                .setDisplayName(ChatColor.DARK_GRAY + "Back to Main Menu")
                .build(), event -> Navigate(player)), 4, 0);
        taskList.addPane(taskPages);
        taskList.show(player);
    }

    public static void TeamMenu(int TeamID, Player player) {
        HopperGui teamMenu = new HopperGui("         Team Manager");
        StaticPane pane = new StaticPane(0, 0, 5, 1);
        pane.setOnClick(event -> event.setCancelled(true));
        pane.fillWith(outlineintem);
        pane.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setDisplayName("Player Manager").build(), event -> {
            TeamPlayer(player, TeamID);
            event.setCancelled(true);
        }), 1, 0);
        pane.addItem(new GuiItem(new ItemBuilder(Material.BOOK).setDisplayName("Edit Team").build(), event -> {
            TeamEdit(TeamID, player);
            event.setCancelled(true);

        }), 3, 0);
        teamMenu.getSlotsComponent().addPane(pane);
        teamMenu.show(player);
    }


    public static void TeamEdit(int TeamID, Player player) {
        HopperGui teamEdit = new HopperGui("         Team Edit");
        StaticPane pane = new StaticPane(0, 0, 5, 1);
        pane.fillWith(outlineintem);
        pane.addItem(new GuiItem(new ItemBuilder(Material.KNOWLEDGE_BOOK).setDisplayName("Name").build(), event -> {
            editTeam(TeamID, player, true);
            event.setCancelled(true);
        }), 1, 0);
        pane.addItem(new GuiItem(new ItemBuilder(Material.BOOK).setDisplayName("Description").build(), event -> {
            editTeam(TeamID, player, false);
            event.setCancelled(true);
        }), 3, 0);

        teamEdit.getSlotsComponent().addPane(pane);
        teamEdit.show(player);
    }

    public static List<GuiItem> TeamPlayerList(Player player, int teamID) {
        BuilderSystem builderSystem = Main.getBuilderSystem();
        List<GuiItem> guiItems = new ArrayList<>();


        JsonObject team = builderSystem.getTeam(teamID);

        JsonArray players = team.getAsJsonArray("user_ids");
        List<String> stringList = new ArrayList<String>();


        String owner = team.get("owner_id").getAsString();


        ArrayList<String> loreList1 = new ArrayList<>();
        loreList1.add(ChatColor.GOLD + "--------------");
        loreList1.add(ChatColor.GOLD + "Owner");
        String ownerName = Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName();
        ItemStack TaskItem1 = new ItemBuilder(Material.PLAYER_HEAD).setHead(ownerName).setDisplayName(ChatColor.GOLD + ownerName).setLore(loreList1).addID(teamID).build();
        GuiItem guiItem1 = new GuiItem(TaskItem1, event -> {
            player.sendMessage("The owner cannot be managed");
            event.setCancelled(true);
        });
        guiItems.add(guiItem1);

        for (JsonElement jsonElement : players) {
            JsonObject uuid = jsonElement.getAsJsonObject();
            String pid = uuid.get("mc_uuid").getAsString();
            String user_name = Bukkit.getOfflinePlayer(UUID.fromString(pid)).getName();

            stringList.add(user_name);
        }

        for (int i = 0; i < stringList.size(); i++) {

            ArrayList<String> loreList = new ArrayList<>();
            loreList.add(ChatColor.GOLD + "--------------");
            ItemStack TaskItem = new ItemBuilder(Material.PLAYER_HEAD).setHead(stringList.get(i)).setDisplayName(ChatColor.WHITE + stringList.get(i)).setLore(loreList).addID(teamID).build();
            @NotNull OfflinePlayer managetPlayer = Bukkit.getOfflinePlayer(stringList.get(i));
            GuiItem guiItem = new GuiItem(TaskItem, event -> {
                //  if (player.hasPermission("gigaclub_team.edit_user")) {
                playerManager(teamID, player, managetPlayer);
                //  } else { player.sendMessage("No Permission");}
                event.setCancelled(true);
            });
            guiItems.add(guiItem);
        }
        // for (int i2 = 0; i2 < stringList.size(); i2++) {
        //
        //      }
        return guiItems;
    }

    public static void playerManager(int TeamID, Player player, OfflinePlayer managetPlayer) {
        BuilderSystem builderSystem = Main.getBuilderSystem();
        ChestGui playermanager = new ChestGui(3, managetPlayer.getName() + " Manager");
        StaticPane pane = new StaticPane(9, 3);
        pane.fillWith(outlineintem);
        pane.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHead(managetPlayer.getName()).setDisplayName(managetPlayer.getName()).build()), 0, 0);
        GuiItem addPermsGroup = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(10250).setDisplayName("set team Group").build(), event -> {
            player.sendMessage("set user Group");
            event.setCancelled(true);
        });
        pane.addItem(addPermsGroup, 2, 1);
        GuiItem adduserperms = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(997).setDisplayName("User Perm").build(), event -> {
            player.sendMessage("ser player perms");
            event.setCancelled(true);
        });
        pane.addItem(adduserperms, 4, 1);

        GuiItem kickUser = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9348).setDisplayName("Kick " + managetPlayer.getName() + " from Team").build(), event -> {
            DispenserGui confirm = new DispenserGui("Confirm Kick " + managetPlayer.getName());
            StaticPane cpane = new StaticPane(3, 3);
            cpane.fillWith(outlineintem);
            GuiItem check = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(21774).setDisplayName("Kick " + managetPlayer.getName()).build(), event1 -> {
                builderSystem.kickMember(String.valueOf(player.getUniqueId()), TeamID, String.valueOf(managetPlayer.getUniqueId()));
                confirm.getInventory().close();
                player.sendMessage(managetPlayer.getName() + " got kicked");
                event1.setCancelled(true);
            });
            cpane.addItem(check, Slot.fromIndex(5));
            GuiItem stop = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9382).setDisplayName("don't kick " + managetPlayer.getName()).build(), event1 -> {
                TeamPlayer(player, TeamID);
                event1.setCancelled(true);
            });
            cpane.addItem(stop, Slot.fromIndex(3));
            confirm.getContentsComponent().addPane(cpane);
            confirm.show(player);

        });
        pane.setOnClick(event -> event.setCancelled(true));
        pane.addItem(kickUser, 6, 1);
        playermanager.addPane(pane);
        playermanager.show(player);
    }

    public static void TeamPlayer(Player player, int teamID) {
        ChestGui taskList = new ChestGui(6, "Team Player List Page 1");
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
        taskPages.populateWithGuiItems(TeamPlayerList(player, teamID));

        StaticPane navigation = new StaticPane(0, 5, 9, 1);
        navigation.setOnClick(event -> event.setCancelled(true));
        navigation.setPriority(Pane.Priority.HIGHEST);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8784).setDisplayName(ChatColor.GRAY + "Back").build(), event -> {
            if (taskPages.getPage() > 0) {
                taskPages.setPage(taskPages.getPage() - 1);
                taskList.setTitle("Team List Page " + (taskPages.getPage() + 1));
                taskList.update();
            } else event.setCancelled(true);
        }), 2, 0);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8782).setDisplayName(ChatColor.GRAY + "Next").build(), event -> {
            if (taskPages.getPage() < taskPages.getPages() - 1) {
                taskPages.setPage(taskPages.getPage() + 1);
                taskList.setTitle("Team List Page " + (taskPages.getPage() + 1));
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
                            builderSystem.inviteMember(String.valueOf(player.getUniqueId()), teamID, String.valueOf(Bukkit.getOfflinePlayer(invitePlayer.getRenameText()).getUniqueId()));

                            player.sendMessage(User[1] + "was sent a request");
                            TeamPlayer(player, teamID);
                            event1.setCancelled(true);
                        } else {

                            data.createPlayer(User[1], String.valueOf(UUID.fromString(User[0])));
                            builderSystem.inviteMember(String.valueOf(player.getUniqueId()), teamID, String.valueOf(UUID.fromString(User[0])));

                            player.sendMessage("Your request will be sent to the player (" + User[1] + ") when he joins the server");
                            TeamPlayer(player, teamID);
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
        navigation.addItem(playerInvite, 8, 0);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD)
                .setHeadDatabase(10298)
                .setDisplayName(ChatColor.DARK_GRAY + "Back to Team Select")
                .build(), event -> TeamList(player)), 4, 0);

        if (taskPages.getPages() == 1) {
            taskList.setTitle("Team Player");
            StaticPane outline4 = new StaticPane(0, 5, 9, 1);
            outline4.fillWith(outlineintem);
            outline4.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(10298).setDisplayName(ChatColor.DARK_GRAY + "Back to Main Menu").build(), event -> Navigate(player)), 4, 0);
            outline4.addItem(playerInvite, 8, 0);
            taskList.addPane(outline4);
        } else {
            taskList.addPane(navigation);
        }

        navigation.fillWith(outlineintem);
        taskList.addPane(taskPages);
        taskList.show(player);
    }


    public static void editTeam(int teamID, Player player, boolean editName) {
        BuilderSystem builderSystem = Main.getBuilderSystem();

        AnvilGui editname = new AnvilGui("Edit Team Name");

        if (editName) {
            GuiItem slot1 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(builderSystem.getTeam(teamID).get("name").getAsString()).build(), event -> {
                event.setCancelled(true);
            });
            StaticPane pane1 = new StaticPane(1, 1);
            pane1.addItem(slot1, 0, 0);
            editname.getFirstItemComponent().addPane(pane1);

        } else {
            editname.setTitle("Edit Team Description");
            if (builderSystem.getTeam(teamID).get("description").getAsString().isEmpty()) {
                String deskr = "Description";
                GuiItem slot1 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(deskr).build(), event -> {
                    event.setCancelled(true);
                });
                StaticPane pane1 = new StaticPane(1, 1);
                pane1.addItem(slot1, 0, 0);
                editname.getFirstItemComponent().addPane(pane1);
            } else {
                String deskr = builderSystem.getTeam(teamID).get("description").getAsString();
                GuiItem slot1 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(deskr).build(), event -> {
                    event.setCancelled(true);
                });
                StaticPane pane1 = new StaticPane(1, 1);
                pane1.addItem(slot1, 0, 0);
                editname.getFirstItemComponent().addPane(pane1);
            }
        }

        editname.setCost((short) 0);
        StaticPane pane2 = new StaticPane(1, 1);
        if (editName) {
            GuiItem slot3 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName("Click to Save Name").build(), event -> {
                builderSystem.editTeam(String.valueOf(player.getUniqueId()), teamID, editname.getRenameText());
                event.setCancelled(true);
                TeamMenu(teamID, player);
            });
            pane2.addItem(slot3, 0, 0);
            editname.getResultComponent().addPane(pane2);
        } else {
            GuiItem slot3 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName("Click to Save Description").build(), event -> {
                builderSystem.editTeam(String.valueOf(player.getUniqueId()), teamID, builderSystem.getTeam(teamID).get("name").getAsString(), editname.getRenameText());
                event.setCancelled(true);
                TeamMenu(teamID, player);
            });
            pane2.addItem(slot3, 0, 0);
            editname.getResultComponent().addPane(pane2);
        }
        editname.show(player);
    }

    public static List<GuiItem> invitetoteamList(Player player) {
        BuilderSystem builderSystem = Main.getBuilderSystem();
        List<GuiItem> guiItems = new ArrayList<>();

        JsonArray invits = builderSystem.getUserMemberToTeamInvitations(String.valueOf(player.getUniqueId()));
        for (JsonElement jsonElement : invits) {
            JsonObject invite = jsonElement.getAsJsonObject();

            int sender = invite.get("sender_team_id").getAsInt();


            ArrayList<String> loreList = new ArrayList<>();
            loreList.add(ChatColor.GRAY + "------------------");
            loreList.add(ChatColor.GRAY + "State: " + ChatColor.WHITE + "Waiting");
            ItemStack invit = new ItemBuilder(Material.WHITE_WOOL).setDisplayName(ChatColor.GRAY + "Invite From " + ChatColor.WHITE + builderSystem.getTeam(sender).get("name").getAsString()).setLore(loreList).build();
            GuiItem guiItem = new GuiItem(invit, event -> {
                player.sendMessage("request menu ");
                event.setCancelled(true);
            });
            guiItems.add(guiItem);


        }

        return guiItems;
    }


    public static void TeamInvits(Player player) {
        ChestGui teamInits = new ChestGui(6, "Invites Page 1");
        StaticPane outline = new StaticPane(0, 0, 9, 1);
        StaticPane outline2 = new StaticPane(0, 1, 1, 4);
        StaticPane outline3 = new StaticPane(8, 1, 1, 4);

        outline.setOnClick(event -> event.setCancelled(true));
        outline2.setOnClick(event -> event.setCancelled(true));
        outline3.setOnClick(event -> event.setCancelled(true));
        outline.fillWith(outlineintem);
        outline2.fillWith(outlineintem);
        outline3.fillWith(outlineintem);
        teamInits.addPane(outline);
        teamInits.addPane(outline2);
        teamInits.addPane(outline3);
        teamInits.setOnBottomClick(event -> event.setCancelled(true));


        PaginatedPane taskPages = new PaginatedPane(1, 1, 7, 4);
        taskPages.populateWithGuiItems(invitetoteamList(player));

        StaticPane navigation = new StaticPane(0, 5, 9, 1);
        navigation.setOnClick(event -> event.setCancelled(true));
        navigation.setPriority(Pane.Priority.HIGHEST);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8784).setDisplayName(ChatColor.GRAY + "Back").build(), event -> {
            if (taskPages.getPage() > 0) {
                taskPages.setPage(taskPages.getPage() - 1);
                teamInits.setTitle("Task List Page " + (taskPages.getPage() + 1));
                teamInits.update();
            } else event.setCancelled(true);
        }), 1, 0);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8782).setDisplayName(ChatColor.GRAY + "Next").build(), event -> {
            if (taskPages.getPage() < taskPages.getPages() - 1) {
                taskPages.setPage(taskPages.getPage() + 1);
                teamInits.setTitle("Task List Page " + (taskPages.getPage() + 1));
                teamInits.update();
            } else event.setCancelled(true);
        }), 7, 0);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(10298).setDisplayName(ChatColor.DARK_GRAY + "Back to Main Menu").build(), event -> Navigate(player)), 4, 0);
        navigation.fillWith(outlineintem);
        if (taskPages.getPages() == 1) {
            teamInits.setTitle("Invites");
            StaticPane outline4 = new StaticPane(0, 5, 9, 1);
            outline4.fillWith(outlineintem);
            outline4.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(10298).setDisplayName(ChatColor.DARK_GRAY + "Back to Main Menu").build(), event -> Navigate(player)), 4, 0);
            teamInits.addPane(outline4);
        } else {
            teamInits.addPane(navigation);
        }
        teamInits.addPane(taskPages);
        teamInits.show(player);
    }

}
