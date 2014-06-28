package com.varankin.brains.jfx.archive;

import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.artificial.Элемент;
import com.varankin.brains.db.Атрибутный;
import com.varankin.brains.factory.Proxy;
import com.varankin.property.MonitoredCollection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Николай
 */
class МониторКоллекции implements PropertyChangeListener
{
    private static final Logger LOGGER = Logger.getLogger( МониторКоллекции.class.getName() );

    final ObservableList<TreeItem<Атрибутный>> ITEMS;

    МониторКоллекции( ObservableList<TreeItem<Атрибутный>> items )
    {
        ITEMS = items;
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        switch( evt.getPropertyName() )
        {
            case MonitoredCollection.PROPERTY_ADDED:
                Platform.runLater( new OnElementAdded( (Атрибутный)evt.getNewValue() ) );
                break;

            case MonitoredCollection.PROPERTY_REMOVED:
                Platform.runLater( new OnElementRemoved( (Атрибутный)evt.getOldValue() ) );
                break;

//            case Процесс.СОСТОЯНИЕ:
//                Элемент элемент = УЗЕЛ.getValue();
//                if( элемент instanceof Процесс || 
//                        элемент instanceof Proxy && ((Proxy)элемент).оригинал() instanceof Процесс )
//                    Platform.runLater( new BrowserMonitor.OnStatusChangeded( 
//                            (Процесс.Состояние)evt.getNewValue() ) );
//                break;

            default:
                LOGGER.log( Level.FINE, "Unsupported change to node received: {0}", evt.getPropertyName() );
        }            
    }
    
    private class OnElementAdded implements Runnable
    {
        private final Атрибутный ЭЛЕМЕНТ;

        OnElementAdded( Атрибутный элемент )
        {
            ЭЛЕМЕНТ = элемент;
        }

        @Override
        public void run()
        {
//            BrowserNode узел = СТРОИТЕЛЬ.узел( ЭЛЕМЕНТ );
//            ITEMS.add( СТРОИТЕЛЬ.позиция( узел, ITEMS ), узел );
//            узел.expand( СТРОИТЕЛЬ );
        }

    };

    private class OnElementRemoved implements Runnable
    {
        private final Атрибутный ЭЛЕМЕНТ;

        OnElementRemoved( Атрибутный элемент )
        {
            ЭЛЕМЕНТ = элемент;
        }

        @Override
        public void run()
        {
            TreeItem<Атрибутный> удаляемый = null;
            for( TreeItem<Атрибутный> узел : ITEMS )
                if( ЭЛЕМЕНТ.equals( узел.getValue() ) )
                {
                    удаляемый = узел;
                    break;
                }
            if( удаляемый != null )
            {
//                removeTreeItemChildren( удаляемый );
//                ITEMS.remove( удаляемый );
            }
        }
    };

}
