package xyz.oribuin.eternalmines.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalmines.mine.Mine;
import xyz.oribuin.eternalmines.mine.Region;
import xyz.oribuin.eternalmines.util.MineUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MineManager extends Manager {

    private final Map<String, Mine> cachedMines = new HashMap<>();

    public MineManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        final File minesFolder = new File(this.rosePlugin.getDataFolder(), "mines");
        if (!minesFolder.exists()) {
            minesFolder.mkdirs();
        }

        this.cachedMines.clear();
        this.rosePlugin.logger.info("Loading all the mines from /EternalMines/mines/");

        File[] files = minesFolder.listFiles();
        if (files == null) {
            MineUtils.createFile(this.rosePlugin, "mines", "example.yml");
            files = minesFolder.listFiles();
        }

        if (files == null) {
            this.rosePlugin.logger.severe("Unable to load mines from /EternalMines/mines/");
            return;
        }

        Arrays.stream(files).filter(file -> file.getName().endsWith(".yml"))
                .forEach(file -> {
                    final Mine mine = this.createMine(CommentedFileConfiguration.loadConfiguration(file));
                    if (mine != null) {
                        this.cachedMines.put(mine.getId(), mine);
                    }
                });
    }

    /**
     * Create and load a mine from the given config
     *
     * @param config The config to load the mine from
     * @return The loaded mine
     */
    public @Nullable Mine createMine(@NotNull final CommentedFileConfiguration config) {
        CommentedConfigurationSection settings = config.getConfigurationSection("mine-settings");
        if (settings == null) {
            this.rosePlugin.logger.severe("Unable to load mine settings from " + config.getName());
            return null;
        }

        final String id = settings.getString("id"); // Id of the mine
        if (id == null) return null;

        Location spawnLocation = null;

        // Load spawn location of the mine
        final CommentedConfigurationSection spawnSection = settings.getConfigurationSection("spawn");
        if (spawnSection != null) {
            final String world = spawnSection.getString("world");

            if (world != null) {
                spawnLocation = new Location(
                        this.rosePlugin.getServer().getWorld(world),
                        spawnSection.getDouble("x"),
                        spawnSection.getDouble("y"),
                        spawnSection.getDouble("z"),
                        (float) spawnSection.getDouble("yaw"),
                        (float) spawnSection.getDouble("pitch")
                );
            }
        }

        if (spawnLocation == null) return null;

        // Load region of the mine
        final Region region = new Region();
        final CommentedConfigurationSection regions = settings.getConfigurationSection("region");
        if (regions != null) {
            World world = Bukkit.getWorld(regions.getString("world", "")); // World of the mine
            if (world != null) {
                final Location pos1 = new Location(
                        world,
                        regions.getDouble("pos1.x"),
                        regions.getDouble("pos1.y"),
                        regions.getDouble("pos1.z")
                );

                final Location pos2 = new Location(
                        world,
                        regions.getDouble("pos2.x"),
                        regions.getDouble("pos2.y"),
                        regions.getDouble("pos2.z")
                );

                region.setPos1(pos1);
                region.setPos2(pos2);
            }
        }

        // Load blocks of the mine
        final Map<Material, Double> blocks = new HashMap<>();
        final CommentedConfigurationSection blockSection = settings.getConfigurationSection("blocks");
        if (blockSection != null && !blockSection.getKeys(false).isEmpty()) {
            blockSection.getKeys(false).forEach(s -> {
                try {
                    double chance = Double.parseDouble(blockSection.getString(s, "0.0"));
                    Material material = Material.matchMaterial(s);

                    if (material != null && chance > 0)
                        blocks.put(material, chance);
                } catch (NumberFormatException ignored) {
                }
            });
        }

        // Load the mine settings.
        Mine mine = new Mine(id, spawnLocation);
        mine.setResetPercentage(settings.getDouble("reset-percentage", 0.20));
        mine.setResetTime(settings.getLong("reset-delay", 0) * 1000);
        mine.setBlocks(blocks);
        mine.setRegion(region);

        return mine;
    }

    /**
     * Get a mine from the cache
     *
     * @param mine The id of the mine
     */
    public void saveMine(@NotNull Mine mine) {
        this.saveMine(mine, null);
    }

    /**
     * Save a mine to a cache and optionally to a file
     *
     * @param mine The mine to save
     * @param file The file to save the mine to
     */
    public void saveMine(@NotNull Mine mine, @Nullable File file) {
        this.cachedMines.put(mine.getId(), mine);

        if (file != null) {
            CommentedFileConfiguration config = CommentedFileConfiguration.loadConfiguration(file);
            CommentedConfigurationSection settings = config.getConfigurationSection("mine-settings");

            if (settings == null)
                settings = config.createSection("mine-settings"); // Create the section if it doesn't exist.

            settings.set("id", mine.getId());
            settings.set("reset-percentage", mine.getResetPercentage());
            settings.set("reset-delay", mine.getResetTime() / 1000);

            // Set the spawn location of the mine
            settings.set("spawn.world", mine.getSpawn().getWorld().getName());
            settings.set("spawn.x", mine.getSpawn().getX());
            settings.set("spawn.y", mine.getSpawn().getY());
            settings.set("spawn.z", mine.getSpawn().getZ());
            settings.set("spawn.yaw", mine.getSpawn().getYaw());
            settings.set("spawn.pitch", mine.getSpawn().getPitch());

            // Set the region of the mine
            settings.set("region.world", mine.getRegion().getPos1().getWorld().getName());

            // Save Pos1
            settings.set("region.pos1.x", mine.getRegion().getPos1().getX());
            settings.set("region.pos1.y", mine.getRegion().getPos1().getY());
            settings.set("region.pos1.z", mine.getRegion().getPos1().getZ());

            // Save Pos2
            settings.set("region.pos2.x", mine.getRegion().getPos2().getX());
            settings.set("region.pos2.y", mine.getRegion().getPos2().getY());
            settings.set("region.pos2.z", mine.getRegion().getPos2().getZ());

            // Save the blocks of the mine
            CommentedConfigurationSection blockSection = settings.getConfigurationSection("blocks");
            if (blockSection == null)
                blockSection = settings.createSection("blocks"); // Create the section if it doesn't exist.

            for (Map.Entry<Material, Double> entry : mine.getBlocks().entrySet()) {
                blockSection.set(entry.getKey().name(), entry.getValue());
            }

            config.save(file);
        }
    }

    /**
     * Get a mine by its name
     *
     * @param name The name of the mine
     * @return The mine
     */
    public @Nullable Mine getMine(@NotNull final String name) {
        return this.cachedMines.get(name);
    }

    @Override
    public void disable() {
        this.cachedMines.clear();
    }

    /**
     * Get all the mines
     *
     * @return All the mines
     */
    public @NotNull Map<String, Mine> getMines() {
        return this.cachedMines;
    }

}
