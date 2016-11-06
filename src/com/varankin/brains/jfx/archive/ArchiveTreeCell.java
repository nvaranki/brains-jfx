package com.varankin.brains.jfx.archive;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import com.varankin.brains.jfx.db.FxАтрибутный;

/**
 * Ячейка (строка) навигатора.
 *
 * @author &copy; 2016 Николай Варанкин
 */
final class ArchiveTreeCell extends TreeCell<FxАтрибутный>
{
    @Override
    protected void updateItem( FxАтрибутный item, boolean empty ) 
    {
        super.updateItem( item, empty );

        TreeItem<FxАтрибутный> treeItem = getTreeItem();
        if( !empty && item != null && treeItem instanceof AbstractTreeItem ) 
        {
            AbstractTreeItem ati = (AbstractTreeItem)treeItem;
            textProperty()   .bind( ati.textProperty() );
            graphicProperty().bind( ati.graphicProperty() );
            tooltipProperty().bind( ati.tooltipProperty() );
        } else {
            textProperty().unbind();
            setText( null );
            graphicProperty().unbind();
            setGraphic( null );
            tooltipProperty().unbind();
            setTooltip( null );
        }
    }

}
