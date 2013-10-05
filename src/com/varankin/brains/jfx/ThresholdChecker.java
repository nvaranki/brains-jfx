package com.varankin.brains.jfx;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;

/**
 * Детектор числа выбранных элементов.
 *
 * @author &copy; 2013 Николай Варанкин
 */
class ThresholdChecker extends BooleanBinding 
{
    private final int LOW;
    private final int HIGH;
    private final boolean DIRECT;
    private final ObservableList<?> LIST;

    /**
     * @param list     объект мониторинга.
     * @param direct   {@code true} для проверки вхождения в диапазон, {@code false} для проверки отсутствия в диапазоне.
     * @param low      нижняя граница диапазона.
     * @param high     верхняя граница диапазона.
     */
    ThresholdChecker( ObservableList<?> list, boolean direct, int low, int high )
    {
        LOW = low;
        HIGH = high;
        DIRECT = direct;
        super.bind( LIST = list );
    }

    @Override
    protected boolean computeValue()
    {
        int size = LIST.size();
        boolean value = LOW <= size && size <= HIGH;
        return DIRECT ? value : !value;
    }

}
