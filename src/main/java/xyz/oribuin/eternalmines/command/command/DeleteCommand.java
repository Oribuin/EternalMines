package xyz.oribuin.eternalmines.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Location;
import org.bukkit.World;
import xyz.oribuin.eternalmines.manager.LocaleManager;
import xyz.oribuin.eternalmines.manager.MineManager;
import xyz.oribuin.eternalmines.mine.Mine;

public class DeleteCommand extends RoseCommand {

    public DeleteCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, String name, Mine mine) {

        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        final MineManager manager = this.rosePlugin.getManager(MineManager.class);

        if (manager.getMine(name) == null) {
            locale.sendMessage(context.getSender(), "command-delete-doesnt-exists");
            return;
        }

        if (manager.deleteMine(mine.getId())) {
            locale.sendMessage(context.getSender(), "command-delete-success", StringPlaceholders.of("mine", name));
            return;
        }

        locale.sendMessage(context.getSender(), "command-doesnt-already-exists");
    }

    @Override
    protected String getDefaultName() {
        return "delete";
    }

    @Override
    public String getDescriptionKey() {
        return "command-delete-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalmines.delete";
    }

}
