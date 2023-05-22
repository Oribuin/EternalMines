package xyz.oribuin.eternalmines;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import xyz.oribuin.eternalmines.manager.CommandManager;
import xyz.oribuin.eternalmines.manager.ConfigurationManager;
import xyz.oribuin.eternalmines.manager.LocaleManager;
import xyz.oribuin.eternalmines.manager.MineManager;

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
