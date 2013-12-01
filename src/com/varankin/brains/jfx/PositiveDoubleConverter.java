package com.varankin.brains.jfx;

import com.varankin.util.LoggerX;
import javafx.scene.Node;
import javafx.util.StringConverter;

/**
 * Реализация {@linkplain StringConverter конвертера} для 
 * положительного {@link Double}.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public class PositiveDoubleConverter extends DoubleConverter
{
    private static final LoggerX LOGGER = LoggerX.getLogger( PositiveDoubleConverter.class );

    /**
     * @param node владелец значения в виде {@link String}.
     */
    public PositiveDoubleConverter( Node node )
    {
        super( node );
    }

    @Override
    public Double fromString( String string )
    {
        Double value = super.fromString( string );
        if( value != null && value <= 0d )
        {
            LOGGER.log( "002001004W", value );
            highlight( value = null );
        }
        return value;
    }
    
}
