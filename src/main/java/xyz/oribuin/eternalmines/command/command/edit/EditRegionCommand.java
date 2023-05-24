package xyz.oribuin.eternalmines.command.command.edit;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.Inject;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Location;
import org.bukkit.World;
import xyz.oribuin.eternalmines.manager.LocaleManager;
import xyz.oribuin.eternalmines.manager.MineManager;
import xyz.oribuin.eternalmines.mine.Mine;
import xyz.oribuin.eternalmines.mine.Region;

public class EditRegionCommand extends RoseSubCommand {

    public EditRegionCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(@Inject CommandContext context, @Inject Mine mine, World world, Location pos1, Location pos2) {

        // Set the world of the locations.
        pos1.setWorld(world);
        pos2.setWorld(world);
        mine.setRegion(new Region(pos1, pos2));

        this.rosePlugin.getManager(MineManager.class).saveMine(mine);
        this.rosePlugin.getManager(LocaleManager.class)
                .sendMessage(
                        context.getSender(),
                        "command-edit-region-success",
                        StringPlaceholders.of("mine", mine.getId())
                );
    }

    @Override
    protected String getDefaultName() {
        return "region";
    }

}
