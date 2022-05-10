package com.varankin.brains.jfx.history;

import com.varankin.io.container.Holder;
import java.io.Serializable;

/**
 * Поставщик объектов, пригодный для использования в 
 * {@linkplain com.varankin.history.HistoryList списке хранения истории}.
 * @param <T> класс вложенного объекта.
 *
 * @author &copy; 2016 Николай Варанкин
 */
public interface SerializableHolder<T> 
        extends Holder<T>, Serializable
{
    
}
