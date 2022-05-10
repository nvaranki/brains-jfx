package com.varankin.brains.jfx.analyser.rating;

/**
 *
 * @author Николай
 */
public interface Ранжируемый<T>
{
    float значение( T сигнал );
}
