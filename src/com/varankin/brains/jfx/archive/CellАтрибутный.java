package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.property.PropertyMonitor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.*;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import com.varankin.brains.db.DbАтрибутный;

/**
 * Ячейка (строка) навигатора.
 *
 * @author &copy; 2014 Николай Варанкин
 */
class CellАтрибутный extends TreeCell<DbАтрибутный>
{
    private static final Logger LOGGER = Logger.getLogger( CellАтрибутный.class.getName() );
    
    static final Object PCL = new Object(), CCPCL = new Object();

    @Override
    public void updateItem( DbАтрибутный item, boolean empty )
    {
        super.updateItem( item, empty );

        if( empty )
        {
            setText( null );
            setGraphic( null );
            Object pcl = getProperties().remove( PCL );
            Object ccpcl = getProperties().remove( CCPCL );
            if( ccpcl instanceof Collection )
                for( Object pm : (Collection)ccpcl ) //TODO java.util.ConcurrentModificationException
                    if( pm instanceof PropertyMonitor )
                        ((PropertyMonitor)pm).listeners().remove( pcl );
        }
        else
        {
            final TreeItem<DbАтрибутный> treeItem = getTreeItem();
            LOGGER.log( Level.FINEST, "Updating at index {0}", getIndex() );
            // initiate check of name and children
            //TODO fails to reset value back! setText( "Loading..." ); textProperty().setValue( "Loading..." );
            setGraphic( treeItem.getGraphic() );
            if( !getProperties().containsKey( PCL ) ) 
                getProperties().put( PCL, new МониторКоллекции( treeItem.getChildren() ) );
            if( !getProperties().containsKey( CCPCL ) ) 
                getProperties().put( CCPCL, new ArrayList<>() );
            JavaFX.getInstance().getExecutorService().submit( new CellUpdateTask( this ) );
        }
    }
    
}
