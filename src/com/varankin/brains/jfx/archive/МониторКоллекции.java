package com.varankin.brains.jfx.archive;

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
import com.varankin.brains.db.DbАтрибутный;

/**
 *
 * @author Николай
 */
class МониторКоллекции implements PropertyChangeListener
{
    private static final Logger LOGGER = Logger.getLogger( МониторКоллекции.class.getName() );

    final ObservableList<TreeItem<DbАтрибутный>> ITEMS;

    МониторКоллекции( ObservableList<TreeItem<DbАтрибутный>> items )
    {
        ITEMS = items;
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        switch( evt.getPropertyName() )
        {
            case Коллекция.PROPERTY_ADDED:
                Platform.runLater(() -> onElementAdded((DbАтрибутный)evt.getNewValue() ) );
                break;

            case Коллекция.PROPERTY_REMOVED:
                Platform.runLater(() -> onElementRemoved((DbАтрибутный)evt.getOldValue() ) );
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
    
    private void onElementAdded( DbАтрибутный элемент )
    {
        CellUpdateTask.вставить( new TitledTreeItem<>( элемент ), ITEMS );
    }
    
    private void onElementRemoved( DbАтрибутный элемент )
    {
        Collection<TreeItem<DbАтрибутный>> удалить = new ArrayList<>();
        for( TreeItem<DbАтрибутный> узел : ITEMS )
            if( элемент.equals( узел.getValue() ) )
                удалить.add( узел );
        
        //removeTreeItemChildren( удаляемый );
        ITEMS.removeAll( удалить );
    }
    
}
