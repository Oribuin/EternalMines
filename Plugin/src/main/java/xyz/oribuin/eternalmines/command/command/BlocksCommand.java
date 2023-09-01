package xyz.oribuin.eternalmines.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import xyz.oribuin.eternalmines.command.command.blocks.BlocksClearCommand;
import xyz.oribuin.eternalmines.command.command.blocks.BlocksRemoveCommand;
import xyz.oribuin.eternalmines.command.command.blocks.BlocksSetCommand;
import xyz.oribuin.eternalmines.mine.Mine;

public class BlocksCommand extends RoseCommand {

    public BlocksCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent,
                BlocksRemoveCommand.class,
                BlocksClearCommand.class,
                BlocksSetCommand.class
        );
    }

    @RoseExecutable
    public void execute(CommandContext context, Mine mine, RoseSubCommand command) {
        // Empty
    }

    @Override
    protected String getDefaultName() {
        return "blocks";
    }

    @Override
    public String getDescriptionKey() {
        return "command-blocks-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalmines.edit";
    }

}
