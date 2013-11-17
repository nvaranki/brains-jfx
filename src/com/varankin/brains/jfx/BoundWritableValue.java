package com.varankin.brains.jfx;

import javafx.beans.property.Property;
import javafx.beans.value.WeakChangeListener;
import javafx.beans.value.WritableValue;

/**
 * Завернутое {@linkplain WritableValue изменяемое значение} (источник), 
 * с "мягкой" связкой с другим изменяемым значением (приемник).
 * Эта связка автоматически передает изменения от источника
 * к приемнику. В отличие от связи по 
 * {@link Property#bind(javafx.beans.value.ObservableValue) },
 * связка допускает независимую установку значений как на источнике,
 * так и на приемнике.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class BoundWritableValue<T> implements WritableValue<T>
{
    private final WritableValue<T> ИСТОЧНИК;
    private final ValueSetter<T> АГЕНТ;

    public BoundWritableValue( Property<T> источник, WritableValue<T> приемник )
    {
        ИСТОЧНИК = источник;
        АГЕНТ = new ValueSetter<>( приемник );
        источник.addListener( new WeakChangeListener<>( АГЕНТ ) );
    }

    @Override
    public T getValue()
    {
        return ИСТОЧНИК.getValue();
    }

    @Override
    public void setValue( T value )
    {
        ИСТОЧНИК.setValue( value );
    }
    
}
