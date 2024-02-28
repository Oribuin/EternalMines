package xyz.oribuin.eternalmines.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import xyz.oribuin.eternalmines.EternalMines;
import xyz.oribuin.eternalmines.manager.MineManager;
import xyz.oribuin.eternalmines.mine.Mine;

public class MineListener implements Listener {

    private final MineManager manager;

    public MineListener(EternalMines plugin) {
        this.manager = plugin.getManager(MineManager.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        Mine mine = this.manager.getMine(event.getBlock().getLocation());
        if (mine != null && mine.shouldReset()) {
            mine.reset(); // Reset the mine if it should be reset.
        }
    }

}
