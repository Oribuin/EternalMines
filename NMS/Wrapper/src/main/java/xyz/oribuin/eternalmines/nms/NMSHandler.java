package xyz.oribuin.eternalmines.nms;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Map;

public interface NMSHandler {

    /**
     * Update a map of blocks to a specific material.
     *
     * @param toUpdate The map of locations and materials to update.
     */
    void update(Map<Location, Material> toUpdate);


}

