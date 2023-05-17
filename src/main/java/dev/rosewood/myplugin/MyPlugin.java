package dev.rosewood.myplugin;

import dev.rosewood.guiframework.GuiFramework;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.*;

import java.util.ArrayList;
import java.util.List;

public class MyPlugin extends RosePlugin {

    private static MyPlugin instance;
    private GuiFramework framework;

    public static MyPlugin getInstance() {
        return instance;
    }

    public MyPlugin() {
        super(-1, -1, null, null, null, null);

        instance = this;
    }

    @Override
    protected void enable() {
        this.framework = GuiFramework.instantiate(this);
    }

    @Override
    protected void disable() {

    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of();
    }

    public GuiFramework getFramework() {
        return framework;
    }

}
