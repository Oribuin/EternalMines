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
        File minesFolder = new File(this.rosePlugin.getDataFolder(), "mines");
        if (!minesFolder.exists()) {
            minesFolder.mkdirs();
        }

        this.cachedMines.clear();
        this.rosePlugin.getLogger().info("Loading all the mines from /EternalMines/mines/");

        File[] files = minesFolder.listFiles();
        if (files == null || files.length == 0) {
            MineUtils.createFile(this.rosePlugin, "mines", "example.yml");
            files = minesFolder.listFiles();
        }

        if (files == null) {
            this.rosePlugin.getLogger().severe("Unable to load mines from /EternalMines/mines/");
            return;
        }

        Arrays.stream(files).filter(file -> file.getName().endsWith(".yml")).forEach(file -> {
            Mine mine = this.createMine(file, CommentedFileConfiguration.loadConfiguration(file));
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
    public @Nullable Mine createMine(@NotNull File file, @NotNull CommentedFileConfiguration config) {
        CommentedConfigurationSection settings = config.getConfigurationSection("mine-settings");
        if (settings == null) {
            this.rosePlugin.getLogger().severe("Unable to load mine settings from " + config.getName());
            return null;
        }

        String id = settings.getString("id"); // Id of the mine
        if (id == null) {
            this.rosePlugin.getLogger().severe("Unable to load mine id from " + file.getName());
            return null;
        }

        Location spawnLocation = null;
        String worldName = settings.getString("world", "");
        World world = Bukkit.getWorld(worldName); // World of the mine
        if (world == null) {
            this.rosePlugin.getLogger().severe("Unable to load mine world from " + file.getName() + " (World: " + worldName + " does not exist)");
            return null;
        }

        // Load spawn location of the mine
        CommentedConfigurationSection spawnSection = settings.getConfigurationSection("spawn");
        if (spawnSection != null) {
            spawnLocation = new Location(world, spawnSection.getDouble("x"), spawnSection.getDouble("y"), spawnSection.getDouble("z"), (float) spawnSection.getDouble("yaw"), (float) spawnSection.getDouble("pitch"));
        }

        if (spawnLocation == null) {
            this.rosePlugin.getLogger().severe("Unable to load mine spawn location from " + file.getName());
            return null;
        }

        // Load region of the mine
        Region region = new Region();
        CommentedConfigurationSection regions = settings.getConfigurationSection("region");
        if (regions != null) {
            Location pos1 = new Location(world, regions.getDouble("pos1.x"), regions.getDouble("pos1.y"), regions.getDouble("pos1.z"));
            Location pos2 = new Location(world, regions.getDouble("pos2.x"), regions.getDouble("pos2.y"), regions.getDouble("pos2.z"));

            region.setPos1(pos1);
            region.setPos2(pos2);
            region.setWorld(world);
        }

        // Load blocks of the mine
        Map<Material, Double> blocks = new HashMap<>();
        CommentedConfigurationSection blockSection = settings.getConfigurationSection("blocks");
        if (blockSection != null && !blockSection.getKeys(false).isEmpty()) {
            blockSection.getKeys(false).forEach(s -> {
                try {
                    double chance = Double.parseDouble(blockSection.getString(s, "0.0"));
                    Material material = Material.matchMaterial(s);

                    if (material != null && chance > 0) blocks.put(material, chance);
                } catch (NumberFormatException ignored) {
                }
            });
        }

        // Load the mine settings.
        Mine mine = new Mine(id, spawnLocation);
        mine.setWorld(world);
        mine.setCachedFile(file);
        mine.setResetPercentage(settings.getDouble("reset-percentage", 20));
        mine.setResetTime(Math.max(1, settings.getLong("reset-delay", 300)));
        mine.setBlocks(blocks);
        mine.setRegion(region);

        // Check if the blocks combined chance is above 100%
        double totalChance = blocks.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalChance > 100) {
            this.rosePlugin.getLogger().warning("The total chance of the blocks in " + mine.getId() + " is above 100% (" + totalChance + "%)");
        }

        return mine;
    }

    /**
     * Save a mine to a cache and optionally to a file
     *
     * @param mine The mine to save
     */
    public void saveMine(@NotNull Mine mine, boolean saveToFile) {
        this.cachedMines.put(mine.getId(), mine);

        if (mine.getCachedFile() != null && saveToFile) {
            CommentedFileConfiguration config = CommentedFileConfiguration.loadConfiguration(mine.getCachedFile());
            CommentedConfigurationSection settings = config.getConfigurationSection("mine-settings");

            if (settings == null)
                settings = config.createSection("mine-settings"); // Create the section if it doesn't exist.

            settings.set("id", mine.getId());
            settings.set("reset-percentage", mine.getResetPercentage());
            settings.set("reset-delay", mine.getResetTime());
            settings.set("world", mine.getWorld().getName());

            // Set the spawn location of the mine
            if (mine.getSpawn().getWorld() != null) {
                settings.set("spawn.x", mine.getSpawn().getX());
                settings.set("spawn.y", mine.getSpawn().getY());
                settings.set("spawn.z", mine.getSpawn().getZ());
                settings.set("spawn.yaw", mine.getSpawn().getYaw());
                settings.set("spawn.pitch", mine.getSpawn().getPitch());
            }

            // Set the region of the mine
            // Save Pos1
            if (mine.getRegion().getPos1() != null) {
                settings.set("region.pos1.x", mine.getRegion().getPos1().getX());
                settings.set("region.pos1.y", mine.getRegion().getPos1().getY());
                settings.set("region.pos1.z", mine.getRegion().getPos1().getZ());
            }

            // Save Pos2
            if (mine.getRegion().getPos2() != null) {
                settings.set("region.pos2.x", mine.getRegion().getPos2().getX());
                settings.set("region.pos2.y", mine.getRegion().getPos2().getY());
                settings.set("region.pos2.z", mine.getRegion().getPos2().getZ());
            }

            // Save the blocks of the mine
            CommentedConfigurationSection blockSection = settings.getConfigurationSection("blocks");
            if (blockSection == null)
                blockSection = settings.createSection("blocks"); // Create the section if it doesn't exist.

            for (Map.Entry<Material, Double> entry : mine.getBlocks().entrySet()) {
                blockSection.set(entry.getKey().name(), entry.getValue());
            }

            // Check if the blocks combined chance is above 100%
            double totalChance = mine.getBlocks().values().stream().mapToDouble(Double::doubleValue).sum();
            if (totalChance > 100) {
                this.rosePlugin.getLogger().warning("The total chance of the blocks in " + mine.getId() + " is above 100% (" + totalChance + "%)");
            }

            config.save(mine.getCachedFile());
        }
    }

    /**
     * Get a mine by its name
     *
     * @param name The name of the mine
     * @return The mine
     */
    public @Nullable Mine getMine(@NotNull String name) {
        return this.cachedMines.get(name);
    }

    /**
     * Get a mine by its location
     *
     * @param mine The location of the mine
     * @return The mine
     */
    public @Nullable Mine getMine(@NotNull Location mine) {
        return this.cachedMines.values().stream().filter(m -> m.getRegion().isInside(mine)).findFirst().orElse(null);
//        for (Mine m : this.cachedMines.values()) {
//            if (m.getRegion().isInside(mine)) {
//                return m;
//            }
//        }
//        return null;
    }

    public boolean deleteMine(@NotNull String name) {
        Mine mine = this.cachedMines.get(name);
        if (mine == null) return false;

        // Delete the mine file
        if (mine.getCachedFile() != null) {
            mine.getCachedFile().delete();
        }

        // Delete the mine from the cache
        this.cachedMines.remove(name);
        return true;
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
