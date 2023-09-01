package xyz.oribuin.eternalmines.nms;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface NMSHandler {

    void setBlock(final Location location, final Material type);

    Material getBlock(final Location location);

    void update(final World world);

}

