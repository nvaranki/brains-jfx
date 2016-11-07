package com.varankin.brains.jfx.db;

import javafx.collections.ObservableList;

/**
 * Оператор над объектом и коллекцией согласованного типа.
 * 
 * @param <R> класс результата выполнения оператора.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
@FunctionalInterface
public interface FxОператор<R>
{
    /**
     * Выполняет данный оператор над объектом и коллекцией.
     * 
     * @param <T>       класс объекта и элементов коллекции.
     * @param объект    объект.
     * @param коллекция коллекция.
     * @return результат выполнения оператора.
     */
    <T> R выполнить( T объект, ObservableList<T> коллекция ); 
}
