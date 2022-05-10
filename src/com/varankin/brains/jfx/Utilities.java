package com.varankin.brains.jfx;

import javafx.beans.value.WritableValue;

/**
 * Общие методы.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public class Utilities
{

    private Utilities() {}
    
    public static <T> void applyDistinct( WritableValue<T> valueN, WritableValue<T> valueO )
    {
        T oldValue = valueO.getValue();
        T newValue = valueN.getValue();
        if( newValue != null && !newValue.equals( oldValue ) )
            valueO.setValue( newValue );
    }
    
    public static <T> void copy( WritableValue<T> valueFrom, WritableValue<T> valueTo )
    {
        valueTo.setValue( valueFrom.getValue() );
    }
    
}
