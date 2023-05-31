package xyz.oribuin.eternalmines.mine;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalmines.EternalMines;
import xyz.oribuin.eternalmines.manager.MineManager;
import xyz.oribuin.eternalmines.util.MineUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Mine {

    private final @NotNull String id; // Name of the mine
    private @NotNull World world; // World of the mine
    private @NotNull Location spawn; // Spawn of the mine (Where players teleport to)
    private @NotNull Region region; // Region of the mine (Where players can mine)
    private @NotNull Map<Material, Double> blocks; // Block material and chance to spawn
    private double resetPercentage; // % of blocks that need to be mined to reset the mine
    private long resetTime; // (Time in milliseconds) Delay between the mine resets
    private long lastReset; // (Time in milliseconds) Last time the mine was reset
    private @Nullable File cachedFile; // Cached file of the mine

    public Mine(@NotNull String id, @NotNull Location spawn) {
        this.id = id;
        this.spawn = spawn;
        this.world = spawn.getWorld();
        this.region = new Region();
        this.blocks = new HashMap<>() {{
            this.put(Material.STONE, 100.0);
        }};
        this.resetPercentage = 20;
        this.lastReset = System.currentTimeMillis();
        this.resetTime = 300;
        this.cachedFile = null;
    }

    public boolean create(RosePlugin plugin) {
        if (this.cachedFile != null && this.cachedFile.exists())
            return false;

        try {
            this.cachedFile = MineUtils.createFile(plugin, "mines", this.id + ".yml");
            plugin.getManager(MineManager.class).saveMine(this, true);
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

        this.lastReset = System.currentTimeMillis(); // Set the last reset to the current time

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
        EternalMines.getInstance().getManager(MineManager.class).saveMine(this, false); // Save the mine

        return true;

    }

    /**
     * Time left until the mine resets
     *
     * @return the time left in milliseconds
     */
    public long getResetTimeLeft() {
        return (this.lastReset + (this.resetTime * 1000)) - System.currentTimeMillis();
    }

    /**
     * Check if a mine is ready to be reset based on the reset percentage
     *
     * @return true if the mine should be reset
     */
    public boolean shouldReset() {

        // If the reset time is -1, Then the mine should reset when the reset percentage is reached
        if (this.resetTime <= 0)
            return this.getPercentageLeft() <= this.resetPercentage;

        return (System.currentTimeMillis() - this.lastReset) >= (this.resetTime * 1000) || this.getPercentageLeft() <= this.resetPercentage;
    }

    /**
     * Calculates the % of blocks left in the mine
     *
     * @return the percentage of blocks left
     */
    public double getPercentageLeft() {

        // Get all the blocks in the mine
        List<Material> blocksInMine = new ArrayList<>(this.region.getLocations().stream().map(MineUtils::getLazyMaterial).filter(Objects::nonNull).toList());

        if (this.region.getLocations().size() == 0) {
            this.region.cacheLocations(); // Cache the locations
        }

        int airBlocks = (int) blocksInMine.stream().filter(material -> material == Material.AIR).count();
        int totalBlocks = this.region.getTotalBlocks();

        if (blocksInMine.isEmpty() || airBlocks == totalBlocks) return 0.0;
        int blocksLeft = totalBlocks - airBlocks;

        return Math.round((double) blocksLeft / totalBlocks * 100.0);
    }

    /**
     * Calculates the % of blocks broken in the mine
     *
     * @return the percentage of blocks broken
     */
    public double getPercentageBroken() {
        double percentageLeft = this.getPercentageLeft();

        return Math.max(100.0 - percentageLeft, 0.0);
    }

    public @NotNull String getId() {
        return this.id;
    }

    public @NotNull Location getSpawn() {
        return this.spawn;
    }

    public @NotNull World getWorld() {
        return this.world;
    }

    public void setWorld(@NotNull World world) {
        this.world = world;
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

    public @Nullable File getCachedFile() {
        return this.cachedFile;
    }

    public void setCachedFile(@Nullable File cachedFile) {
        this.cachedFile = cachedFile;
    }

}
