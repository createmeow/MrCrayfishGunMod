package com.mrcrayfish.guns.debug.client.screen.widget;

import com.mrcrayfish.guns.debug.IDebugWidget;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class DebugSlider extends AbstractSliderButton implements IDebugWidget {
    private final double minValue;
    private final double maxValue;
    private final Consumer<Double> callback;

    public DebugSlider(double minValue, double maxValue, double currentValue, double stepSize, int precision, Consumer<Double> callback) {
        super(0, 0, 0, 14, Component.empty(), (currentValue - minValue) / (maxValue - minValue));
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.callback = callback;
    }

    private double getScaledValue() {
        return this.minValue + this.value * (this.maxValue - this.minValue);
    }

    @Override
    protected void updateMessage() {
    }

    @Override
    protected void applyValue() {
        this.callback.accept(this.getScaledValue());
    }
}
