package xyz.oribuin.eternalmines.mine;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalmines.EternalMines;
import xyz.oribuin.eternalmines.manager.ConfigurationManager.Setting;
import xyz.oribuin.eternalmines.manager.MineManager;
import xyz.oribuin.eternalmines.util.MineUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mine {

    private final @NotNull String id; // Name of the mine
    private @NotNull World world; // World of the mine
    private @NotNull Location spawn; // Spawn of the mine (Where players teleport to)
    private @NotNull Region region; // Region of the mine (Where players can mine)
    private @NotNull Map<Material, Double> blocks; // Block material and chance to spawn
    private double resetPercentage; // % of blocks that need to be mined to reset the mine
    private long resetDelay; // (Time in milliseconds) Delay between the mine resets
    private long lastReset; // (Time in milliseconds) Last time the mine was reset
    private @Nullable File cachedFile; // Cached file of the mine

    public Mine(@NotNull String id, @NotNull Location spawn) {
        this.id = id;
        this.spawn = spawn;
        this.world = spawn.getWorld();
        this.region = new Region(world, null, null); // Create a new region
        this.blocks = new HashMap<>() {{
            this.put(Material.STONE, 100.0);
        }};
        this.resetPercentage = 20;
        this.lastReset = System.currentTimeMillis();
        this.resetDelay = 300;
        this.cachedFile = null;
    }

    public boolean create(RosePlugin plugin) {
        if (this.cachedFile != null && this.cachedFile.exists())
            return false;

        try {
            this.cachedFile = MineUtils.createFile(plugin, "mines", this.id + ".yml");
            plugin.getManager(MineManager.class).saveMine(this, true);
        } catch (Exception ignored) {
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

        this.lastReset = System.currentTimeMillis(); // Set the last reset to the current time
        EternalMines.getInstance().getManager(MineManager.class).saveMine(this, false); // Save the mine

        // Don't reset if the mine hasn't been hit the reset threshold
        if (Setting.LAG_CHECKS_ONLY_THRESHOLD_RESET.getBoolean()) {
            if (this.getPercentageLeft() > this.resetPercentage)
                return false;
        }

        // Don't reset if the mine is empty
        if (Setting.LAG_CHECKS_ONLY_IF_NOT_EMPTY.getBoolean()) {
            if (this.getPercentageBroken() <= 0)
                return false;
        }

        Map<Material, Double> blocks = new HashMap<>(this.blocks);
        if (this.blocks.isEmpty() || this.blocks.keySet().stream().allMatch(material -> material == Material.AIR)) {
            this.lastReset = System.currentTimeMillis(); // Set the last reset to the current time
            return true;
        }

        // Remove all non-block materials
        blocks.keySet().removeIf(material -> !material.isBlock());

        // Teleport players into the spawn
        if (Setting.LAG_CHECKS_PLAYER_CHECKS.getBoolean()) {
            this.region.getPlayersInside()
                    .forEach(player -> {
                        // why would you not want to use paper?
                        if (!NMSUtil.isPaper()) {
                            player.teleport(this.spawn);
                            return;
                        }

                        // Teleport the player async
                        player.teleportAsync(this.spawn).thenAccept(result -> {

                            // If the teleport failed, Teleport the player sync
                            if (!result) {
                                player.teleport(this.spawn);
                            }
                        });
                    });
        }

        // Fill the region with the blocks, cannot be run async due to Bukkit API
        Bukkit.getScheduler().runTask(EternalMines.getInstance(), () -> this.region.fill(blocks, Setting.LAG_CHECKS_RESET_ALL.getBoolean()));
        EternalMines.getInstance().getManager(MineManager.class).saveMine(this, false); // Save the mine
        return true;
    }

    /**
     * Time left until the mine resets
     *
     * @return the time left in milliseconds
     */
    public long getResetTimeLeft() {
        if (this.resetDelay <= 0 || !Setting.RESET_TIMER_ENABLED.getBoolean())
            return this.resetDelay * 1000;

        return (this.lastReset + (this.resetDelay * 1000)) - System.currentTimeMillis();
    }

    /**
     * Check if a mine is ready to be reset based on the reset percentage
     *
     * @return true if the mine should be reset
     */
    public boolean shouldReset() {
        // If the reset time is -1, Then the mine should reset when the reset percentage is reached
        if (this.resetDelay <= 0 && this.resetPercentage >= 0 || !Setting.RESET_TIMER_ENABLED.getBoolean())
            return this.getPercentageLeft() <= this.resetPercentage;

        return (System.currentTimeMillis() - this.lastReset) >= (this.resetDelay * 1000) || (this.resetPercentage >= 0 && this.getPercentageLeft() <= this.resetPercentage);
    }

    /**
     * Calculates the % of blocks left in the mine
     *
     * @return the percentage of blocks left
     */
    public double getPercentageLeft() {

        // Get all the blocks in the mine
        List<Material> blocksInMine = this.region.getLocations().stream().map(x -> x.getBlock().getType()).toList();

        if (this.region.getLocations().isEmpty()) {
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

    public void setSpawn(@NotNull Location spawn) {
        this.spawn = spawn;
    }

    public @NotNull World getWorld() {
        return this.world;
    }

    public void setWorld(@NotNull World world) {
        this.world = world;
        this.region.setWorld(world);
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
        return this.resetDelay;
    }

    public void setResetTime(long resetTime) {
        this.resetDelay = resetTime;
    }

    public @Nullable File getCachedFile() {
        return this.cachedFile;
    }

    public void setCachedFile(@Nullable File cachedFile) {
        this.cachedFile = cachedFile;
    }

}
