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

public class CreateCommand extends RoseCommand {

    public CreateCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, String name, World world, Location spawn) {

        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        final MineManager manager = this.rosePlugin.getManager(MineManager.class);

        if (manager.getMine(name) != null) {
            locale.sendMessage(context.getSender(), "command-create-already-exists");
            return;
        }

        spawn.setWorld(world);

        Mine mine = new Mine(name.toLowerCase(), spawn);

        if (mine.create(this.rosePlugin)) {
            locale.sendMessage(context.getSender(), "command-create-success", StringPlaceholders.of("name", name));
            return;
        }

        locale.sendMessage(context.getSender(), "command-create-already-exists");
    }

    @Override
    protected String getDefaultName() {
        return "create";
    }

    @Override
    public String getDescriptionKey() {
        return "command-create-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalmines.create";
    }

}
