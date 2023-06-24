package xyz.oribuin.eternalmines.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class BlockMaterialArgumentHandler extends RoseCommandArgumentHandler<Material> {

    public BlockMaterialArgumentHandler(RosePlugin plugin) {
        super(plugin, Material.class);
    }

    @Override
    protected Material handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        String input = argumentParser.next();
        Material material = Material.matchMaterial(input);

        if (material == null || !material.isBlock() || material.isAir())
            throw new HandledArgumentException("argument-handler-material", StringPlaceholders.of("input", input));

        return material;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();

        return Arrays.stream(Material.values())
                .filter(material -> material.isBlock() && !material.isAir())
                .map(material -> material.name().toLowerCase())
                .toList();
    }

}
