package com.varankin.brains.jfx.browser;

import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.artificial.Элемент;
import com.varankin.property.MonitoredSet;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Николай
 */
class BrowserMonitor implements PropertyChangeListener
{
    private static final Logger LOGGER = Logger.getLogger( BrowserMonitor.class.getName() );
    
    private final BrowserNode УЗЕЛ;
    private final BrowserNodeBuilder СТРОИТЕЛЬ;

    BrowserMonitor( BrowserNode узел, BrowserNodeBuilder строитель )
    {
        УЗЕЛ = узел;
        СТРОИТЕЛЬ = строитель;
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        switch( evt.getPropertyName() )
        {
            case MonitoredSet.PROPERTY_ADDED:
                Platform.runLater( new OnElementAdded( (Элемент)evt.getNewValue() ) );
                break;

            case MonitoredSet.PROPERTY_REMOVED:
                Platform.runLater( new OnElementRemoved( (Элемент)evt.getOldValue() ) );
                break;

            case Процесс.СОСТОЯНИЕ:
                Элемент элемент = УЗЕЛ.getValue();
                Object source = evt.getSource();
                Процесс.Состояние состояние;
                if( !элемент.equals( source ) && элемент instanceof Процесс )
                {
                    состояние = ((Процесс)элемент).getPropertyValue( Процесс.СОСТОЯНИЕ );
                    PropertyChangeListener наблюдатель = УЗЕЛ.наблюдатель();
//                    if( наблюдатель != null )
//                        наблюдатель.propertyChange( evt );
                }
                else
                    состояние = (Процесс.Состояние)evt.getNewValue();
                Platform.runLater( new OnStatusChangeded( состояние ) );
                break;

            default:
                LOGGER.log( Level.SEVERE, "Unsupported change to node received: {0}", evt.getPropertyName() );
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
        private final Элемент ЭЛЕМЕНТ;

        OnElementAdded( Элемент элемент )
        {
            ЭЛЕМЕНТ = элемент;
        }

        @Override
        public void run()
        {
            BrowserNode узел = СТРОИТЕЛЬ.узел( ЭЛЕМЕНТ );
            УЗЕЛ.getChildren().add( узел );
            узел.expand( СТРОИТЕЛЬ, узел.addMonitor( СТРОИТЕЛЬ, УЗЕЛ.наблюдатель() ) );
        }

    };

    private class OnElementRemoved implements Runnable
    {
        private final Элемент ЭЛЕМЕНТ;

        OnElementRemoved( Элемент элемент )
        {
            ЭЛЕМЕНТ = элемент;
        }

        @Override
        public void run()
        {
            TreeItem<Элемент> удаляемый = null;
            for( TreeItem<Элемент> узел : УЗЕЛ.getChildren() )
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

    private static <T> void removeTreeItemChildren( TreeItem<T> ti )
    {
        if( !ti.isLeaf() )
        {
            for( TreeItem<T> c : ti.getChildren() )
                removeTreeItemChildren( c );
            ti.getChildren().clear();
            if( ti instanceof BrowserNode )
                ((BrowserNode)ti).removeMonitor();
        }
    }

}
