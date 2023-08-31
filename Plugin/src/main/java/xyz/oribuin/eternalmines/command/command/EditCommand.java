package xyz.oribuin.eternalmines.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import xyz.oribuin.eternalmines.command.command.edit.EditDelayCommand;
import xyz.oribuin.eternalmines.command.command.edit.EditPercentageCommand;
import xyz.oribuin.eternalmines.command.command.edit.EditRegionCommand;
import xyz.oribuin.eternalmines.command.command.edit.EditSpawnCommand;
import xyz.oribuin.eternalmines.mine.Mine;

public class EditCommand extends RoseCommand {

    public EditCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent,
                EditDelayCommand.class,
                EditPercentageCommand.class,
                EditRegionCommand.class,
                EditSpawnCommand.class
        );
    }

    @RoseExecutable
    public void execute(CommandContext context, Mine mine, RoseSubCommand command) {
        // Empty method
    }

    @Override
    protected String getDefaultName() {
        return "edit";
    }

    @Override
    public String getDescriptionKey() {
        return "command-edit-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalmines.edit";
    }

}
