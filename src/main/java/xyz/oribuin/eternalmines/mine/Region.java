package xyz.oribuin.eternalmines.mine;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Region {

    private @Nullable Location pos1; // First position of the region
    private @Nullable Location pos2; // Second position of the region
    private int totalBlocks; // Total blocks in the region
    private List<Block> blocks;

    public Region(@Nullable Location pos1, @Nullable Location pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.blocks = new ArrayList<>();
    }

    public Region() {
        this(null, null);
    }

    /**
     * Fill the region with the specified blocks and their chances
     *
     * @param blocks The blocks to fill the region with
     */
    public void fill(Map<Material, Double> blocks) {
        if (this.pos1 == null || this.pos2 == null)
            return;

        // Check if the blocks map is empty or if all the blocks are air
        boolean onlyAir = blocks.isEmpty() || blocks.keySet().stream()
                .allMatch(material -> material == Material.AIR);

        if (onlyAir) {
            this.blocks.forEach(block -> block.setBlockData(Material.AIR.createBlockData()));
            this.loadBlocksInside();
            return;
        }

        double totalWeight = blocks.values().stream().mapToDouble(Double::doubleValue).sum();

        this.blocks.forEach(block -> {
            double random = Math.random() * totalWeight;
            double weightSum = 0;

            for (Map.Entry<Material, Double> entry : blocks.entrySet()) {
                weightSum += entry.getValue();
                if (random <= weightSum) {
                    block.setType(entry.getKey());
                    break;
                }
            }
        });

        this.loadBlocksInside(); // Update the blocks inside the region
    }

    public void loadBlocksInside() {
        if (pos1 == null || pos2 == null)
            return;

        List<Block> blocks = new ArrayList<>();
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(pos1.getWorld().getBlockAt(x, y, z));
                }
            }
        }

        this.blocks = blocks; // Set the blocks to the region
        this.totalBlocks = blocks.size(); // Set the total blocks in the region
    }

    /**
     * Get all the entities inside the region
     *
     * @return A list of all the entities inside the region
     */
    public List<LivingEntity> getEntitiesInside() {
        if (pos1 == null || pos2 == null)
            return new ArrayList<>();

        List<Chunk> chunks = new ArrayList<>();
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = minX; x <= maxX; x += 16) {
            for (int z = minZ; z <= maxZ; z += 16) {
                chunks.add(pos1.getWorld().getChunkAt(x >> 4, z >> 4));
            }
        }

        List<LivingEntity> entities = new ArrayList<>();
        for (Chunk chunk : chunks) {
            for (Entity entity : chunk.getEntities()) {
                // Check if the entity is a player
                if (!(entity instanceof LivingEntity livingEntity))
                    continue;

                // Check if the player is inside the region
                if (!this.isInside(livingEntity.getLocation()))
                    continue;

                entities.add(livingEntity);
            }
        }

        return entities;
    }

    /**
     * Check if location is in the region
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
    }

    public @Nullable Location getPos2() {
        return this.pos2;
    }

    public void setPos2(@Nullable Location pos2) {
        this.pos2 = pos2;
    }

    public List<Block> getBlocks() {
        return this.blocks;
    }

    public int getTotalBlocks() {
        return this.totalBlocks;
    }
}