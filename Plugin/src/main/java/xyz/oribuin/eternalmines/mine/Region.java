package xyz.oribuin.eternalmines.mine;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalmines.nms.NMSAdapter;
import xyz.oribuin.eternalmines.nms.NMSHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Region {

    private final List<Location> locations; // All the locations in the region
    private @Nullable World world;
    private @Nullable Location pos1; // First position of the region
    private @Nullable Location pos2; // Second position of the region
    private int totalBlocks; // Total blocks in the region

    public Region(@Nullable World world, @Nullable Location pos1, @Nullable Location pos2) {
        this.world = world;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.locations = new ArrayList<>();
        this.cacheLocations(); // Cache the locations
        this.totalBlocks = this.locations.size();
    }

    public Region() {
        this(null, null, null);
    }

    public void cacheLocations() {
        if (this.world == null || this.pos1 == null || this.pos2 == null)
            return;

        this.locations.clear(); // Clear the locations
        this.totalBlocks = 0; // Reset the total blocks

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    this.totalBlocks++;
                    this.locations.add(this.world.getBlockAt(x, y, z).getLocation());
                }
            }
        }
    }

    /**
     * Fill the region with the specified blocks and their chances
     *
     * @param blocks The blocks to fill the region with
     */
    public void fill(Map<Material, Double> blocks) {
        if (this.world == null || this.pos1 == null || this.pos2 == null)
            return;

        // Don't bother if the blocks are empty or all air
        if (blocks.isEmpty() || blocks.keySet().stream().allMatch(Material::isAir))
            return;

        final double totalWeight = blocks.values().stream().mapToDouble(Double::doubleValue).sum();

        for (final Block block : this.locations.stream().map(Location::getBlock).toList()) {
            final double random = Math.random() * totalWeight;
            double weightSum = 0;

            for (final Map.Entry<Material, Double> entry : blocks.entrySet()) {
                weightSum += entry.getValue();
                if (random <= weightSum) {
                    NMSAdapter.getHandler().setBlock(block.getLocation(), entry.getKey());
                    break;
                }
            }
        }

        // Update the blocks
        NMSAdapter.getHandler().update(this.world);
    }

    /**
     * Get all the players inside a region
     *
     * @return A list of all the players inside the region
     */
    public List<Player> getPlayersInside() {
        if (pos1 == null || pos2 == null)
            return new ArrayList<>();

        List<Player> players = new ArrayList<>();
        for (final Player player : Bukkit.getOnlinePlayers()) {
            // Check if the player is inside the region
            if (!this.isInside(player.getLocation()))
                continue;

            players.add(player);
        }

        return players;
    }

    /**
     * Check if the location is in the region
     *
     * @param location The location to check
     * @return Whether the location is in the region
     */
    public boolean isInside(Location location) {
        // Check if the position is null
        if (this.pos1 == null || this.pos2 == null)
            return false;

        // Check if the location is inside the world of the region
        if (location.getWorld() != this.pos1.getWorld() || location.getWorld() != this.pos2.getWorld())
            return false;

        // Declare location x, y, z
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        // Check if the location is inside the region
        return x >= Math.min(this.pos1.getX(), this.pos2.getX()) && x <= Math.max(this.pos1.getX(), this.pos2.getX()) &&
                y >= Math.min(this.pos1.getY(), this.pos2.getY()) && y <= Math.max(this.pos1.getY(), this.pos2.getY()) &&
                z >= Math.min(this.pos1.getZ(), this.pos2.getZ()) && z <= Math.max(this.pos1.getZ(), this.pos2.getZ());
    }

    public @Nullable Location getPos1() {
        return this.pos1;
    }

    public void setPos1(@Nullable Location pos1) {
        this.pos1 = pos1;

        this.cacheLocations(); // Load the locations
    }

    public @Nullable Location getPos2() {
        return this.pos2;
    }

    public void setPos2(@Nullable Location pos2) {
        this.pos2 = pos2;

        this.cacheLocations(); // Load the locations
    }

    public int getTotalBlocks() {
        return this.totalBlocks;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public @Nullable World getWorld() {
        return world;
    }

    public void setWorld(@Nullable World world) {
        this.world = world;

        this.cacheLocations(); // Load the locations
    }

}