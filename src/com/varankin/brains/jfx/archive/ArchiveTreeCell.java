package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.archive.action.ActionProcessor;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.db.*;
import java.util.Collections;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;

/**
 * Ячейка (строка) навигатора.
 *
 * @author &copy; 2017 Николай Варанкин
 */
final class ArchiveTreeCell extends TreeCell<FxАтрибутный>
{
    private static final SnapshotParameters snapParams;
    private static final TransferMode[] COPY_ONLY = new TransferMode[] { TransferMode.COPY };
    
    static
    {
        snapParams = new SnapshotParameters();
        snapParams.setFill( Color.TRANSPARENT );
    }
    
    ArchiveTreeCell( TreeView<FxАтрибутный> treeView ) {
        setOnDragDetected( e -> 
        {
            FxАтрибутный<?> атрибутный = getTreeItem().getValue();
            if( !( атрибутный instanceof FxАрхив || атрибутный instanceof FxМусор ) )
            {
                Image ss = snapshot( snapParams, null ); //TODO make "unselected" image
                Dragboard dndb = startDragAndDrop( TransferMode.COPY_OR_MOVE );
                dndb.setDragView( ss );
                //String название = атрибутный.тип().getValue().название();
                dndb.setContent( Collections.singletonMap( DataFormat.PLAIN_TEXT, "название" ));
            }
            e.consume();
        } );
        setOnDragDone( e -> 
        {
            if( e.isDropCompleted() )
            {
                e.acceptTransferModes( TransferMode.COPY_OR_MOVE );//TODO check with ActionProcessor
                TransferMode transferMode = e.getTransferMode();
                if( e.getTransferMode() == TransferMode.MOVE )
                {
                    //TODO initiate removal for all
                    TreeItem<FxАтрибутный> treeItem = ArchiveTreeCell.this.getTreeItem();
                    treeItem.getParent().getChildren().remove( treeItem );
                }
            }
            e.consume();
        } );
        setOnDragOver( this::onDragOver );
        setOnDragDropped( this::onDragDropped );
    }

    @FXML
    protected void onDragOver( DragEvent event )
    {
        Object gs = event.getGestureSource();
        if( gs instanceof ArchiveTreeCell )
        {
            ArchiveTreeCell tc = (ArchiveTreeCell)gs;
            FxАтрибутный source = tc.getTreeItem().getValue();
            FxАтрибутный parent = tc.getTreeItem().getParent().getValue(); 
            FxАтрибутный target = getTreeItem().getValue();
            TransferMode[] modes = parent == target ? COPY_ONLY :
                    ActionProcessor.disableActionAdd( target, source ) ? 
                    TransferMode.NONE : TransferMode.COPY_OR_MOVE;
            event.acceptTransferModes( modes );
        }
        else 
        {
            event.acceptTransferModes( TransferMode.NONE );
        }
        event.consume();
    }
    
    @FXML
    protected void onDragDropped( DragEvent event )
    {
        boolean completed = false;
        event.acceptTransferModes( TransferMode.COPY_OR_MOVE ); // affects getAcceptingObject()
        Object ao = event.getAcceptingObject();
        Object gs = event.getGestureSource();
        if( ao instanceof ArchiveTreeCell && gs instanceof ArchiveTreeCell )
        {
            ArchiveTreeCell tcAccepting = (ArchiveTreeCell)ao;
            ArchiveTreeCell tcSource = (ArchiveTreeCell)gs;
            FxАтрибутный valueAccepting = tcAccepting.getTreeItem().getValue();
            FxАтрибутный valueTarget = getTreeItem().getValue();
            FxАтрибутный valueSource = tcSource.getTreeItem().getValue();
            FxАтрибутный valueParent = tcSource.getTreeItem().getParent().getValue();
            if( !( ActionProcessor.disableActionAdd( valueAccepting, valueSource ) 
                    || valueAccepting != valueTarget ))
            {
                JavaFX.getInstance().execute( new TaskCopyOrMoveАтрибутный( 
                    valueSource, valueTarget, event.getTransferMode() == TransferMode.MOVE ?
                        valueParent : null ) );
                completed = true;
            }
        }
        event.setDropCompleted( completed );
        event.consume();
    }
    
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
        } 
        else 
        {
            textProperty().unbind();
            setText( null );
            graphicProperty().unbind();
            setGraphic( null );
            tooltipProperty().unbind();
            setTooltip( null );
        }
    }

}
