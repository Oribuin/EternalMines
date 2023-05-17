package xyz.oribuin.eternalmines;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.*;

import java.util.List;

public class EternalMines extends RosePlugin {

    private static EternalMines instance;

    public static EternalMines getInstance() {
        return instance;
    }

    public EternalMines() {
        super(-1, -1, null, null, null, null);

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
        return null;
    }

}
