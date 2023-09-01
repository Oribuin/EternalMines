package xyz.oribuin.eternalmines.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.List;

public class WorldArgumentHandler extends RoseCommandArgumentHandler<World> {

    public WorldArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, World.class);
    }

    @Override
    protected World handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        String input = argumentParser.next();
        World world = Bukkit.getWorld(input);

        if (world == null)
            throw new HandledArgumentException("argument-handler-world", StringPlaceholders.of("input", input));

        return world;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();

        return Bukkit.getWorlds().stream().map(World::getName).toList();
    }

}
