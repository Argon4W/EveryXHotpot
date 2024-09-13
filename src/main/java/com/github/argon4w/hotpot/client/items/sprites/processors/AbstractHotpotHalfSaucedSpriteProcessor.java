package com.github.argon4w.hotpot.client.items.sprites.processors;

import org.joml.Math;

public abstract class AbstractHotpotHalfSaucedSpriteProcessor extends AbstractHotpotGrayScaleSaucedSpriteProcessor {
    @Override
    public double getResultAlpha(double alpha, int x, int y, double width, double height) {
        return alpha * sigmoid(((height - 2f * y) / height) * 10f) * getAlphaModifier();
    }

    @Override
    public double getResultGrayScaleBase() {
        return 200;
    }

    @Override
    public double getResultGrayScaleFactor() {
        return 55;
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public abstract float getAlphaModifier();
}
