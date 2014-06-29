package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.Атрибутный;
import com.varankin.brains.jfx.JavaFX;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.*;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;

/**
 * Ячейка (строка) навигатора.
 *
 * @author &copy; 2014 Николай Варанкин
 */
class CellАтрибутный extends TreeCell<Атрибутный>
{
    private static final Logger LOGGER = Logger.getLogger( CellАтрибутный.class.getName() );
    Collection<МониторКоллекции> мониторы = new ArrayList<>();

    @Override
    public void updateItem( Атрибутный item, boolean empty )
    {
        super.updateItem( item, empty );

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
            LOGGER.log( Level.FINEST, "Updating at index {0}", getIndex() );
            // initiate check of name and children
            //TODO fails to reset value back! setText( "Loading..." ); textProperty().setValue( "Loading..." );
            setGraphic( treeItem.getGraphic() );

            JavaFX.getInstance().getExecutorService().submit( new CellUpdateTask( this ) );
            
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
