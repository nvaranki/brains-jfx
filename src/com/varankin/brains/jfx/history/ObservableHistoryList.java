package com.varankin.brains.jfx.history;

import com.varankin.history.HistoryList;
import java.io.Serializable;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;

/**
 * Наблюдаемый {@linkplain HistoryList список хранения истории} использования элементов.
 * Первая часть списка содержит неизменяемые элементы.
 * Вторая часть содержит динамичные элементы истории.
 * Любой элемент списка можно сделать приоритетным в истории, 
 * при этом остальные элементы истории сдвигаются в конец списка.
 * Список элементов оперативно сохраняется в локальном хранилище
 * пользователя операционной системы. Список восстанавливается
 * при создании элемента данного класса.
 *
 * @author &copy; 2016 Николай Варанкин
 * 
 * @param <T> класс элементов списка; обязан реализовать интерфейс {@link Serializable}
 *              для успешного сохранения/восстановления в хранилище.
 */
public final class ObservableHistoryList<T extends Serializable> 
        extends HistoryList<T> implements Observable
{
    private final Observable observable;

    public ObservableHistoryList( ObservableList<T> base, Class<?> owner )
    {
        super( base, owner );
        observable = base;
    }

    @Override
    public void addListener( InvalidationListener listener )
    {
        observable.addListener( listener );
    }

    @Override
    public void removeListener( InvalidationListener listener )
    {
        observable.removeListener( listener );
    }
    
}
