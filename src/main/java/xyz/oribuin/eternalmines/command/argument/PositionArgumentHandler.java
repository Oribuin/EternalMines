package xyz.oribuin.eternalmines.command.argument;


import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.eternalmines.mine.Position;
import xyz.oribuin.eternalmines.util.MineUtils;

import java.util.List;

public class PositionArgumentHandler extends RoseCommandArgumentHandler<Position> {

    public PositionArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Position.class);
    }

    @Override
    protected Position handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        String input = argumentParser.next();
        Position position = MineUtils.getEnum(Position.class, input);

        if (input == null || position == null)
            throw new HandledArgumentException("argument-handler-position", StringPlaceholders.of("input", input));

        return position;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();

        return List.of("first", "second");
    }

}
