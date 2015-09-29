package com.varankin.brains.jfx.browser;

import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.factory.Вложенный;
import com.varankin.brains.artificial.Элемент;
import com.varankin.property.MonitoredCollection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.*;
import javafx.application.Platform;

/**
 * Монитор {@linkplain BrowserNode узла}.
 * 
 * @author &copy; 2015 Николай Варанкин
 */
class BrowserMonitor<T> implements PropertyChangeListener
{
    private static final Logger LOGGER = Logger.getLogger( BrowserMonitor.class.getName() );
    
    private final BrowserNode<T> УЗЕЛ;

    BrowserMonitor( BrowserNode<T> узел )
    {
        УЗЕЛ = узел;
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        switch( evt.getPropertyName() )
        {
            case MonitoredCollection.PROPERTY_ADDED:
                Platform.runLater( () -> УЗЕЛ.вставить( (T)evt.getNewValue() ) ); //TODO cast
                break;

            case MonitoredCollection.PROPERTY_REMOVED:
                Platform.runLater( () -> УЗЕЛ.удалить( (T)evt.getOldValue() ) ); //TODO cast
                break;

            case Процесс.СОСТОЯНИЕ:
                T value = УЗЕЛ.getValue();
                Процесс процесс = Вложенный.извлечь( Процесс.class, (Элемент)value );
                if( процесс != null )
                    Platform.runLater( () -> УЗЕЛ.раскрасить( (Процесс.Состояние)evt.getNewValue() ) );
                break;

            default:
                LOGGER.log( Level.FINE, "Unsupported change to node received: {0}", evt.getPropertyName() );
        }            
    }

}
