package xyz.oribuin.eternalmines.command.command.edit;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.RoseSubCommand;
import dev.rosewood.rosegarden.command.framework.annotation.Inject;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalmines.manager.LocaleManager;
import xyz.oribuin.eternalmines.manager.MineManager;
import xyz.oribuin.eternalmines.mine.Mine;
import xyz.oribuin.eternalmines.util.MineUtils;

public class EditSpawnCommand extends RoseSubCommand {

    public EditSpawnCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(@Inject CommandContext context, @Inject Mine mine, World world, @Optional Location location) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        Location spawn = location;

        if (spawn == null && context.getSender() instanceof Player player) {
            spawn = player.getLocation();
        }

        if (spawn == null || spawn.getWorld() == null) {
            locale.sendMessage(context.getSender(), "command-edit-spawn-invalid");
            return;
        }

        spawn.setWorld(world);
        mine.setSpawn(spawn);

        this.rosePlugin.getManager(MineManager.class).saveMine(mine, true);
        locale.sendMessage(context.getSender(), "command-edit-spawn-success", StringPlaceholders.of(
                "mine", mine.getId(),
                "location", MineUtils.formatLocation(spawn) + " (" + spawn.getWorld().getName() + ")"
        ));
    }


    @Override
    protected String getDefaultName() {
        return "spawn";
    }

}
