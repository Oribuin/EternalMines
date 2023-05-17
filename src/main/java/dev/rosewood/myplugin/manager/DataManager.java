package dev.rosewood.myplugin.manager;

import dev.rosewood.myplugin.database.migration._1_CreateInitialTables;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.AbstractDataManager;

import java.util.List;

public class DataManager extends AbstractDataManager {

    public DataManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public List<Class<? extends DataMigration>> getDataMigrations() {
        return List.of(_1_CreateInitialTables.class);
    }

    /**
     * Run a task asynchronously
     *
     * @param runnable The task to run
     */
    public void async(Runnable runnable) {
        this.rosePlugin.getServer().getScheduler().runTaskAsynchronously(this.rosePlugin, runnable);
    }
}
