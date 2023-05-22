package xyz.oribuin.eternalmines.mine;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalmines.EternalMines;
import xyz.oribuin.eternalmines.manager.MineManager;
import xyz.oribuin.eternalmines.util.MineUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Mine {

    private final @NotNull String id; // Name of the mine
    private @NotNull Location spawn; // Spawn of the mine (Where players teleport to)
    private @NotNull Region region; // Region of the mine (Where players can mine)
    private @NotNull Map<Material, Double> blocks; // Block material and chance to spawn
    private double resetPercentage; // % of blocks that need to be mined to reset the mine
    private long resetTime; // (Time in milliseconds) Delay between the mine resets
    private long lastReset; // (Time in milliseconds) Last time the mine was reset
    private File cachedFile; // Cached file of the mine

    public Mine(@NotNull String id, @NotNull Location spawn) {
        this.id = id;
        this.spawn = spawn;
        this.region = new Region();
        this.blocks = new HashMap<>() {{
            this.put(Material.STONE, 100.0);
        }};
        this.resetPercentage = 0.20;
        this.lastReset = System.currentTimeMillis();
        this.resetTime = 0;
        this.cachedFile = null;
    }

    public boolean create(RosePlugin plugin) {
        if (this.cachedFile != null && this.cachedFile.exists())
            return false;

        try {
            this.cachedFile = MineUtils.createFile(plugin, "mines", this.id + ".yml");
            plugin.getManager(MineManager.class).saveMine(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Reset the current mine
     */
    public boolean reset() {
        // Make sure the region is set
        if (this.region.getPos1() == null || this.region.getPos2() == null)
            return false;

        Map<Material, Double> blocks = new HashMap<>(this.blocks);
        if (this.blocks.isEmpty())
            blocks.put(Material.AIR, 100.0);

        // Remove all non-block materials
        blocks.keySet().removeIf(material -> !material.isBlock());

        this.lastReset = System.currentTimeMillis();
        this.region.loadBlocksInside(); // Load all the blocks inside the region

        this.region.getEntitiesInside().stream()
                .filter(livingEntity -> livingEntity.getType() == EntityType.PLAYER)
                .forEach(livingEntity -> {
                    Player player = (Player) livingEntity;

                    // Teleport the player to the spawn
                    if (NMSUtil.isPaper())
                        player.teleportAsync(this.spawn);
                    else
                        player.teleport(this.spawn);
                });

        // Fill the region with the blocks, cannot be run async due to Bukkit API
        // TODO: Optimize this to use a cuboid region iterator
        Bukkit.getScheduler().runTask(EternalMines.getInstance(), () -> this.region.fill(blocks));
        return true;

    }

    /**
     * Check if a mine is ready to be reset based on the reset percentage
     *
     * @return true if the mine should be reset
     */
    public boolean shouldReset() {
        if (this.calculatePercentage() <= this.resetPercentage)
            return true;

        return System.currentTimeMillis() - this.lastReset >= this.resetTime;
    }

    /**
     * Calculate the percentage of blocks mined in the mine
     *
     * @return the percentage of blocks mined
     */
    public double calculatePercentage() {
        List<Block> blocksInMine = this.region.getBlocks();
        if (blocksInMine.isEmpty()) {
            this.region.loadBlocksInside(); // Load all the blocks inside the region
            blocksInMine.addAll(this.region.getBlocks()); // Add all the blocks to the list
        }

        int airBlocks = (int) blocksInMine.stream().filter(block -> block == null || block.getType().isAir()).count();

        if (blocksInMine.isEmpty() || airBlocks == blocksInMine.size()) {
            return 0.0;
        }

        int totalBlocks = this.region.getTotalBlocks();
        int blocksLeft = totalBlocks - airBlocks;

        return Math.round(((double) blocksLeft / totalBlocks) * 100.0);
    }

    public @NotNull String getId() {
        return this.id;
    }

    public @NotNull Location getSpawn() {
        return this.spawn;
    }

    public void setSpawn(@NotNull Location spawn) {
        this.spawn = spawn;
    }

    public @NotNull Region getRegion() {
        return this.region;
    }

    public void setRegion(@NotNull Region region) {
        this.region = region;
    }

    public @NotNull Map<Material, Double> getBlocks() {
        return this.blocks;
    }

    public void setBlocks(@NotNull Map<Material, Double> blocks) {
        this.blocks = blocks;
    }

    public double getResetPercentage() {
        return this.resetPercentage;
    }

    public void setResetPercentage(double resetThreshold) {
        this.resetPercentage = resetThreshold;
    }

    public long getLastReset() {
        return this.lastReset;
    }

    public void setLastReset(long lastReset) {
        this.lastReset = lastReset;
    }

    public long getResetTime() {
        return this.resetTime;
    }

    public void setResetTime(long resetTime) {
        this.resetTime = resetTime;
    }

    public File getCachedFile() {
        return this.cachedFile;
    }

    public void setCachedFile(File cachedFile) {
        this.cachedFile = cachedFile;
    }

}
