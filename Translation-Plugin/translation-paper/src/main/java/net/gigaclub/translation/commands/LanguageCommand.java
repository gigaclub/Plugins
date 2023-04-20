package net.gigaclub.translation.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.gigaclub.translation.Main;
import net.gigaclub.translation.Translation;
import net.gigaclub.translation.util.ComponentHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LanguageCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        String playerUUID = player.getUniqueId().toString();
        Translation t = Main.getTranslation();
        Gson gson = new Gson();
        JsonObject values;

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "set":
                    if (args.length > 1) {
                        TagResolver langTag = Placeholder.parsed("language", args[1]);
                        if (Main.getData().checkIfLanguageExists(args[1])) {
                            Main.getData().setLanguage(playerUUID, args[1]);
                            t.sendMessage("translation.command.language.set", player, langTag);
                        } else {
                            t.sendMessage("translation.command.language.does.not.exist", player, langTag);
                        }
                    } else {
                        t.sendMessage("translation.command.language.no.language.parameter", player);
                    }
                    break;
                case "list":
                    List<String> languages = Main.getData().getAvailableLanguages();
                    Component[] langComponents = languages.stream().map(s -> Component.text(s)).toArray(Component[]::new);
                    t.sendMessage("translation.command.language.list", player, TagResolver.resolver("languages", Tag.inserting(ComponentHelper.join(langComponents, Component.text(", ")))));
                    break;
                default:
                    t.sendMessage("translation.command.language.incorrect.parameter", player, Placeholder.parsed("wrongParameter", args[0]));
                    break;
            }
        } else {
            t.sendMessage("translation.command.language.no.parameters", player);
        }
        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("set");
            arguments.add("list");
            return arguments;
        }else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                List<String> languages = Main.getData().getAvailableLanguages();
                return languages;
            }
        }
        return null;
    }
}