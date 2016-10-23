package com.varankin.brains.jfx.history;

import java.io.*;
import java.util.Objects;

/**
 * Поставщик {@linkplain Serializable сериализуемых} объектов, пригодный для использования в 
 * {@linkplain com.varankin.history.HistoryList списке хранения истории}.
 * @param <T> класс вложенного объекта.
 *
 * @author &copy; 2016 Николай Варанкин
 */
public final class ObjectProvider<T extends Serializable> implements SerializableHolder<T>
{
    private final T объект;

    public ObjectProvider( T объект )
    {
        this.объект = объект;
    }

    @Override
    public T getInstance()
    {
        return объект;
    }

    @Override
    public boolean equals( Object o )
    {
        return o instanceof ObjectProvider &&
            Objects.equals( объект, ((ObjectProvider)o).объект );
    }

    @Override
    public int hashCode()
    {
        int hash = ObjectProvider.class.hashCode() ^ Objects.hashCode( объект );
        return hash;
    }
    
    @Override
    public String toString()
    {
        return объект != null ? объект.toString() : "";
    }
    
}
