package xyz.oribuin.eternalmines.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LocationArgumentHandler extends RoseCommandArgumentHandler<Location> {

    public LocationArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Location.class);
    }

    @Override
    protected Location handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        String input = argumentParser.next();
        if (input == null)
            throw new HandledArgumentException("Expected a location, but found nothing.");

        String[] split = input.split(",");
        if (split.length < 3)
            throw new HandledArgumentException("Expected a location, but found nothing.");

        try {
            double x = Double.parseDouble(split[0]);
            double y = Double.parseDouble(split[1]);
            double z = Double.parseDouble(split[2]);
            String worldName = split.length > 3 ? split[3] : null;

            return new Location(worldName == null ? null : Bukkit.getWorld(worldName), x, y, z);
        } catch (NumberFormatException ex) {
            throw new HandledArgumentException("Expected a location, but found nothing.");
        }
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();

        CommandSender sender = argumentParser.getContext().getSender();
        if (sender == null) return null;

        if (!(sender instanceof Player player))
            return List.of("0,0,0");

        Location location = player.getLocation();

        Block targetBlock = player.getTargetBlockExact(10);
        if (targetBlock != null && !targetBlock.getType().isAir())
            return List.of(String.format("%d,%d,%d", targetBlock.getX(), targetBlock.getY(), targetBlock.getZ()));

        return List.of(
                String.format("%d,%d,%d", location.getBlockX(), location.getBlockY(), location.getBlockZ())
        );
    }
}
