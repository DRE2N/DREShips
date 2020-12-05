package de.fyreum.dreships.config;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.config.Message;
import de.erethon.commons.config.MessageHandler;
import de.fyreum.dreships.DREShips;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.md_5.bungee.api.ChatColor.translateAlternateColorCodes;

public enum ShipMessage implements Message {

    DS("name"),
    PREFIX("prefix"),
    WARN_SUFFOCATION("warn.suffocation"),
    WARN_VAULT("warn.vault"),
    CMD_CACHE_EMPTY("cmd.cacheEmpty"),
    CMD_CREATE_START("cmd.create.start"),
    CMD_CREATE_SUCCESS("cmd.create.success"),
    CMD_DELETE_SUCCESS("cmd.delete.success"),
    CMD_HELP("cmd.help"),
    CMD_INFO_TRAVEL_SIGN("cmd.info.travelSign"),
    CMD_INFO_NO_SIGN("cmd.info.noSign"),
    CMD_SAVE_ALREADY_SIGN("cmd.save.alreadySign"),
    CMD_SAVE_ALREADY_CACHED("cmd.save.alreadyCached"),
    CMD_SAVE_SUCCESS("cmd.save.success"),
    CMD_TP_SUGGESTION("cmd.tp.suggestion"),
    ERROR_PRICE_INVALID("error.price.invalid"),
    ERROR_TARGET_BLOCK_INVALID("error.target.invalid"),
    ERROR_TARGET_NO_SIGN("error.target.noSign"),
    ERROR_BREAK_DENIED("error.break.denied"),
    ERROR_MISSING_ARGUMENTS("error.missingArguments"),
    ERROR_NO_MONEY("error.money"),
    TP_SUCCESS("tp.success"),
    SIGN_LINE_ONE("sign.lineOne"),
    SIGN_LINE_TWO("sign.lineTwo"),
    SIGN_LINE_THREE("sign.lineThree"),
    SIGN_LINE_FOUR("sign.lineFour");

    private final String path;

    ShipMessage(String path) {
        this.path = path;
    }

    @Override
    public String getMessage() {
        if (this.getMessageHandler().getMessage(this) == null) {
            return "Invalid Message at " + getPath();
        }
        return this.getMessageHandler().getMessage(this);
    }
    @Override
    public String getMessage(String... args) {
        if (this.getMessageHandler().getMessage(this, args) == null) {
            return "Invalid Message at " + getPath();
        }
        return this.getMessageHandler().getMessage(this, args);
    }

    public void sendMessage(CommandSender sender, String... args) {
        MessageUtil.sendMessage(sender, this.getMessage(args));
    }

    @Override
    public MessageHandler getMessageHandler() {
        return DREShips.getInstance().getMessageHandler();
    }

    @Override
    public String getPath() {
        return path;
    }
}
