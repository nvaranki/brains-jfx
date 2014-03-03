package com.varankin.brains.jfx;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WeakChangeListener;
import javafx.beans.value.WritableValue;

/**
 * Пара несимметрично связанных значений "a" и "b".
 * Эта связка автоматически передает изменения от "a"
 * к "b". В отличие от связи по 
 * {@link Property#bind(javafx.beans.value.ObservableValue) },
 * связка допускает независимую установку значений в паре.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class PropertyGate<T>
{
    private static final WritableValue DUMMY = new SimpleObjectProperty();
    
    private WritableValue<T> valueA = DUMMY;
    private WritableValue<T> valueB = DUMMY;
    private ValueSetter<T> agent;

    /**
     * Устанавливает автоматическое обновление значения "b" при изменении "a".
     * 
     * @param a связываемое значение.
     * @param b связываемое значение.
     */
    public void bind( Property<T> a, WritableValue<T> b )
    {
        valueA = a;
        valueB = b;
        agent = new ValueSetter<>( b );
        a.addListener( new WeakChangeListener<>( agent ) );
        b.setValue( a.getValue() );
    }
    
    /**
     * Переустанавливает значение "b" значением "a".
     */
    public void forceReset()
    {
        // сбросить прежнее значение
        valueB.setValue( null );
        // установить новое значение
        valueB.setValue( valueA.getValue() );
    }
    
    /**
     * Устанавливает значение "a" значением "b", если оно - отличающиеся.
     */
    public void pullDistinctValue()
    {
        T oldValue = valueA.getValue();
        T newValue = valueB.getValue();
        if( newValue != null && !newValue.equals( oldValue ) )
            valueA.setValue( newValue );
    }
    
}
