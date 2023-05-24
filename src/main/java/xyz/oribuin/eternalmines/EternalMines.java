package xyz.oribuin.eternalmines;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.plugin.PluginManager;
import xyz.oribuin.eternalmines.hook.MineExpansion;
import xyz.oribuin.eternalmines.listener.MineListener;
import xyz.oribuin.eternalmines.listener.PlayerListeners;
import xyz.oribuin.eternalmines.manager.CommandManager;
import xyz.oribuin.eternalmines.manager.ConfigurationManager;
import xyz.oribuin.eternalmines.manager.ConfigurationManager.Setting;
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
        if (Setting.LISTENERS_BREAK_BLOCK.getBoolean())
            pluginManager.registerEvents(new MineListener(this), this);

        if (Setting.LISTENERS_LOGIN.getBoolean())
            pluginManager.registerEvents(new PlayerListeners(this), this);

        // Register Plugin Tasks.
        if (Setting.RESET_TIMER_ENABLED.getBoolean())
            new ResetTask(this).runTaskTimerAsynchronously(this, 0L, Setting.RESET_TIMER_INTERVAL.getLong());

        if (pluginManager.isPluginEnabled("PlaceholderAPI"))
            new MineExpansion(this).register();

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
