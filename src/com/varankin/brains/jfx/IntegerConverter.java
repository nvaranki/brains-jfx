package com.varankin.brains.jfx;

import com.varankin.util.LoggerX;
import javafx.scene.Node;
import javafx.util.StringConverter;

/**
 * Реализация {@linkplain StringConverter конвертера} для {@link Integer}.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public class IntegerConverter extends StringConverter<Integer>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( IntegerConverter.class );

    private final Node stringHolder;

    /**
     * @param node владелец значения в виде {@link String}.
     */
    public IntegerConverter( Node node )
    {
        this.stringHolder = node;
    }

    @Override
    public String toString( Integer value )
    {
        return value != null ? value.toString() : null;
    }

    @Override
    public Integer fromString( String string )
    {
        Integer value;
        try
        {
            value = Integer.valueOf( string );
        } 
        catch( NumberFormatException _ )
        {
            if( string != null && !string.isEmpty() )
                LOGGER.log( "002001001W", string );
            value = null;
        }
        highlight( value );
        return value;
    }
    
    protected void highlight( Integer value )
    {
        stringHolder.setStyle( value != null ? null : JavaFX.STYLE_WRONG_TEXT_VALUE );
    }
    
}
