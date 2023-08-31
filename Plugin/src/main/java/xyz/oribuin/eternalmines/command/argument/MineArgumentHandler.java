package xyz.oribuin.eternalmines.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.eternalmines.manager.MineManager;
import xyz.oribuin.eternalmines.mine.Mine;

import java.util.List;
import java.util.Set;

public class MineArgumentHandler extends RoseCommandArgumentHandler<Mine> {

    public MineArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Mine.class);
    }

    @Override
    protected Mine handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        String input = argumentParser.next();
        Mine mine = this.rosePlugin.getManager(MineManager.class).getMine(input);
        if (mine == null) {
            throw new HandledArgumentException("argument-handler-mine", StringPlaceholders.of("mine", input));
        }

        return mine;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();
        Set<String> mines = this.rosePlugin.getManager(MineManager.class).getMines().keySet();

        if (mines.isEmpty()) {
            return List.of("<no loaded mines>");
        }

        return mines.stream().toList();
    }

}
