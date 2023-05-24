package xyz.oribuin.eternalmines.listener;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import xyz.oribuin.eternalmines.EternalMines;
import xyz.oribuin.eternalmines.manager.MineManager;
import xyz.oribuin.eternalmines.mine.Mine;

public class MineListener implements Listener {

    private final EternalMines plugin;
    private final MineManager manager;

    public MineListener(final EternalMines plugin) {
        this.plugin = plugin;
        this.manager = this.plugin.getManager(MineManager.class);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Mine mine = this.manager.getMine(event.getBlock().getLocation());
        if (mine == null) return;

        double currentProgress = mine.getPercentageLeft();
        event.getPlayer().sendActionBar(MiniMessage.miniMessage().deserialize("<gradient:#7F7FD5:#86A8E7:#91EAE4><bold>Mine Progress: " + currentProgress + "%"));

        if (mine.shouldReset()) {
            mine.reset();
            return;
        }

        // Remove the block from the mine.

        int index = mine.getRegion().getBlocks().indexOf(event.getBlock());
        if (index == -1) return;

        mine.getRegion().getBlocks().set(index, null);
    }

}
