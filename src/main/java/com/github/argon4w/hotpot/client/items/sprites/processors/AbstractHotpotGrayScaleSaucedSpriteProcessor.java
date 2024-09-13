package com.github.argon4w.hotpot.client.items.sprites.processors;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import org.joml.Math;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class AbstractHotpotGrayScaleSaucedSpriteProcessor implements IHotpotSpriteProcessor {
    @Override
    public void processSpriteImage(NativeImage original, NativeImage image, FrameSize frameSize, int frame) {
        double amplifier = 0.7f / getAverageGrayScale(original, frameSize, frame);
        double base = getResultGrayScaleBase();
        double factor = getResultGrayScaleFactor();

        RandomSource source = RandomSource.create();
        source.setSeed(42L);

        getPointStream(frameSize).forEach(point -> {
            int x = point.x;
            int y = point.y + frame * frameSize.height();

            int color = original.getPixelRGBA(x, y);
            int alpha = FastColor.ABGR32.alpha(color);
            double grayScale = Math.min(1f, getGrayScale(color) * amplifier + source.nextGaussian() * 0.1f);

            double resultAlpha = getResultAlpha(alpha, x, y, frameSize.width(), frameSize.height());
            double resultColor = base + grayScale * factor;

            image.setPixelRGBA(x, y, FastColor.ABGR32.color((int) resultAlpha, (int) resultColor, (int) resultColor, (int) resultColor));
        });
    }

    private double getAverageGrayScale(NativeImage image, FrameSize frameSize, int frame) {
        return getPointStream(frameSize).mapToInt(point -> image.getPixelRGBA(point.x, point.y + frame * frameSize.height())).filter(color -> FastColor.ARGB32.alpha(color) != 0).mapToDouble(this::getGrayScale).average().orElse(0d);
    }

    public double getGrayScale(int color) {
        return (FastColor.ABGR32.red(color) * 0.299 + FastColor.ABGR32.green(color) * 0.587 + FastColor.ABGR32.blue(color) * 0.144) / 255.0;
    }

    public Stream<Point> getPointStream(FrameSize frameSize) {
        return IntStream.range(0, frameSize.width()).boxed().flatMap(x -> IntStream.range(0, frameSize.height()).mapToObj(y -> new Point(x, y)));
    }

    public abstract double getResultAlpha(double alpha, int x, int y, double width, double height);
    public abstract double getResultGrayScaleBase();
    public abstract double getResultGrayScaleFactor();

    public record Point(int x, int y) {

    }
}
