package xyz.oribuin.eternalmines.command.command.blocks;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.Inject;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Material;
import xyz.oribuin.eternalmines.manager.LocaleManager;
import xyz.oribuin.eternalmines.manager.MineManager;
import xyz.oribuin.eternalmines.mine.Mine;

public class BlocksRemoveCommand extends RoseSubCommand {

    public BlocksRemoveCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(@Inject CommandContext context, @Inject Mine mine, Material material) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        if (!mine.getBlocks().containsKey(material)) {
            locale.sendMessage(context.getSender(), "command-blocks-remove-invalid");
            return;
        }

        mine.getBlocks().remove(material);
        this.rosePlugin.getManager(MineManager.class).saveMine(mine, true);

        locale.sendMessage(context.getSender(), "command-blocks-remove-success",
                StringPlaceholders.of("mine", mine.getId(),
                        "material", material.name())
        );
    }

    @Override
    protected String getDefaultName() {
        return "remove";
    }

}
