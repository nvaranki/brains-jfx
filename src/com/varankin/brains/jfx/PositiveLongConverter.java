package com.varankin.brains.jfx;

import com.varankin.util.LoggerX;
import javafx.scene.Node;
import javafx.util.StringConverter;

/**
 * Реализация {@linkplain StringConverter конвертера} для 
 * положительного {@link Long}.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public class PositiveLongConverter extends LongConverter
{
    private static final LoggerX LOGGER = LoggerX.getLogger( PositiveLongConverter.class );

    /**
     * @param node владелец значения в виде {@link String}.
     */
    public PositiveLongConverter( Node node )
    {
        super( node );
    }

    @Override
    public Long fromString( String string )
    {
        Long value = super.fromString( string );
        if( value != null && value <= 0L )
        {
            LOGGER.log( "002001002W", value );
            highlight( value = null );
        }
        return value;
    }
    
}
