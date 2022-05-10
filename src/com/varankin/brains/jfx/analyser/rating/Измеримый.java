package com.varankin.brains.jfx.analyser.rating;

/**
 * Объект, обладающий свойством численной измеримости.
 * 
 * @param <T> тип измеряемого значения.
 *
 * @author &copy; 2014 Николай Варанкин
 */
public interface Измеримый<T extends Number>
{
    T значение();
}
