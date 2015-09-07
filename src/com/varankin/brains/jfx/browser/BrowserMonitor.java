package com.varankin.brains.jfx.browser;

import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.factory.Вложенный;
import com.varankin.brains.artificial.Элемент;
import com.varankin.brains.factory.runtime.RtЭлемент;
import com.varankin.property.MonitoredCollection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;

/**
 * Монитор {@linkplain BrowserNode узла}.
 * 
 * @author &copy; 2015 Николай Варанкин
 */
class BrowserMonitor<T> implements PropertyChangeListener
{
    private static final Logger LOGGER = Logger.getLogger( BrowserMonitor.class.getName() );
    
    private final BrowserNode<T> УЗЕЛ;
    private final BrowserNodeBuilder<T> СТРОИТЕЛЬ;

    BrowserMonitor( BrowserNode<T> узел, BrowserNodeBuilder<T> строитель )
    {
        УЗЕЛ = узел;
        СТРОИТЕЛЬ = строитель;
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        switch( evt.getPropertyName() )
        {
            case MonitoredCollection.PROPERTY_ADDED:
                Platform.runLater( new OnElementAdded( (T)evt.getNewValue() ) ); //TODO cast
                break;

            case MonitoredCollection.PROPERTY_REMOVED:
                Platform.runLater( new OnElementRemoved( (T)evt.getOldValue() ) ); //TODO cast
                break;

            case Процесс.СОСТОЯНИЕ:
                T value = УЗЕЛ.getValue();
                Процесс процесс;
                if( value instanceof RtЭлемент )
                    процесс = RtЭлемент.извлечь( Процесс.class, (RtЭлемент)value );
                else
                    процесс = Вложенный.извлечь( Процесс.class, (Элемент)value );
                if( процесс != null )
                    Platform.runLater( new OnStatusChangeded( 
                            (Процесс.Состояние)evt.getNewValue() ) );
                break;

            default:
                LOGGER.log( Level.FINE, "Unsupported change to node received: {0}", evt.getPropertyName() );
        }            
    }

    private class OnStatusChangeded implements Runnable
    {
        final Процесс.Состояние СОСТОЯНИЕ;

        OnStatusChangeded( Процесс.Состояние состояние )
        {
            СОСТОЯНИЕ = состояние;
        }

        @Override
        public void run()
        {
            СТРОИТЕЛЬ.фабрикаКартинок().setBgColor( УЗЕЛ.getGraphic(), СОСТОЯНИЕ );
        }
    }

    private class OnElementAdded implements Runnable
    {
        private final T ЭЛЕМЕНТ;

        OnElementAdded( T элемент )
        {
            ЭЛЕМЕНТ = элемент;
        }

        @Override
        public void run()
        {
            BrowserNode узел = СТРОИТЕЛЬ.узел( ЭЛЕМЕНТ );
            УЗЕЛ.getChildren().add( СТРОИТЕЛЬ.позиция( узел, УЗЕЛ.getChildren() ), узел );
            узел.expand( СТРОИТЕЛЬ );
        }

    };

    private class OnElementRemoved implements Runnable
    {
        private final T ЭЛЕМЕНТ;

        OnElementRemoved( T элемент )
        {
            ЭЛЕМЕНТ = элемент;
        }

        @Override
        public void run()
        {
            TreeItem<T> удаляемый = null;
            for( TreeItem<T> узел : УЗЕЛ.getChildren() )
                if( ЭЛЕМЕНТ.equals( узел.getValue() ) )
                {
                    удаляемый = узел;
                    break;
                }
            if( удаляемый != null )
            {
                removeTreeItemChildren( удаляемый );
                УЗЕЛ.getChildren().remove( удаляемый );
            }
        }
    };

    private static <T> void removeTreeItemChildren( TreeItem<T> узел )
    {
        if( !узел.isLeaf() )
        {
            for( TreeItem<T> c : узел.getChildren() )
                removeTreeItemChildren( c );
            узел.getChildren().clear();
        }
        if( узел instanceof BrowserNode )
            ((BrowserNode)узел).removeMonitor();
    }

}
