package com.varankin.brains.jfx.archive;

import com.varankin.util.LoggerX;
import javafx.util.StringConverter;

/**
 *
 * @author Varankine
 * @param <T>
 */
class StringToEnumConverter<T extends Enum<T>> extends StringConverter<T>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( StringToEnumConverter.class );
    
    private final String pref;
    private final T[] values;

    StringToEnumConverter( T[] values, String pref )
    {
        this.values = values;
        this.pref = pref;
    }

    @Override
    public String toString( T object )
    {
        return object != null ? LOGGER.text( pref + ( object != null ? object.ordinal() : "null" ) ) : null;
    }

    @Override
    public T fromString( String string )
    {
        for( T t : values )
            if( t.name().equals( string ) )
                return t;
        LOGGER.getLogger().warning( "Not an Enum object: " + string );
        return null;
    }

}
