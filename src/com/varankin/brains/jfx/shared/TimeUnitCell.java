package com.varankin.brains.jfx.shared;

import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.scene.control.ListCell;

/**
 * Локализованная ячейка класса {@link TimeUnit}.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class TimeUnitCell extends ListCell<TimeUnit>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TimeUnitCell.class );
    private static final ResourceBundle RB = LOGGER.getLogger().getResourceBundle();

    @Override
    protected void updateItem( TimeUnit item, boolean empty )
    {
        super.updateItem( item, empty );
        if( item != null )
        {
            String key = "TimeUnit." + item.name();
            if( RB.containsKey( key ) )
            {
                setText( RB.getString( key ) );
            }
        }
    }
    
}
