package xyz.oribuin.eternalmines.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalmines.manager.LocaleManager;
import xyz.oribuin.eternalmines.mine.Mine;

import java.util.List;

public class TeleportCommand extends RoseCommand {

    public TeleportCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, Mine mine) {
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        final Player player = (Player) context.getSender();

        // Teleport the player to the mine's spawn, using spigot's teleportation method if the server is not running paper.
        if (NMSUtil.isPaper() ? player.teleportAsync(mine.getSpawn()).isDone() : player.teleport(mine.getSpawn())) {
            locale.sendMessage(player, "command-teleport-success", StringPlaceholders.of("mine", mine.getId()));
            return;
        }

        locale.sendMessage(player, "command-teleport-failed");
    }

    @Override
    protected String getDefaultName() {
        return "teleport";
    }

    @Override
    protected List<String> getDefaultAliases() {
        return List.of("tp");
    }

    @Override
    public String getDescriptionKey() {
        return "command-teleport-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalmines.command.teleport";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
