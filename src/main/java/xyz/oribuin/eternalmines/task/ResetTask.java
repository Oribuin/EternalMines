package xyz.oribuin.eternalmines.task;

import org.bukkit.scheduler.BukkitRunnable;
import xyz.oribuin.eternalmines.EternalMines;
import xyz.oribuin.eternalmines.manager.MineManager;
import xyz.oribuin.eternalmines.mine.Mine;

public class ResetTask extends BukkitRunnable {

    private final MineManager manager;

    public ResetTask(final EternalMines plugin) {
        this.manager = plugin.getManager(MineManager.class);
    }

    @Override
    public void run() {
        this.manager.getMines().values()
                .stream()
                .filter(Mine::shouldReset)
                .forEach(Mine::reset);
    }

}
