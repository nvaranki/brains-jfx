package com.varankin.brains.jfx;

import com.varankin.util.LoggerX;
import javafx.scene.Node;
import javafx.util.StringConverter;

/**
 * Реализация {@linkplain StringConverter конвертера} для {@link Double}.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public class DoubleConverter extends StringConverter<Double>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( DoubleConverter.class );

    private final Node stringHolder;

    /**
     * @param node владелец значения в виде {@link String}.
     */
    public DoubleConverter( Node node )
    {
        this.stringHolder = node;
    }

    @Override
    public String toString( Double value )
    {
        return value != null ? value.toString() : null;
    }

    @Override
    public Double fromString( String string )
    {
        Double value;
        try
        {
            value = Double.valueOf( string );
        } 
        catch( NumberFormatException _ )
        {
            if( string != null && !string.isEmpty() )
                LOGGER.log( "002001003W", string );
            value = null;
        }
        highlight( value );
        return value;
    }
    
    protected void highlight( Double value )
    {
        stringHolder.setStyle( value != null ? null : JavaFX.STYLE_WRONG_TEXT_VALUE );
    }
    
}
