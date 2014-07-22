package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.Атрибутный;
import com.varankin.brains.db.Коллекция;
import com.varankin.brains.jfx.TitledTreeItem;
import com.varankin.property.MonitoredCollection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.*;
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
            case Коллекция.PROPERTY_ADDED:
                Platform.runLater( () -> onElementAdded( (Атрибутный)evt.getNewValue() ) );
                break;

            case Коллекция.PROPERTY_REMOVED:
                Platform.runLater( () -> onElementRemoved( (Атрибутный)evt.getOldValue() ) );
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
    
    private void onElementAdded( Атрибутный элемент )
    {
        CellUpdateTask.вставить( new TitledTreeItem<>( элемент ), ITEMS );
    }
    
    private void onElementRemoved( Атрибутный элемент )
    {
        Collection<TreeItem<Атрибутный>> удалить = new ArrayList<>();
        for( TreeItem<Атрибутный> узел : ITEMS )
            if( элемент.equals( узел.getValue() ) )
                удалить.add( узел );
        
        //removeTreeItemChildren( удаляемый );
        ITEMS.removeAll( удалить );
    }
    
}
