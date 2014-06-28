package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.Архив;
import com.varankin.brains.db.Атрибутный;
import com.varankin.brains.db.Проект;
import com.varankin.brains.jfx.JavaFX;
import java.util.ArrayList;
import java.util.Collection;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Николай
 */
class CellАтрибутный extends TreeCell<Атрибутный>
{
    Collection<МониторКоллекции> мониторы = new ArrayList<>();

    
    
//    @Override
//    public void updateTreeItem( TreeItem<Атрибутный> item )
//    {
//        
//    }
    
    @Override
    public void updateItem( Атрибутный item, boolean empty )
    {
        super.updateItem( item, empty );
        //setText( empty ? null : название( item ) ); //TODO Транзакция!!!!!!
        //textProperty().bind( ? );
        if( empty )
        {
//            if( item instanceof Архив )
//            {
//                Архив архив = (Архив)item;
//                архив.пакеты().наблюдатели().removeAll( мониторы );
//                архив.namespaces().наблюдатели().removeAll( мониторы );
//            }
//            мониторы.clear();
            setText( null );
            setGraphic( null );
        }
        else
        {
            final TreeItem<Атрибутный> treeItem = getTreeItem();
            if( treeItem == null )
            {
                return;
            }
            // initiate check of name and children
            //TODO fails to reset value back! setText( "Loading..." ); textProperty().setValue( "Loading..." );
            //setText( item.getClass().getSimpleName() );
            setGraphic( treeItem.getGraphic() );
            Task task = new CellUpdateTask( this );
            JavaFX.getInstance().getExecutorService().submit( task );
            
//            if( !мониторы.isEmpty() )
//            {
//                return;
//            }
//            ObservableList<TreeItem<Атрибутный>> children = treeItem.getChildren();
//            if( item instanceof Архив )
//            {
//                Архив архив = (Архив)item;
//                МониторКоллекции мк;
//                мк = new МониторКоллекции( children );
//                if( архив.пакеты().наблюдатели().add( мк ) )
//                {
//                    мониторы.add( мк );
//                }
//                мк = new МониторКоллекции( children );
//                if( архив.namespaces().наблюдатели().add( мк ) )
//                {
//                    мониторы.add( мк );
//                }
//            }
        }
    }
    
}
