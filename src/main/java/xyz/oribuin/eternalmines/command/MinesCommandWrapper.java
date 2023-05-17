package xyz.oribuin.eternalmines.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;

import java.util.List;

public class MinesCommandWrapper extends RoseCommandWrapper {

    public MinesCommandWrapper(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public String getDefaultName() {
        return "mines";
    }

    @Override
    public List<String> getDefaultAliases() {
        return List.of("emines");
    }

    @Override
    public List<String> getCommandPackages() {
        return List.of("xyz.oribuin.eternalmines.command.command");
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
