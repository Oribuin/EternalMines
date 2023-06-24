package xyz.oribuin.eternalmines.command.command.edit;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.Inject;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.eternalmines.manager.LocaleManager;
import xyz.oribuin.eternalmines.manager.MineManager;
import xyz.oribuin.eternalmines.mine.Mine;

public class EditDelayCommand extends RoseSubCommand {

    public EditDelayCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(@Inject CommandContext context, @Inject Mine mine, int seconds) {
        mine.setResetTime(seconds);

        this.rosePlugin.getManager(MineManager.class).saveMine(mine, true);
        this.rosePlugin.getManager(LocaleManager.class)
                .sendMessage(
                        context.getSender(),
                        "command-edit-delay-success",
                        StringPlaceholders.of("mine", mine.getId(),
                                "delay", seconds)
                );
    }

    @Override
    protected String getDefaultName() {
        return "percentage";
    }

}
