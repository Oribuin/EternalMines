package xyz.oribuin.eternalmines.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.manager.AbstractCommandManager;
import xyz.oribuin.eternalmines.command.MinesCommandWrapper;

import java.util.List;

public class CommandManager extends AbstractCommandManager {

    public CommandManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public List<Class<? extends RoseCommandWrapper>> getRootCommands() {
        return List.of(MinesCommandWrapper.class);
    }

    @Override
    public List<String> getArgumentHandlerPackages() {
        return List.of("xyz.oribuin.eternalmines.command.argument");
    }

}
