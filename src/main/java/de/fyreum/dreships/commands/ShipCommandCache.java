package de.fyreum.dreships.commands;

import de.erethon.commons.command.DRECommand;
import de.erethon.commons.command.DRECommandCache;
import de.erethon.commons.javaplugin.DREPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

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

    public ShipCommandCache(DREPlugin plugin) {
        super(LABEL, plugin);
        this.plugin = plugin;

        addCommand(infoCommand);
        addCommand(createCommand);
        addCommand(deleteCommand);
        addCommand(saveCommand);
        addCommand(teleportCommand);
        addCommand(helpCommand);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> cmds = new ArrayList<>();
        for (DRECommand cmd : getCommands()) {
            if (sender.hasPermission(cmd.getPermission())) {
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
        return null;
    }
}
