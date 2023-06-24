package xyz.oribuin.eternalmines.task;

import xyz.oribuin.eternalmines.EternalMines;
import xyz.oribuin.eternalmines.manager.MineManager;
import xyz.oribuin.eternalmines.mine.Mine;

public class ResetTask implements Runnable {

    private final MineManager manager;

    public ResetTask(final EternalMines plugin) {
        this.manager = plugin.getManager(MineManager.class);
    }

    @Override
    public void run() {
        for (Mine mine : this.manager.getMines().values()) {
            if (mine.shouldReset())
                mine.reset();
        }
    }
}
