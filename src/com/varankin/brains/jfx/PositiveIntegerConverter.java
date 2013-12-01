package com.varankin.brains.jfx;

import com.varankin.util.LoggerX;
import javafx.scene.Node;
import javafx.util.StringConverter;

/**
 * Реализация {@linkplain StringConverter конвертера} для 
 * положительного {@link Integer}.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public class PositiveIntegerConverter extends IntegerConverter
{
    private static final LoggerX LOGGER = LoggerX.getLogger( PositiveIntegerConverter.class );

    /**
     * @param node владелец значения в виде {@link String}.
     */
    public PositiveIntegerConverter( Node node )
    {
        super( node );
    }

    @Override
    public Integer fromString( String string )
    {
        Integer value = super.fromString( string );
        if( value != null && value <= 0L )
        {
            LOGGER.log( "002001002W", value );
            highlight( value = null );
        }
        return value;
    }
    
}
