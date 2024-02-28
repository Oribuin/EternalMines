package xyz.oribuin.eternalmines.mine;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalmines.EternalMines;
import xyz.oribuin.eternalmines.manager.ConfigurationManager.Setting;
import xyz.oribuin.eternalmines.util.ChunkPosition;
import xyz.oribuin.eternalmines.util.MineUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    public void fill(Map<Material, Double> blocks, boolean resetAll) {
        if (this.world == null || this.pos1 == null || this.pos2 == null)
            return;

        // Don't bother if the blocks are empty or all air
        if (blocks.isEmpty() || blocks.keySet().stream().allMatch(Material::isAir))
            return;

        FillType fillType = MineUtils.getEnum(FillType.class, Setting.FILL_TYPE.getString(), FillType.LAYERED);
        switch (fillType) {
            case LAYERED -> this.fillLayered(blocks, resetAll);
            case WHOLE -> this.fillEntire(blocks, resetAll);
            case CHUNKED -> this.fillChunked(blocks, resetAll);
        }
    }

    /**
     * Fill the entire region all at once (Fastest but least performant method)
     *
     * @param blocks The blocks to fill the region with
     */
    public void fillEntire(Map<Material, Double> blocks, boolean resetAll) {
        double totalWeight = blocks.values().stream().mapToDouble(Double::doubleValue).sum();

        for (Block block : this.locations.stream().map(Location::getBlock).toList()) {
            double random = Math.random() * totalWeight;
            double weightSum = 0;

            // If we're only resetting blocks that are air, skip the block if it's not air
            if (!resetAll && !block.getType().isAir()) continue;

            for (Map.Entry<Material, Double> entry : blocks.entrySet()) {
                weightSum += entry.getValue();
                if (random <= weightSum) {
                    MineUtils.setLazyBlock(block, entry.getKey());
                    break;
                }
            }
        }
    }

    /**
     * Fill the entire region layer by layer (Slowest but most performant method)
     *
     * @param blocks The blocks to fill the region with
     */
    public void fillLayered(Map<Material, Double> blocks, boolean resetAll) {
        AtomicInteger layer = new AtomicInteger(0);

        if (this.pos1 == null || this.pos2 == null)
            return;

        int minimumY = Math.min(this.pos1.getBlockY(), this.pos2.getBlockY());
        int maximumY = Math.max(this.pos1.getBlockY(), this.pos2.getBlockY());

        Map<Integer, List<Block>> blocksByLayer = new HashMap<>();
        for (int i = minimumY - 1; i <= maximumY; i++) {
            int finalI = i;
            blocksByLayer.put(i, this.locations.stream()
                    .filter(loc -> loc.getBlockY() == finalI)
                    .map(Location::getBlock)
                    .collect(Collectors.toList())
            );
        }

        Bukkit.getScheduler().runTaskTimer(EternalMines.getInstance(), bukkitTask -> {
            if (blocks.isEmpty()) {
                bukkitTask.cancel();
                return;
            }

            if (layer.get() > maximumY) {
                bukkitTask.cancel();
                return;
            }

            List<Block> locations = blocksByLayer.get(layer.getAndIncrement());
            for (Block block : locations) {
                double random = Math.random() * blocks.values().stream().mapToDouble(Double::doubleValue).sum();
                double weightSum = 0;

                // If we're only resetting blocks that are air, skip the block if it's not air
                if (!resetAll && !block.getType().isAir()) continue;

                for (Map.Entry<Material, Double> entry : blocks.entrySet()) {
                    weightSum += entry.getValue();
                    if (random <= weightSum) {
                        MineUtils.setLazyBlock(block, entry.getKey());
                        break; // Break the Map.Entry<Material, Double> entry loop
                    }
                }
            }
        }, 0, Setting.LAYER_FILL_DELAY.getLong());
    }

    /**
     * Fill an entire region chunk by chunk
     *
     * @param blocks The blocks to fill the region with
     */
    public void fillChunked(Map<Material, Double> blocks, boolean resetAll) {
        if (this.pos1 == null || this.pos2 == null)
            return;

        List<ChunkPosition> chunks = new ArrayList<>(this.locations.stream()
                .map(ChunkPosition::fromChunk)
                .distinct()
                .toList()
        );

        AtomicInteger index = new AtomicInteger(0);
        Bukkit.getScheduler().runTaskTimer(EternalMines.getInstance(), task -> {
            int maxIndex = chunks.size() - 1;

            if (index.get() > maxIndex) {
                task.cancel();
                return;
            }

            ChunkPosition chunk = chunks.get(index.getAndIncrement());
            List<Block> locations = this.locations.stream()
                    .filter(loc -> ChunkPosition.fromChunk(loc.getChunk()).equals(chunk))
                    .map(Location::getBlock)
                    .toList();

            for (Block block : locations) {
                double random = Math.random() * blocks.values().stream().mapToDouble(Double::doubleValue).sum();
                double weightSum = 0;

                // If we're only resetting blocks that are air, skip the block if it's not air
                if (!resetAll && !block.getType().isAir()) continue;

                for (Map.Entry<Material, Double> entry : blocks.entrySet()) {
                    weightSum += entry.getValue();
                    if (random <= weightSum) {
                        MineUtils.setLazyBlock(block, entry.getKey());
                        break;
                    }
                }
            }
        }, 0, Setting.CHUNKED_FILL_DELAY.getLong());
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
        for (Player player : Bukkit.getOnlinePlayers()) {
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