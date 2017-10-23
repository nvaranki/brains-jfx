package com.varankin.brains.jfx.archive;

import java.util.function.Function;
import javafx.util.StringConverter;

/**
 *
 * 
 * @author &copy; 2017 Николай Варанкин
 */
class ToStringConverter<T> extends StringConverter<T>
{
    
    final Function<String, T> f;

    ToStringConverter( Function<String, T> f )
    {
        this.f = f;
    }

    @Override
    public String toString( T object )
    {
        return object != null ? object.toString() : "";
    }

    @Override
    public T fromString( String string )
    {
        return string == null || string.trim().isEmpty() ? null : f.apply( string );
    }
    
}
