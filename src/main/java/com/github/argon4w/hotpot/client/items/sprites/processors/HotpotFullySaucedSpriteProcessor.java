package com.github.argon4w.hotpot.client.items.sprites.processors;

public class HotpotFullySaucedSpriteProcessor extends AbstractHotpotGrayScaleSaucedSpriteProcessor {
    @Override
    public double getResultAlpha(double alpha, int x, int y, double width, double height) {
        return alpha;
    }

    @Override
    public double getResultGrayScaleBase() {
        return 85;
    }

    @Override
    public double getResultGrayScaleFactor() {
        return 170;
    }

    @Override
    public double getRandomFactor() {
        return 0.06f;
    }

    @Override
    public String getSuffix() {
        return "_fully_sauced";
    }
}
