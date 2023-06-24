package xyz.oribuin.eternalmines;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import xyz.oribuin.eternalmines.hook.MineExpansion;
import xyz.oribuin.eternalmines.listener.MineListener;
import xyz.oribuin.eternalmines.listener.PlayerListeners;
import xyz.oribuin.eternalmines.manager.CommandManager;
import xyz.oribuin.eternalmines.manager.ConfigurationManager;
import xyz.oribuin.eternalmines.manager.ConfigurationManager.Setting;
import xyz.oribuin.eternalmines.manager.LocaleManager;
import xyz.oribuin.eternalmines.manager.MineManager;

import java.util.List;

public class EternalMines extends RosePlugin {

    private static EternalMines instance;

    public EternalMines() {
        super(110719, 18644, ConfigurationManager.class, null, LocaleManager.class, CommandManager.class);

        instance = this;
    }

    public static EternalMines getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        // Register PlaceholderAPI
        if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new MineExpansion(this).register();
        }
    }


    @Override
    public void reload() {
        super.reload();

        PluginManager pluginManager = this.getServer().getPluginManager();
        HandlerList.unregisterAll(this); // Unregister all listeners.

        if (Setting.LISTENERS_BREAK_BLOCK.getBoolean())
            pluginManager.registerEvents(new MineListener(this), this);

        if (Setting.LISTENERS_LOGIN.getBoolean())
            pluginManager.registerEvents(new PlayerListeners(this), this);

        // Register Plugin Tasks.
        if (Setting.RESET_TIMER_ENABLED.getBoolean()) {
            this.getManager(MineManager.class).getMines().values()
                    .forEach(mine -> {
                        List<Chunk> chunks = mine.getChunks();
                        if (chunks.isEmpty()) return;

                        chunks.forEach(chunk -> Bukkit.getRegionScheduler()
                                .runAtFixedRate(this, chunk.getWorld(), chunk.getX(), chunk.getZ(), task -> {
                                    if (mine.shouldReset())
                                        mine.reset();
                                }, 60, Setting.RESET_TIMER_INTERVAL.getLong()));
                    });
        }
    }

    @Override
    public void disable() {
        Bukkit.getAsyncScheduler().cancelTasks(this);
    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(
                MineManager.class
        );
    }

}
