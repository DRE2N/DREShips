package de.fyreum.dreships.config;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.config.Message;
import de.erethon.commons.config.MessageHandler;
import de.fyreum.dreships.DREShips;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum ShipMessage implements Message {

    PREFIX("prefix"),
    WARN_SUFFOCATION("warn.suffocation"),
    WARN_DISABLED_SIGN("warn.disabledSign"),
    CMD_CACHE_EMPTY("cmd.cacheEmpty"),
    CMD_CALCULATE_MULTIPLIER_INVALID("cmd.calculate.multiplierInvalid"),
    CMD_CALCULATE_SUCCESS("cmd.calculate.success"),
    CMD_CHECK_SIGN_IS_CORRECT("cmd.check.signIsCorrect"),
    CMD_CHECK_SIGN_WAS_NOT_LISTED("cmd.check.signWasNotListed"),
    CMD_CHECK_DESTINATION_WAS_NOT_LISTED("cmd.check.destinationWasNotListed"),
    CMD_CREATE_SUCCESS("cmd.create.success"),
    CMD_DELETE_SUCCESS("cmd.delete.success"),
    CMD_DISABLE_SUCCESS("cmd.disable.success"),
    CMD_ENABLE_SUCCESS("cmd.enable.success"),
    CMD_INFO_TRAVEL_SIGN("cmd.info.travelSign"),
    CMD_MESSAGE_SUCCESS("cmd.message.success"),
    CMD_RELOAD_SUCCESS("cmd.reload.success"),
    CMD_REMOVE_MESSAGE_SUCCESS("cmd.removeMessage.success"),
    CMD_RENAME_SUCCESS("cmd.rename.success"),
    CMD_SAVE_ALREADY_SIGN("cmd.save.alreadySign"),
    CMD_SAVE_SUCCESS("cmd.save.success"),
    CMD_SET_COOLDOWN_SUCCESS("cmd.setCooldown.success"),
    CMD_SET_PRICE_SUCCESS("cmd.setPrice.success"),
    CMD_TP_SUGGESTION("cmd.tp.suggestion"),
    CMD_TP_HOVER_TEXT("cmd.tp.hoverText"),
    CMD_TP_NOT_WHITELISTED("cmd.tp.notWhitelisted"),
    ERROR_PRICE_INVALID("error.price.invalid"),
    ERROR_TARGET_BLOCK_INVALID("error.target.invalid"),
    ERROR_TARGET_NO_SIGN("error.target.noSign"),
    ERROR_TARGET_NO_TRAVEL_SIGN("error.target.noTravelSign"),
    ERROR_BREAK_DENIED("error.break.denied"),
    ERROR_MISSING_ARGUMENTS("error.missingArguments"),
    ERROR_NO_MONEY("error.money"),
    TP_MOVE_CANCEL("tp.moveCancel"),
    TP_SUCCESS("tp.success"),
    TP_TAX_MESSAGE("tp.taxMessage"),
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
        if (sender == null) {
            return;
        }
        MessageUtil.sendMessage(sender, this.getMessage(args));
    }

    public void sendActionBar(Player player, String... args) {
        MessageUtil.sendActionBarMessage(player, this.getMessage(args));
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
