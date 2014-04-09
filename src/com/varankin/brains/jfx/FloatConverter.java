package com.varankin.brains.jfx;

import com.varankin.util.LoggerX;
import javafx.scene.Node;
import javafx.util.StringConverter;

/**
 * Реализация {@linkplain StringConverter конвертера} для {@link Float}.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public class FloatConverter extends StringConverter<Float>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( FloatConverter.class );

    private final Node stringHolder;

    /**
     * @param node владелец значения в виде {@link String}.
     */
    public FloatConverter( Node node )
    {
        this.stringHolder = node;
    }

    @Override
    public String toString( Float value )
    {
        return value != null ? value.toString() : null;
    }

    @Override
    public Float fromString( String string )
    {
        Float value;
        try
        {
            value = Float.valueOf( string );
        } 
        catch( NumberFormatException __ )
        {
            if( string != null && !string.isEmpty() )
                LOGGER.log( "002001003W", string );
            value = null;
        }
        highlight( value );
        return value;
    }
    
    protected void highlight( Float value )
    {
        stringHolder.setStyle( value != null ? null : JavaFX.STYLE_WRONG_TEXT_VALUE );
    }
    
}
