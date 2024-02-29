package xyz.oribuin.eternalmines.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;
import xyz.oribuin.eternalmines.EternalMines;
import xyz.oribuin.eternalmines.mine.FillType;

public class ConfigurationManager extends AbstractConfigurationManager {

    public ConfigurationManager(RosePlugin rosePlugin) {
        super(rosePlugin, Setting.class);
    }

    @Override
    protected String[] getHeader() {
        return new String[]{
                "___________ __                             .__      _____  .__                      ",
                "\\_   _____//  |_  ___________  ____ _____  |  |    /     \\ |__| ____   ____   ______",
                " |    __)_\\   __\\/ __ \\_  __ \\/    \\\\__  \\ |  |   /  \\ /  \\|  |/    \\_/ __ \\ /  ___/",
                " |        \\|  | \\  ___/|  | \\/   |  \\/ __ \\|  |__/    Y    \\  |   |  \\  ___/ \\___ \\ ",
                "/_______  /|__|  \\___  >__|  |___|  (____  /____/\\____|__  /__|___|  /\\___  >____  >",
                "        \\/           \\/           \\/     \\/              \\/        \\/     \\/     \\/ "
        };
    }

    public enum Setting implements RoseSetting {
        LAG_CHECKS("lag-checks", null, "Disable certain features of the plugin if they're causing slight amounts of lag."),
        LAG_CHECKS_PLAYER_CHECKS("lag-checks.player-checks", true, "Whether the player checks are enabled or not.", "This is to prevent the plugin from checking for player inside the mine"),
        LAG_CHECKS_ONLY_THRESHOLD_RESET("lag-checks.only-threshold-reset", false, "Enabling this option will only reset a mine if the threshold has been reached."),
        LAG_CHECKS_ONLY_IF_NOT_EMPTY("lag-checks.only-if-not-empty", false, "Enabling this open will only reset a mine if all the blocks have been broken"),
        LAG_CHECKS_RESET_ALL("lag-checks.reset-all", true, "Disabling this option will make mines only fill in the blank spaces, This is recommended for performance.", "This is to prevent the mine from resetting all blocks if it is not needed."),

        RESET_TIMER("reset-timer", null, "The task that checks for whether a mine should reset or not."),
        RESET_TIMER_ENABLED("reset-timer.enabled", true, "Whether the reset timer is enabled or not."),
        RESET_TIMER_INTERVAL("reset-timer.interval", 20, "The interval in seconds between each reset check.", "This is in ticks, [20 ticks = 1 second]"),

        LISTENERS("listeners", null, "The listeners that are registered to the plugin."),
        LISTENERS_BREAK_BLOCK("listeners.break-block", true, "Whether the break block listener is enabled or not.", "This listener is used to check whether the mine should reset when a block is broken."),
        LISTENERS_LOGIN("listeners.login", true, "Whether the login listener is enabled or not.", "This listener is used to teleport the player to the mine spawn when they login inside a mine."),

        FILL_TYPE("fill-type", FillType.LAYERED.name(), "The method that a mine would be reset with", "Options: [LAYERED, WHOLE, CHUNKED]", "Layered: Resets the mine layer by layer", "Whole: Resets the entire mine at once", "Chunked: Resets the mine in each individual chunk"),
        LAYER_FILL_DELAY("layer-fill-delay", 20, "The delay in seconds between each layer fill.", "This is in ticks, [20 ticks = 1 second]"),
        CHUNKED_FILL_DELAY("chunked-fill-delay", 100, "The delay in seconds between each chunk fill.", "This is in ticks, [20 ticks = 1 second]");;

        private final String key;
        private final Object defaultValue;
        private final String[] comments;
        private Object value = null;

        Setting(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String[] getComments() {
            return this.comments;
        }

        @Override
        public Object getCachedValue() {
            return this.value;
        }

        @Override
        public void setCachedValue(Object value) {
            this.value = value;
        }

        @Override
        public CommentedFileConfiguration getBaseConfig() {
            return EternalMines.getInstance().getManager(ConfigurationManager.class).getConfig();
        }
    }
}
