package com.varankin.brains.jfx;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.MultipleSelectionModel;

/**
 * Детектор числа выбранных элементов.
 *
 * @author &copy; 2013 Николай Варанкин
 */
class SelectionDetector<T> extends BooleanBinding 
{
    private final int LOW;
    private final int HIGH;
    private final boolean DIRECT;
    private final ObjectProperty<? extends MultipleSelectionModel<T>> PROPERTY;

    /**
     * @param property объект мониторинга.
     * @param direct   {@code true} для проверки вхождения в диапазон, {@code false} для проверки отсутствия в диапазоне.
     * @param low      нижняя граница диапазона.
     * @param high     верхняя граница диапазона.
     */
    SelectionDetector( ObjectProperty<? extends MultipleSelectionModel<T>> property, boolean direct, int low, int high )
    {
        PROPERTY = property;
        LOW = low;
        HIGH = high;
        DIRECT = direct;
        super.bind( property.getValue().getSelectedItems() );
    }

    /**
     * @param property объект мониторинга.
     * @param direct   {@code true} для проверки вхождения в диапазон, {@code false} для проверки отсутствия в диапазоне.
     * @param min      нижняя граница диапазона; верхняя граница диапазона подразумевается {@link Integer#MAX_VALUE).
     */
    SelectionDetector( ObjectProperty<? extends MultipleSelectionModel<T>> property, boolean direct, int min )
    {
        this( property, direct, min, Integer.MAX_VALUE );
    }

    @Override
    protected boolean computeValue()
    {
        int size = PROPERTY.getValue().getSelectedItems().size();
        boolean value = LOW <= size && size <= HIGH;
        return DIRECT ? value : !value;
    }

}
