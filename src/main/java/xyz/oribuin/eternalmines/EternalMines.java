package xyz.oribuin.eternalmines;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.plugin.PluginManager;
import xyz.oribuin.eternalmines.listener.MineListener;
import xyz.oribuin.eternalmines.manager.CommandManager;
import xyz.oribuin.eternalmines.manager.ConfigurationManager;
import xyz.oribuin.eternalmines.manager.LocaleManager;
import xyz.oribuin.eternalmines.manager.MineManager;
import xyz.oribuin.eternalmines.task.ResetTask;

import java.util.List;

public class EternalMines extends RosePlugin {

    private static EternalMines instance;

    public static EternalMines getInstance() {
        return instance;
    }

    public EternalMines() {
        super(-1, -1, ConfigurationManager.class, null, LocaleManager.class, CommandManager.class);

        instance = this;
    }

    @Override
    public void enable() {

        // Register Listeners.
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new MineListener(this), this);

        // Register Plugin Tasks.
        new ResetTask(this).runTaskTimerAsynchronously(this, 0L, 20L);
    }

    @Override
    public void disable() {

    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(
                MineManager.class
        );
    }

}
