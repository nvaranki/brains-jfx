package com.varankin.brains.jfx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;

/**
 *
 * @author Николай
 */
public class ValueSetter<T> implements ChangeListener<T>
{
    private final WritableValue<T> target;

    public ValueSetter( WritableValue<T> target )
    {
        this.target = target;
    }

    @Override
    public void changed( ObservableValue<? extends T> observable, T oldValue, T newValue )
    {
        target.setValue( newValue );
    }
    
}
