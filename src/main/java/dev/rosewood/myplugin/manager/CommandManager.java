package dev.rosewood.myplugin.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.manager.AbstractCommandManager;

import java.util.ArrayList;
import java.util.List;

public class CommandManager extends AbstractCommandManager {

    public CommandManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public List<Class<? extends RoseCommandWrapper>> getRootCommands() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getArgumentHandlerPackages() {
        return List.of("dev.rosewood.myplugin.command.argument");
    }

}
