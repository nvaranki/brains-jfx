package com.varankin.brains.jfx.shared;

import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import javafx.scene.control.ListCell;

/**
 * Локализованная ячейка класса {@link Enum}.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class EnumCell<T extends Enum<T>> extends ListCell<T>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( EnumCell.class );
    private static final ResourceBundle RB = LOGGER.getLogger().getResourceBundle();

    private final String PREFIX;
    
    public EnumCell( Class<T> c )
    {
        PREFIX = c.getSimpleName() + '.';
    }

    @Override
    protected void updateItem( T item, boolean empty )
    {
        super.updateItem( item, empty );
        if( item != null )
        {
            String key = PREFIX + item.name();
            if( RB.containsKey( key ) )
            {
                setText( RB.getString( key ) );
            }
        }
    }
    
}
