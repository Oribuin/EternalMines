package xyz.oribuin.eternalmines.command.command.blocks;

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

public class BlocksClearCommand extends RoseSubCommand {

    private boolean isConfirming = false;

    public BlocksClearCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(@Inject CommandContext context, @Inject Mine mine) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        if (this.isConfirming) {
            mine.getBlocks().clear();
            this.rosePlugin.getManager(MineManager.class).saveMine(mine, true);

            locale.sendMessage(context.getSender(), "command-blocks-clear-success",
                    StringPlaceholders.of("mine", mine.getId())
            );

            this.isConfirming = false;
            return;
        }

        locale.sendMessage(context.getSender(), "command-blocks-clear-confirm",
                StringPlaceholders.of("mine", mine.getId())
        );

        this.isConfirming = true;
    }

    @Override
    protected String getDefaultName() {
        return "clear";
    }

}
