package xyz.oribuin.eternalmines.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.eternalmines.manager.LocaleManager;
import xyz.oribuin.eternalmines.manager.MineManager;

public class ListCommand extends RoseCommand {

    public ListCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        MineManager manager = this.rosePlugin.getManager(MineManager.class);

        locale.sendSimpleMessage(context.getSender(), "command-list-header");
        manager.getMines().values().forEach(mine -> {
            StringPlaceholders placeholders = StringPlaceholders.builder("mine", mine.getId())
                    .add("world", mine.getWorld().getName())
                    .add("spawn", String.format("%.2f, %.2f, %.2f", mine.getSpawn().getX(), mine.getSpawn().getY(), mine.getSpawn().getZ()))
                    .add("reset-percent", mine.getResetPercentage())
                    .add("reset-delay", mine.getResetTime())
                    .add("blocks", mine.getBlocks().size())
                    .build();

            locale.sendSimpleMessage(context.getSender(), "command-list-format", placeholders);
        });
    }

    @Override
    protected String getDefaultName() {
        return "list";
    }

    @Override
    public String getDescriptionKey() {
        return "command-list-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalmines.list";
    }

}
