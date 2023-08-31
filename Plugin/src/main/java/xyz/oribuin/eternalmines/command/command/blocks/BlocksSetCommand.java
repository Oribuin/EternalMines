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

public class BlocksSetCommand extends RoseSubCommand {

    public BlocksSetCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(@Inject CommandContext context, @Inject Mine mine, Material material, Double percentage) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        if (percentage == null || percentage < 0) {
            locale.sendMessage(context.getSender(), "command-blocks-set-invalid");
            return;
        }

        mine.getBlocks().put(material, percentage);
        this.rosePlugin.getManager(MineManager.class).saveMine(mine, true);

        locale.sendMessage(context.getSender(), "command-blocks-set-success",
                StringPlaceholders.of("mine", mine.getId(),
                        "material", material.name(),
                        "percentage", percentage)
        );
    }

    @Override
    protected String getDefaultName() {
        return "add";
    }

}
