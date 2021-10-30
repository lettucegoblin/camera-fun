package com.cameraeasing;

import net.runelite.client.config.*;

import static com.cameraeasing.CameraEasingPlugin.*;

@ConfigGroup("cameraeasing")
public interface CameraEasingConfig extends Config
{
    @ConfigItem(
            keyName = "filterType",
            name = "Ease type",
            description = "Sets an camera ease type",
            position = 1
    )
    default CameraEasingEaseType filterType()
    {
        return CameraEasingEaseType.easeOutBack;
    }

    @ConfigItem(
            position = 2,
            keyName = "infiniteLoop",
            name = "Loop?",
            description = ""
    )
    default boolean infiniteLoop()
    {
        return false;
    }

    @Range(
            min = 1
    )
    @ConfigItem(
            keyName = "period",
            name = "Duration",
            description = "How long an ease cycle is; the period",
            position = 3
    )
    default int period()
    {
        return period;
    }

    @Range(
            min = 1
    )
    @ConfigItem(
            keyName = "fps",
            name = "Animation fps",
            description = "Higher means smoother animation",
            position = 4
    )
    default int fps()
    {
        return fps;
    }

    @ConfigSection(
            name = "Intensity Type Settings",
            description = "Configuration for each type of intensity condition",
            position = 99
    )
    String intensitySection = "intensitysection";

    @Range(
            min = 1
    )
    @ConfigItem(
            keyName = "amplitude",
            name = "Intensity",
            description = "The amplitude, Higher is more",
            position = 1,
            section = intensitySection
    )
    default double amplitude()
    {
        return amplitude;
    }

    @ConfigItem(
            position = 2,
            keyName = "highStaminaCondition",
            name = "Higher Energy increases intensity?",
            description = "",
            section = intensitySection
    )
    default boolean highStaminaCondition()
    {
        return false;
    }

    @ConfigItem(
            position = 3,
            keyName = "lowStaminaCondition",
            name = "Lower Energy increases intensity?",
            description = "",
            section = intensitySection
    )
    default boolean lowStaminaCondition()
    {
        return false;
    }

    @ConfigItem(
            position = 4,
            keyName = "lowGameTimeCondition",
            name = "Lower Time in-game increases intensity?",
            description = "",
            section = intensitySection
    )
    default boolean lowGameTimeCondition()
    {
        return false;
    }

    @ConfigItem(
            position = 5,
            keyName = "highGameTimeCondition",
            name = "Higher Time in-game increases intensity?",
            description = "",
            section = intensitySection
    )
    default boolean highGameTimeCondition()
    {
        return false;
    }


}
