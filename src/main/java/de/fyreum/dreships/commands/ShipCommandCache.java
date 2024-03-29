package de.fyreum.dreships.commands;

import de.erethon.bedrock.command.ECommand;
import de.erethon.bedrock.command.ECommandCache;
import de.erethon.bedrock.plugin.EPlugin;
import de.fyreum.dreships.util.PriceCalculationUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShipCommandCache extends ECommandCache implements TabCompleter {

    public static final String LABEL = "dreships";
    EPlugin plugin;

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
    public SetCooldownCommand activateCooldownCommand = new SetCooldownCommand();
    public MessageCommand messageCommand = new MessageCommand();
    public RemoveMessageCommand removeMessageCommand = new RemoveMessageCommand();
    public RenameCommand renameCommand = new RenameCommand();
    public SetPriceCommand setPriceCommand = new SetPriceCommand();

    public ShipCommandCache(EPlugin plugin) {
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
        addCommand(activateCooldownCommand);
        addCommand(messageCommand);
        addCommand(removeMessageCommand);
        addCommand(renameCommand);
        addCommand(setPriceCommand);
    }

    @Override
    public void register(JavaPlugin plugin) {
        super.register(plugin);
        plugin.getCommand(LABEL).setTabCompleter(this);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> cmds = new ArrayList<>();
        for (ECommand cmd : getCommands()) {
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
        if (args.length == 3) {
            if (createCommand.getCommand().equalsIgnoreCase(args[0]) || createCommand.getAliases().contains(args[0])) {
                if ("true".toLowerCase().startsWith(args[2].toLowerCase())) completes.add("true");
                if ("false".toLowerCase().startsWith(args[2].toLowerCase())) completes.add("false");
            }
            return completes;
        }
        return completes;
    }
}
