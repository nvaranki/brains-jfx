package com.varankin.brains.jfx.browser;

import com.varankin.characteristic.Изменение;
import com.varankin.characteristic.Наблюдатель;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 *
 * @author Varankine
 */
class BrowserObserver<T> implements Наблюдатель<T> 
{
    private static final Logger LOGGER = Logger.getLogger( BrowserMonitor.class.getName() );
    
    private final BrowserNode<T> УЗЕЛ;

    BrowserObserver( BrowserNode<T> узел )
    {
        УЗЕЛ = узел;
    }

    @Override
    public void отклик( Изменение<T> изменение ) 
    {
        if( изменение.ПРЕЖНЕЕ == null )
        {
            if( изменение.АКТУАЛЬНОЕ == null )
                LOGGER.log( Level.FINE, "Узел изменен null -> null." );
            else
                Platform.runLater( () -> УЗЕЛ.вставить( изменение.АКТУАЛЬНОЕ ) );
        }
        else if( изменение.АКТУАЛЬНОЕ == null )
        {
            Platform.runLater( () -> УЗЕЛ.удалить( изменение.ПРЕЖНЕЕ ) );
        }
        else
        {
            LOGGER.log( Level.FINE, "Узел изменен." );//TODO updated
        }
//        
//        switch( evt.getPropertyName() )
//        {
//            case MonitoredCollection.PROPERTY_ADDED:
//                break;
//
//            case MonitoredCollection.PROPERTY_REMOVED:
//                break;
//
//            case Процесс.СОСТОЯНИЕ:
//                T value = УЗЕЛ.getValue();
//                Процесс процесс;
//                if( value instanceof RtЭлемент )
//                    процесс = RtЭлемент.извлечь( Процесс.class, (RtЭлемент)value );
//                else
//                    процесс = Вложенный.извлечь( Процесс.class, (Элемент)value );
//                if( процесс != null )
//                    Platform.runLater( () -> УЗЕЛ.раскрасить( (Процесс.Состояние)evt.getNewValue() ) );
//                break;
//
//            default:
//                LOGGER.log( Level.FINE, "Unsupported change to node received: {0}", evt.getPropertyName() );
//        }            
    }
    
}
