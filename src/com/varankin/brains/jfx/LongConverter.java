package com.varankin.brains.jfx;

import com.varankin.util.LoggerX;
import javafx.scene.Node;
import javafx.util.StringConverter;

/**
 * Реализация {@linkplain StringConverter конвертера} для {@link Long}.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public class LongConverter extends StringConverter<Long>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( LongConverter.class );

    private final Node stringHolder;

    /**
     * @param node владелец значения в виде {@link String}.
     */
    public LongConverter( Node node )
    {
        this.stringHolder = node;
    }

    @Override
    public String toString( Long value )
    {
        return value != null ? value.toString() : null;
    }

    @Override
    public Long fromString( String string )
    {
        Long value;
        try
        {
            value = Long.valueOf( string );
        } 
        catch( NumberFormatException __ )
        {
            if( string != null && !string.isEmpty() )
                LOGGER.log( "002001001W", string );
            value = null;
        }
        highlight( value );
        return value;
    }
    
    protected void highlight( Long value )
    {
        stringHolder.setStyle( value != null ? null : JavaFX.STYLE_WRONG_TEXT_VALUE );
    }
    
}
