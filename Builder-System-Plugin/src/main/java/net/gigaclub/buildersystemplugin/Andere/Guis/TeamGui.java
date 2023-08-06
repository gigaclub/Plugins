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
import net.gigaclub.translation.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.gigaclub.buildersystemplugin.Andere.Guis.Navigator.Navigate;


public class TeamGui implements Listener {

    public static JsonObject teamObjeckt;
    public static JsonObject teamUserArray;
    static ItemStack outlineintem = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName(" ").build();

    public static void saveTeamsUsers(int teamID, Player player) {
        BuilderSystem builderSystem = Main.getBuilderSystem();

        JsonObject jsonObject = new JsonObject();
        JsonObject team = builderSystem.getTeam(teamID);
        jsonObject.add(player.getName(), team);

        TeamGui.teamUserArray = jsonObject;

    }

    private static void asyncload(int teamID, Player player) {
        // Erstelle eine neue Instanz von BukkitRunnable
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                saveTeamsUsers(teamID, player);
            }
        };
        task.runTaskAsynchronously(Main.getPlugin());
    }

    public static void teams(Player player) {
        Translation t = Main.getTranslation();
        Data data = Main.getData();


        // Team
        ChestGui teams = new ChestGui(4, data.setGuiName("BuilderSystem.team.gui.name", player));
        StaticPane pane = new StaticPane(0, 0, 9, 4);
        pane.fillWith(outlineintem);
        pane.setOnClick(event -> event.setCancelled(true));
        // Create Team
        pane.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9943).setDisplayName(t.t("BuilderSystem.team.gui.item.create", player)).setGlow(true).build(), event -> {
            if (player.hasPermission("gigaclub_team.create_team")) {
                teamCreate(player, null, null);
                event.setCancelled(true);
            } else player.sendMessage(t.t("builder_team.no_permission", player));
            event.setCancelled(true);
        }), 2, 1);
        // Your Teams
        pane.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9386).setDisplayName(t.t("BuilderSystem.team.gui.item.yourteams", player)).setGlow(true).build(), event -> {
            TeamList(player);
            event.setCancelled(true);
        }), 6, 1);
        // Team Invites
        pane.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9937).setDisplayName(t.t("BuilderSystem.team.gui.item.invits", player)).build(), event -> {
            TeamInvits(player);
        }), 4, 2);
        teams.addPane(pane);
        teams.show(player);
    }

    public static void teamCreate(Player player, String name, String description) {
        Translation t = Main.getTranslation();
        Data data = Main.getData();
        BuilderSystem builderSystem = Main.getBuilderSystem();
        //          Team Create
        HopperGui teamCreate = new HopperGui(data.setGuiName("BuilderSystem.team.create.gui.name", player));
        StaticPane pane = new StaticPane(0, 0, 5, 1);
        pane.fillWith(outlineintem);


        ArrayList<Component> loreList = new ArrayList<>();
        if (!(name == null)) {
            // Team Name:     name
            loreList.add(t.t("BuilderSystem.team.create.teamname", player));
        }

        pane.addItem(new GuiItem(new ItemBuilder(Material.BOOK).setDisplayName(t.t("BuilderSystem.team.create.name.item", player)).setLoreComponents(loreList).build(), event -> {
            event.setCancelled(true);
            AnvilGui setName = new AnvilGui(data.setGuiName("BuilderSystem.team.create.setname.gui.name", player));

            StaticPane pane1 = new StaticPane(1, 1);
            if (description == null) {
                GuiItem slot1 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(t.t("BuilderSystem.team.create.name.item", player)).build(), event1 -> {
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
            // klick to accept
            GuiItem slot3 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(t.t("BuilderSystem.team.create.name.accept", player)).build(), event1 -> {
                teamCreate(player, setName.getRenameText(), description);
                event1.setCancelled(true);

            });
            pane2.addItem(slot3, 0, 0);
            setName.getResultComponent().addPane(pane2);

            setName.show(player);

        }), 0, 0);

        loreList.clear();

        if (!(description == null)) {
            // Team Description:     description
            loreList.add(t.t("BuilderSystem.team.create.teamname", player));
        }

        pane.addItem(new GuiItem(new ItemBuilder(Material.BOOK).setDisplayName(t.t("BuilderSystem.team.create.description.item", player)).setLoreComponents(loreList).build(), event -> {
            event.setCancelled(true);
            AnvilGui setDescription = new AnvilGui(data.setGuiName("BuilderSystem.team.create.set.description.gui.name", player));

            StaticPane pane1 = new StaticPane(1, 1);
            if (description == null) {
                GuiItem slot1 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(t.t("BuilderSystem.team.create.description.item", player)).build(), event1 -> {
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
            GuiItem slot3 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(t.t("BuilderSystem.team.create.description.accept", player)).build(), event1 -> {
                teamCreate(player, name, setDescription.getRenameText());
                event1.setCancelled(true);

            });
            pane2.addItem(slot3, 0, 0);
            setDescription.getResultComponent().addPane(pane2);

            setDescription.show(player);
        }), 2, 0);

        loreList.clear();

        pane.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(21771).setDisplayName(t.t("BuilderSystem.team.create.description.accept", player)).setLoreComponents(loreList).build(), event -> {
            if (name == null) {
                player.sendMessage(t.t("BuilderSystem.team.create.missing.name", player));
            } else {
                if (description == null) {
                    builderSystem.createTeam(String.valueOf(player.getUniqueId()), name);
                    teamCreate.getInventory().close();
                    // "Team " + name + " Wurde erstellt"
                    player.sendMessage(t.t("BuilderSystem.team.create.succses", player, Placeholder.parsed("teamName", name)));
                } else {
                    builderSystem.createTeam(String.valueOf(player.getUniqueId()), name, description);
                    teamCreate.getInventory().close();
                    player.sendMessage(t.t("BuilderSystem.team.create.succses", player, Placeholder.parsed("teamName", name)));
                }
            }


            event.setCancelled(true);

        }), 4, 0);


        teamCreate.getSlotsComponent().addPane(pane);
        teamCreate.show(player);
    }


    public static List<GuiItem> teamItemList(Player player) {
        Translation t = Main.getTranslation();

        List<GuiItem> guiItems = new ArrayList<>();

        JsonArray teamArray = teamObjeckt.getAsJsonArray(player.getName());

        for (JsonElement jsonElement : teamArray) {
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

            ArrayList<Component> loreList = new ArrayList<>();
            // ChatColor.GRAY + "ID: " + ChatColor.WHITE + ID
            loreList.add(t.t("BuilderSystem.team.list.team.id", player, Placeholder.parsed("ID", String.valueOf(ID))));
            // ChatColor.GRAY + "Team Owner: " + ChatColor.WHITE + owner
            loreList.add(t.t("BuilderSystem.team.list.team.owner", player, Placeholder.parsed("Owner", owner)));
            if (!(description.isEmpty())) {
                // ChatColor.GRAY + "Description: " + ChatColor.WHITE + description
                loreList.add(t.t("BuilderSystem.team.list.team.description", player, Placeholder.parsed("Description", description)));
            }
            if (!(stringList.isEmpty())) {
                // ChatColor.GRAY + "Team Member: " + ChatColor.WHITE + joinedString
                loreList.add(t.t("BuilderSystem.team.list.team.member", player, Placeholder.parsed("teamMember", joinedString)));
            }
            if (owner.equals(player.getName())) {
                // ChatColor.GRAY + "Name: " + ChatColor.WHITE + name
                ItemStack project = new ItemBuilder(Material.MAP).setDisplayName(t.t("BuilderSystem.team.list.team.name", player, Placeholder.parsed("Name", name))).setLoreComponents(loreList).build();

                GuiItem guiItem = new GuiItem(project, event -> {
                    event.setCancelled(true);
                    TeamMenu(ID, player);
                });
                guiItems.add(guiItem);
            } else {
                ItemStack project = new ItemBuilder(Material.PAPER).setDisplayName(t.t("BuilderSystem.team.list.team.name", player, Placeholder.parsed("Name", name))).setLoreComponents(loreList).build();

                GuiItem guiItem = new GuiItem(project, event -> {
                    event.setCancelled(true);
                    TeamMenu(ID, player);
                });
                guiItems.add(guiItem);
            }
        }
        return guiItems;
    }

    public static void TeamList(Player player) {
        Translation t = Main.getTranslation();
        Data data = Main.getData();

        ChestGui taskList = new ChestGui(5, data.setGuiName("BuilderSystem.team.team.list", player));
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
                // "Back to Main Menu"
                .setDisplayName(t.t("BuilderSystem.back.to.main", player))
                .build(), event -> Navigate(player)), 4, 0);
        taskList.addPane(taskPages);
        taskList.show(player);
    }

    public static void TeamMenu(int TeamID, Player player) {
        Translation t = Main.getTranslation();
        Data data = Main.getData();
        // Team Manager
        HopperGui teamMenu = new HopperGui(data.setGuiName("BuilderSystem.team.team.menu.team.mangae.gui", player));
        StaticPane pane = new StaticPane(0, 0, 5, 1);
        pane.setOnClick(event -> event.setCancelled(true));
        asyncload(TeamID, player);
        pane.fillWith(outlineintem);
        // player manager
        pane.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setDisplayName(t.t("BuilderSystem.team.team.menu.player.Manager", player)).build(), event -> {
            TeamPlayer(player, TeamID);
            event.setCancelled(true);
        }), 1, 0);
        //edit Team
        pane.addItem(new GuiItem(new ItemBuilder(Material.BOOK).setDisplayName(t.t("BuilderSystem.team.team.menu.edit.team", player)).build(), event -> {
            TeamEdit(TeamID, player);
            event.setCancelled(true);

        }), 3, 0);
        teamMenu.getSlotsComponent().addPane(pane);
        teamMenu.show(player);
    }


    public static void TeamEdit(int TeamID, Player player) {
        Translation t = Main.getTranslation();
        Data data = Main.getData();
        HopperGui teamEdit = new HopperGui(data.setGuiName("BuilderSystem.team.team.edit.gui", player));
        StaticPane pane = new StaticPane(0, 0, 5, 1);
        pane.fillWith(outlineintem);
        // name
        pane.addItem(new GuiItem(new ItemBuilder(Material.KNOWLEDGE_BOOK).setDisplayName(t.t("BuilderSystem.team.team.edit.name", player)).build(), event -> {
            editTeam(TeamID, player, true);
            event.setCancelled(true);
        }), 1, 0);
        // description
        pane.addItem(new GuiItem(new ItemBuilder(Material.BOOK).setDisplayName(t.t("BuilderSystem.team.team.edit.description", player)).build(), event -> {
            editTeam(TeamID, player, false);
            event.setCancelled(true);
        }), 3, 0);

        teamEdit.getSlotsComponent().addPane(pane);
        teamEdit.show(player);
    }

    public static List<GuiItem> TeamPlayerList(Player player, int teamID) {
        Translation t = Main.getTranslation();
        Data data = Main.getData();
        List<GuiItem> guiItems = new ArrayList<>();


        JsonObject team = teamUserArray.getAsJsonObject(player.getName());

        JsonArray players = team.getAsJsonArray("user_ids");
        List<String> stringList = new ArrayList<String>();


        String owner = team.get("owner_id").getAsString();


        ArrayList<Component> loreList1 = new ArrayList<>();
        loreList1.add(t.t("BuilderSystem.team.team.player.list.owner.lore1", player));
        loreList1.add(t.t("BuilderSystem.team.team.player.list.owner.lore2", player));
        String ownerName = Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName();
        // ownerName
        ItemStack TaskItem1 = new ItemBuilder(Material.PLAYER_HEAD).setHead(ownerName).setDisplayName(t.t("BuilderSystem.team.team.player.list.owner.name", player, Placeholder.parsed("OwnerName", ownerName))).setLoreComponents(loreList1).addID(teamID).build();
        GuiItem guiItem1 = new GuiItem(TaskItem1, event -> {
            // The owner cannot be managed
            player.sendMessage("");
            event.setCancelled(true);
        });
        guiItems.add(guiItem1);

        for (JsonElement jsonElement : players) {
            JsonObject uuid = jsonElement.getAsJsonObject();
            String pid = uuid.get("mc_uuid").getAsString();
            if (owner.equals(pid)) {

            } else {
                String user_name = Bukkit.getOfflinePlayer(UUID.fromString(pid)).getName();
                stringList.add(user_name);
            }
        }

        for (int i = 0; i < stringList.size(); i++) {

            ArrayList<Component> loreList = new ArrayList<>();
            loreList.add(t.t("BuilderSystem.team.team.player.list.player.lore1", player));
            loreList.add(t.t("BuilderSystem.team.team.player.list.player.lore2", player));
            ItemStack TaskItem = new ItemBuilder(Material.PLAYER_HEAD).setHead(stringList.get(i)).setDisplayName(t.t("BuilderSystem.team.team.player.list.owner.name", player, Placeholder.parsed("PlayerName", stringList.get(i)))).setLoreComponents(loreList).addID(teamID).build();
            @NotNull OfflinePlayer managetPlayer = Bukkit.getOfflinePlayer(stringList.get(i));
            GuiItem guiItem = new GuiItem(TaskItem, event -> {
                if (player.hasPermission("gigaclub_team.edit_user")) {
                    playerManager(teamID, player, managetPlayer);
                    event.setCancelled(true);
                } else {
                    player.sendMessage(t.t("builder_team.no_permission", player));
                }
                event.setCancelled(true);
            });
            guiItems.add(guiItem);
        }
        return guiItems;
    }

    public static void playerManager(int TeamID, Player player, OfflinePlayer managetPlayer) {
        Translation t = Main.getTranslation();
        Data data = Main.getData();
        BuilderSystem builderSystem = Main.getBuilderSystem();
        ChestGui playermanager = new ChestGui(3, data.setGuiName("BuilderSystem.team.player.manager.gui", player, Placeholder.parsed("managetPlayer", managetPlayer.getName())));
        StaticPane pane = new StaticPane(9, 3);
        pane.fillWith(outlineintem);


        pane.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHead(managetPlayer.getName()).setDisplayName(t.t("BuilderSystem.team.player.manager.player", player)).build()), 0, 0);
        // set team Group
        GuiItem addPermsGroup = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(10250).setDisplayName(t.t("BuilderSystem.team.player.manager.player.set.group", player)).build(), event -> {
            player.sendMessage("set user Group");
            event.setCancelled(true);
        });
        pane.addItem(addPermsGroup, 2, 1);

        GuiItem kickUser = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9348).setDisplayName(t.t("BuilderSystem.team.player.manager.kick.player", player, Placeholder.parsed("kicktPlayer", managetPlayer.getName()))).build(), event -> {
            DispenserGui confirm = new DispenserGui(data.setGuiName("BuilderSystem.team.player.manager.kick.player.confirm.gui", player, Placeholder.parsed("kicktPlayer", managetPlayer.getName())));
            StaticPane cpane = new StaticPane(3, 3);
            cpane.fillWith(outlineintem);
            GuiItem check = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(21774).setDisplayName(t.t("BuilderSystem.team.player.manager.kick.player.confirm", player, Placeholder.parsed("kicktPlayer", managetPlayer.getName()))).build(), event1 -> {
                builderSystem.kickMember(String.valueOf(player.getUniqueId()), TeamID, String.valueOf(managetPlayer.getUniqueId()));
                confirm.getInventory().close();
                player.sendMessage(t.t("BuilderSystem.team.player.manager.kick.player.got.kickt", player, Placeholder.parsed("kicktPlayer", managetPlayer.getName())));
                event1.setCancelled(true);
            });
            cpane.addItem(check, Slot.fromIndex(5));
            GuiItem stop = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(9382).setDisplayName(t.t("BuilderSystem.team.player.manager.kick.player.confirm.dont.kick", player, Placeholder.parsed("kicktPlayer", managetPlayer.getName()))).build(), event1 -> {
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
        Translation t = Main.getTranslation();
        Data data = Main.getData();


        ChestGui taskList = new ChestGui(6, data.setGuiName("BuilderSystem.team.player.list.gui", player));
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

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8784).setDisplayName(t.t("BuilderSystem.page.list.back", player)).build(), event -> {
            if (taskPages.getPage() > 0) {
                taskPages.setPage(taskPages.getPage() - 1);
                taskList.setTitle(data.setGuiName("BuilderSystem.team.player.list.gui.pages", player, Placeholder.parsed("page", String.valueOf(taskPages.getPage() + 1))));
                taskList.update();
            } else event.setCancelled(true);
        }), 2, 0);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8782).setDisplayName(t.t("BuilderSystem.page.list.next", player)).build(), event -> {
            if (taskPages.getPage() < taskPages.getPages() - 1) {
                taskPages.setPage(taskPages.getPage() + 1);
                taskList.setTitle(data.setGuiName("BuilderSystem.team.player.list.gui.pages", player, Placeholder.parsed("page", String.valueOf(taskPages.getPage() + 1))));
                taskList.update();
            } else event.setCancelled(true);
        }), 6, 0);
        GuiItem playerInvite = new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(21771).setDisplayName(t.t("BuilderSystem.team.player.list.invite.player", player)).build(), event -> {

            BuilderSystem builderSystem = Main.getBuilderSystem();
            AnvilGui invitePlayer = new AnvilGui(data.setGuiName("BuilderSystem.team.player.list.invite.player.gui", player));
            GuiItem slot1 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(t.t("BuilderSystem.team.player.list.invite.player.tipe.player.name", player)).build(), event1 -> {
                event1.setCancelled(true);
            });
            StaticPane pane1 = new StaticPane(1, 1);
            pane1.addItem(slot1, 0, 0);
            invitePlayer.getFirstItemComponent().addPane(pane1);

            invitePlayer.setCost((short) 0);
            StaticPane pane2 = new StaticPane(1, 1);

            GuiItem slot3 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(t.t("BuilderSystem.team.player.list.invite.player.click.to.invite", player)).build(), event1 -> {


                try {
                    if (data.MCplayerExists(invitePlayer.getRenameText())) {
                        String[] User = data.getMCPlayerInfo(invitePlayer.getRenameText());
                        if (data.checkIfPlayerExists(User[1])) {
                            builderSystem.inviteMember(String.valueOf(player.getUniqueId()), teamID, String.valueOf(Bukkit.getOfflinePlayer(invitePlayer.getRenameText()).getUniqueId()));

                            player.sendMessage(t.t("BuilderSystem.team.player.list.invite.player.invite.sent", player, Placeholder.parsed("invitetPlayer", User[1])));
                            TeamPlayer(player, teamID);
                            event1.setCancelled(true);
                        } else {

                            data.createPlayer(User[1], String.valueOf(UUID.fromString(User[0])));
                            builderSystem.inviteMember(String.valueOf(player.getUniqueId()), teamID, String.valueOf(UUID.fromString(User[0])));

                            player.sendMessage(t.t("BuilderSystem.team.player.list.invite.player.invite.if.first.join", player, Placeholder.parsed("invitetPlayer", User[1])));
                            TeamPlayer(player, teamID);
                            event1.setCancelled(true);
                        }
                    }
                } catch (IOException e) {
                    player.sendMessage(t.t("BuilderSystem.team.player.list.invite.player.is.not.player", player));
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
                .setDisplayName(t.t("BuilderSystem.back.to.team.select", player))
                .build(), event -> TeamList(player)), 4, 0);

        if (taskPages.getPages() == 1) {
            taskList.setTitle(data.setGuiName("BuilderSystem.team.player.list.players.teams.gui", player));
            StaticPane outline4 = new StaticPane(0, 5, 9, 1);
            outline4.fillWith(outlineintem);
            outline4.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(10298).setDisplayName(t.t("BuilderSystem.back.to.team.select", player)).build(), event -> TeamList(player)), 4, 0);
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
        Translation t = Main.getTranslation();
        Data data = Main.getData();

        AnvilGui editname = new AnvilGui(data.setGuiName("BuilderSystem.team.edit.team.name.gui", player));

        if (editName) {
            GuiItem slot1 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(t.t("BuilderSystem.team.edit.team.name.old", player)).build(), event -> {
                event.setCancelled(true);
            });
            StaticPane pane1 = new StaticPane(1, 1);
            pane1.addItem(slot1, 0, 0);
            editname.getFirstItemComponent().addPane(pane1);

        } else {
            editname.setTitle(data.setGuiName("BuilderSystem.team.edit.team.description", player));
            if (builderSystem.getTeam(teamID).get("description").getAsString().isEmpty()) {

                GuiItem slot1 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(t.t("BuilderSystem.team.edit.team.description.empty", player)).build(), event -> {
                    event.setCancelled(true);
                });
                StaticPane pane1 = new StaticPane(1, 1);
                pane1.addItem(slot1, 0, 0);
                editname.getFirstItemComponent().addPane(pane1);
            } else {
                String deskr = builderSystem.getTeam(teamID).get("description").getAsString();
                GuiItem slot1 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(t.t("BuilderSystem.team.edit.team.description.old", player, Placeholder.parsed("description", deskr))).build(), event -> {
                    event.setCancelled(true);
                });
                StaticPane pane1 = new StaticPane(1, 1);
                pane1.addItem(slot1, 0, 0);
                editname.getFirstItemComponent().addPane(pane1);
            }
        }

        editname.setCost((short) 0);
        StaticPane pane2 = new StaticPane(1, 1);
        if (editName) { // "Click to Save Name"
            GuiItem slot3 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(t.t("BuilderSystem.team.edit.team.name.save", player)).build(), event -> {
                builderSystem.editTeam(String.valueOf(player.getUniqueId()), teamID, editname.getRenameText());
                event.setCancelled(true);
                TeamMenu(teamID, player);
            });
            pane2.addItem(slot3, 0, 0);
            editname.getResultComponent().addPane(pane2);
        } else {
            GuiItem slot3 = new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(t.t("BuilderSystem.team.edit.team.name.save", player)).build(), event -> {
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
        Translation t = Main.getTranslation();


        JsonArray invits = builderSystem.getUserMemberToTeamInvitations(String.valueOf(player.getUniqueId()));
        for (JsonElement jsonElement : invits) {
            JsonObject invite = jsonElement.getAsJsonObject();

            int sender = invite.get("sender_team_id").getAsInt();


            ArrayList<Component> loreList = new ArrayList<>();
            loreList.add(t.t("BuilderSystem.team.invits.item.lore1", player));
            loreList.add(t.t("BuilderSystem.team.invits.item.lore2", player));
            // ChatColor.GRAY + "Invite From " + ChatColor.WHITE + builderSystem.getTeam(sender).get("name").getAsString()
            ItemStack invit = new ItemBuilder(Material.WHITE_WOOL).setDisplayName(t.t("BuilderSystem.team.invits.item.invite.from", player, Placeholder.parsed("inviteFrom", builderSystem.getTeam(sender).get("name").getAsString()))).setLoreComponents(loreList).build();
            GuiItem guiItem = new GuiItem(invit, event -> {
                //gui zum acceptation
                player.sendMessage("request menu ");
                event.setCancelled(true);
            });
            guiItems.add(guiItem);


        }

        return guiItems;
    }


    public static void TeamInvits(Player player) { //"Invites Page 1"
        Translation t = Main.getTranslation();
        Data data = Main.getData();

        ChestGui teamInits = new ChestGui(6, data.setGuiName("BuilderSystem.team.invits.list.gui", player));
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

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8784).setDisplayName(t.t("BuilderSystem.page.list.back", player)).build(), event -> {
            if (taskPages.getPage() > 0) {
                taskPages.setPage(taskPages.getPage() - 1);
                // "Task List Page " + (taskPages.getPage() + 1)
                teamInits.setTitle(data.setGuiName("BuilderSystem.team.invits.list.page", player, Placeholder.parsed("curentPage", String.valueOf(taskPages.getPage() + 1))));
                teamInits.update();
            } else event.setCancelled(true);
        }), 1, 0);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(8782).setDisplayName(t.t("BuilderSystem.page.list.next", player)).build(), event -> {
            if (taskPages.getPage() < taskPages.getPages() - 1) {
                taskPages.setPage(taskPages.getPage() + 1);
                teamInits.setTitle(data.setGuiName("BuilderSystem.team.invits.list.page", player, Placeholder.parsed("curentPage", String.valueOf(taskPages.getPage() + 1))));
                teamInits.update();
            } else event.setCancelled(true);
        }), 7, 0);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(10298).setDisplayName(t.t("BuilderSystem.back.to.team.gui", player)).build(), event -> teams(player)), 4, 0);
        navigation.fillWith(outlineintem);
        if (taskPages.getPages() == 1) {
            teamInits.setTitle("Invites");
            StaticPane outline4 = new StaticPane(0, 5, 9, 1);
            outline4.fillWith(outlineintem);
            outline4.addItem(new GuiItem(new ItemBuilder(Material.PLAYER_HEAD).setHeadDatabase(10298).setDisplayName(t.t("BuilderSystem.back.to.team.gui", player)).build(), event -> teams(player)), 4, 0);
            teamInits.addPane(outline4);
        } else {
            teamInits.addPane(navigation);
        }
        teamInits.addPane(taskPages);
        teamInits.show(player);
    }

}
