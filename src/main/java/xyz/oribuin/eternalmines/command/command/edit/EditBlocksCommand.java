package xyz.oribuin.eternalmines.command.command.edit;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.Inject;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Material;
import xyz.oribuin.eternalmines.manager.LocaleManager;
import xyz.oribuin.eternalmines.manager.MineManager;
import xyz.oribuin.eternalmines.mine.Mine;

public class EditBlocksCommand extends RoseSubCommand {

    public EditBlocksCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(@Inject CommandContext context, @Inject Mine mine, EditType type, Material material, @Optional Double percentage) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        switch (type) {
            case SET -> {
                if (percentage == null) {
                    locale.sendMessage(context.getSender(), "command-edit-blocks-set-invalid");
                    return;
                }

                mine.getBlocks().put(material, percentage);
                this.rosePlugin.getManager(MineManager.class).saveMine(mine);

                locale.sendMessage(context.getSender(), "command-edit-blocks-set-success",
                        StringPlaceholders.of("mine", mine.getId(),
                                "material", material.name(),
                                "percentage", percentage)
                );
            }

            case REMOVE -> {

                if (!mine.getBlocks().containsKey(material)) {
                    locale.sendMessage(context.getSender(), "command-edit-blocks-remove-invalid",
                            StringPlaceholders.of("mine", mine.getId(), "material", material.name())
                    );

                    return;
                }

                mine.getBlocks().remove(material);
                this.rosePlugin.getManager(MineManager.class).saveMine(mine);

                locale.sendMessage(context.getSender(), "command-edit-blocks-remove-success",
                        StringPlaceholders.of("mine", mine.getId(), "material", material.name())
                );
            }
        }
    }

    @Override
    protected String getDefaultName() {
        return "blocks";
    }

}
