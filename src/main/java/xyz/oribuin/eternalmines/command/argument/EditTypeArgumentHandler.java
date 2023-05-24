package xyz.oribuin.eternalmines.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.eternalmines.command.command.edit.EditType;
import xyz.oribuin.eternalmines.util.MineUtils;

import java.util.Arrays;
import java.util.List;

public class EditTypeArgumentHandler extends RoseCommandArgumentHandler<EditType> {

    public EditTypeArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, EditType.class);
    }

    @Override
    protected EditType handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        String input = argumentParser.next();
        EditType type = MineUtils.getEnum(EditType.class, input.toUpperCase());

        if (type == null)
            throw new HandledArgumentException("argument-handler-edit-type", StringPlaceholders.of("input", input));

        return type;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();
        return Arrays.stream(EditType.values()).map(Enum::name).toList();
    }

}
