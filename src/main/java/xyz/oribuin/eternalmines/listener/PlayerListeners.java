package xyz.oribuin.eternalmines.listener;

import dev.rosewood.rosegarden.utils.NMSUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import xyz.oribuin.eternalmines.EternalMines;
import xyz.oribuin.eternalmines.manager.MineManager;
import xyz.oribuin.eternalmines.mine.Mine;

public class PlayerListeners implements Listener {

    private final MineManager manager;

    public PlayerListeners(final EternalMines manager) {
        this.manager = manager.getManager(MineManager.class);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLogin(PlayerLoginEvent event) {
        Mine mine = this.manager.getMine(event.getPlayer().getLocation());
        if (mine == null) return;

        if (NMSUtil.isPaper())
            event.getPlayer().teleportAsync(mine.getSpawn());
        else
            event.getPlayer().teleport(mine.getSpawn());
    }

}
