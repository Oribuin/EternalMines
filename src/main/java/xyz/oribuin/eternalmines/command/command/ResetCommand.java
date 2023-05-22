package xyz.oribuin.eternalmines.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalmines.manager.LocaleManager;
import xyz.oribuin.eternalmines.mine.Mine;

import java.util.List;

public class ResetCommand extends RoseCommand {

    public ResetCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, Mine mine) {
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        if (mine.reset()) {
            locale.sendMessage(context.getSender(), "command-reset-success", StringPlaceholders.of("mine", mine.getId()));
            return;
        }

        locale.sendMessage(context.getSender(), "command-reset-failed", StringPlaceholders.of("mine", mine.getId()));
    }

    @Override
    protected String getDefaultName() {
        return "reset";
    }

    @Override
    protected List<String> getDefaultAliases() {
        return List.of("restart");
    }

    @Override
    public String getDescriptionKey() {
        return "command-reset-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalmines.command.reset";
    }

}
