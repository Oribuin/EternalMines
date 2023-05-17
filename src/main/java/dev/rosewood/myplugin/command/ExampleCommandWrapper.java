package dev.rosewood.myplugin.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;

import java.util.List;

public class ExampleCommandWrapper extends RoseCommandWrapper {

    public ExampleCommandWrapper(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public String getDefaultName() {
        return "example";
    }

    @Override
    public List<String> getDefaultAliases() {
        return List.of("ex");
    }

    @Override
    public List<String> getCommandPackages() {
        return List.of("dev.rosewood.myplugin.command.command");
    }

    @Override
    public boolean includeBaseCommand() {
        return true;
    }

    @Override
    public boolean includeHelpCommand() {
        return true;
    }

    @Override
    public boolean includeReloadCommand() {
        return true;
    }

}
