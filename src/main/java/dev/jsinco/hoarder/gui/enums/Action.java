package dev.jsinco.hoarder.gui.enums;

import dev.jsinco.hoarder.gui.GUICreator;
import kotlin.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Action {

    OPEN,
    COMMAND,
    CLOSE,
    MESSAGE;

    public boolean executeAction(String string, Player player) {
        switch (this) {
            case OPEN -> {
                GUICreator guiCreator = new GUICreator(string);
                player.openInventory(guiCreator.getInventory());
            }

            case COMMAND -> {
                CommandSender sender = Bukkit.getConsoleSender();
                if (string.contains("-p")) {
                    string = string.replace("-p", "").trim();
                    sender = player;
                }
                Bukkit.dispatchCommand(sender, string);
            }

            case CLOSE -> player.closeInventory();

            case MESSAGE -> player.sendMessage(string);

            default -> {
                return false;
            }
        }
        return true;
    }

    public static Pair<Action, String> parseStringAction(String string) {
        if (!string.contains("[") || !string.contains("]")) return null;

        Action action;
        try {
            action = Action.valueOf(string.substring(string.indexOf('[') + 1, string.indexOf(']')).trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
        String updatedString = string.replace("[" + action.name() + "]", "").trim();
        return new Pair<>(action, updatedString);
    }
}
