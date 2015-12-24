package com.buckeridge;

import java.util.Locale;

/**
 * Represents screen densities
 *
 * Created by buckeridge85 on 12/24/15.
 */
public enum Density {
    MDPI(1),
    HDPI(1.5f),
    XHDPI(2),
    XXHDPI(3),
    XXXHDPI(4);

    /**
     * The ratio of the resolution to the base density MDPI
     */
    private final float ratioToBaseDensity;

    Density(float ratioToBaseDensity) {
        this.ratioToBaseDensity = ratioToBaseDensity;
    }

    public float getRatioToBaseDensity() {
        return ratioToBaseDensity;
    }

    public String getDrawableResourceDirectoryName() {
        return String.format(Locale.US, "drawable-%s", name().toLowerCase(Locale.US));
    }
}
