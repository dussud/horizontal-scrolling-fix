package org.dussud.horizontal_scrolling_fix.client;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.uku3lig.ukulib.config.option.CyclingOption;
import net.uku3lig.ukulib.config.option.WidgetCreator;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;
import org.dussud.horizontal_scrolling_fix.HorizontalScrollingFix;
import org.dussud.horizontal_scrolling_fix.HorizontalScrollingFixConfig;

import java.util.List;

public class HorizontalScrollingFixConfigScreen extends AbstractConfigScreen<HorizontalScrollingFixConfig> {
    protected HorizontalScrollingFixConfigScreen(Screen parent) {
        super("horizontal-scrolling-fix.config.title", parent, HorizontalScrollingFix.manager);
    }

    @Override
    protected WidgetCreator[] getWidgets(HorizontalScrollingFixConfig config) {
        //? if >=1.21.6 {
        CyclingOption<Boolean> horizontalScrollingEnabled = CyclingOption.ofBoolean(
                "horizontal-scrolling-fix.option.horizontalScrollingEnabled",
                config.isHorizontalScrollingEnabled(),
                value -> {
                    config.enableHorizontalScrolling(value);
                    //? if >=26.2 {
                    /*minecraft.gui.setScreen(new HorizontalScrollingFixConfigScreen(parent));
                    *///?} else {
                    minecraft.setScreen(new HorizontalScrollingFixConfigScreen(parent));
                    //?}
                }
        );

        CyclingOption<Boolean> horizontalScrollingDirectionReversed = new CyclingOption<Boolean>(
                "horizontal-scrolling-fix.option.horizontalScrollingDirectionReversed",
                List.of(Boolean.TRUE, Boolean.FALSE),
                config.isHorizontalScrollingDirectionReversed(),
                config::reverseHorizontalScrollingDirection,
                CyclingOption.BOOL_TO_TEXT,
                OptionInstance.noTooltip(),
                config.isHorizontalScrollingEnabled()
        );
        //?} else {
        /*CyclingOption<Boolean> horizontalScrollingEnabled = CyclingOption.ofBoolean(
                "horizontal-scrolling-fix.option.horizontalScrollingEnabled",
                config.isHorizontalScrollingEnabled(),
                config::enableHorizontalScrolling
        );

        CyclingOption<Boolean> horizontalScrollingDirectionReversed = CyclingOption.ofBoolean(
                "horizontal-scrolling-fix.option.horizontalScrollingDirectionReversed",
                config.isHorizontalScrollingDirectionReversed(),
                config::reverseHorizontalScrollingDirection
        );
        *///?}

        return new WidgetCreator[]{
                horizontalScrollingEnabled.wide(),
                horizontalScrollingDirectionReversed.wide()
        };
    }
}
