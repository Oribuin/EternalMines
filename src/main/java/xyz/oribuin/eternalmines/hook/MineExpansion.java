package xyz.oribuin.eternalmines.hook;

import dev.rosewood.rosegarden.RosePlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalmines.manager.MineManager;
import xyz.oribuin.eternalmines.mine.Mine;
import xyz.oribuin.eternalmines.util.MineUtils;

@SuppressWarnings("deprecation")
public class MineExpansion extends PlaceholderExpansion {

    private final RosePlugin rosePlugin;
    private final MineManager mineManager;

    public MineExpansion(RosePlugin rosePlugin) {
        this.rosePlugin = rosePlugin;
        this.mineManager = this.rosePlugin.getManager(MineManager.class);
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] args = params.split("_");

        if (args.length < 2)
            return "No Mine Specified";

        Mine mine = this.mineManager.getMine(args[0]);
        if (mine == null) return "Unknown Mine";

        String placeholder = String.join("_", args).substring(args[0].length() + 1);

        return switch (placeholder) {
            case "name" -> mine.getId();
            case "left" -> String.valueOf(mine.getPercentageLeft());
            case "broken" -> String.valueOf(mine.getPercentageBroken());
            case "timer_raw" -> String.valueOf(mine.getResetTimeLeft());
            case "timer" -> MineUtils.convertMilliSecondsToHMmSs(mine.getResetTimeLeft());
            default -> null;
        };

    }

    @Override
    public @NotNull String getIdentifier() {
        return "EternalMines";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Oribuin";
    }

    @Override
    public @NotNull String getVersion() {
        return this.rosePlugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

}
