package xyz.oribuin.eternalmines.mine;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.NMSUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalmines.EternalMines;
import xyz.oribuin.eternalmines.util.MineUtils;
import xyz.oribuin.eternalmines.util.SchedulerUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Mine {

    private final @NotNull String id; // Name of the mine
    private @NotNull Location spawn; // Spawn of the mine (Where players teleport to)
    private @NotNull Region region; // Region of the mine (Where players can mine)
    private @NotNull Map<Material, Double> blocks; // Block material and chance to spawn
    private double resetPercentage; // % of blocks that need to be mined to reset the mine
    private long resetTime; // (Time in milliseconds) Delay between the mine resets
    private long lastReset; // (Time in milliseconds) Last time the mine was reset

    public Mine(@NotNull String id, @NotNull Location spawn) {
        this.id = id;
        this.spawn = spawn;
        this.region = new Region();
        this.blocks = new HashMap<>();
        this.resetPercentage = 0.20;
        this.lastReset = System.currentTimeMillis();
        this.resetTime = 0;
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

        CompletableFuture.runAsync(() -> this.region.getInside().stream()
                .filter(livingEntity -> livingEntity.getType() == EntityType.PLAYER)
                .forEach(livingEntity -> {
                    Player player = (Player) livingEntity;

                    // Teleport the player to the spawn
                    if (NMSUtil.isPaper())
                        player.teleportAsync(this.spawn);
                    else
                        player.teleport(this.spawn);
                }));

        // Fill the region with the blocks, maybe try async?
        SchedulerUtil.async(() -> this.region.fill(blocks));
        return true;

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

}
