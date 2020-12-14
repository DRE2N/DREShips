package de.fyreum.dreships.commands;

import de.erethon.commons.command.DRECommand;
import de.erethon.commons.command.DRECommandCache;
import de.erethon.commons.javaplugin.DREPlugin;
import de.fyreum.dreships.util.PriceCalculationUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShipCommandCache extends DRECommandCache implements TabCompleter {

    public static final String LABEL = "dreships";
    DREPlugin plugin;

    public InfoCommand infoCommand = new InfoCommand();
    public CreateCommand createCommand = new CreateCommand();
    public DeleteCommand deleteCommand = new DeleteCommand();
    public SaveCommand saveCommand = new SaveCommand();
    public TeleportCommand teleportCommand = new TeleportCommand();
    public HelpCommand helpCommand = new HelpCommand();
    public ReloadCommand reloadCommand = new ReloadCommand();
    public ListCommand listCommand = new ListCommand();
    public CheckCommand checkCommand = new CheckCommand();
    public DisableCommand disableCommand = new DisableCommand();
    public EnableCommand enableCommand = new EnableCommand();
    public CalculateCommand calculateCommand = new CalculateCommand();

    public ShipCommandCache(DREPlugin plugin) {
        super(LABEL, plugin);
        this.plugin = plugin;

        addCommand(infoCommand);
        addCommand(createCommand);
        addCommand(deleteCommand);
        addCommand(saveCommand);
        addCommand(teleportCommand);
        addCommand(helpCommand);
        addCommand(reloadCommand);
        addCommand(listCommand);
        addCommand(checkCommand);
        addCommand(disableCommand);
        addCommand(enableCommand);
        addCommand(calculateCommand);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> cmds = new ArrayList<>();
        for (DRECommand cmd : getCommands()) {
            if (cmd.getPermission() != null && !cmd.getPermission().isEmpty() && sender.hasPermission(cmd.getPermission())) {
                cmds.add(cmd.getCommand());
            }
        }
        List<String> completes = new ArrayList<>();

        if(args.length == 1) {
            for(String string : cmds) {
                if(string.toLowerCase().startsWith(args[0])) completes.add(string);
            }
            return completes;
        }
        if (args.length == 2) {
            if (createCommand.getCommand().equalsIgnoreCase(args[0]) || createCommand.getAliases().contains(args[0])) {
                for(String string : PriceCalculationUtil.getTravelTypes()) {
                    if(string.toLowerCase().startsWith(args[1].toLowerCase())) completes.add(string);
                }
                return completes;
            }
            if (calculateCommand.getCommand().equalsIgnoreCase(args[0])) {
                for(String string : PriceCalculationUtil.getTravelTypes()) {
                    if(string.toLowerCase().startsWith(args[1].toLowerCase())) completes.add(string);
                }
                return completes;
            }
        }
        return null;
    }
}
