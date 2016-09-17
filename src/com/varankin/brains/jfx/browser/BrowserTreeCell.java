package com.varankin.brains.jfx.browser;

import java.util.Collections;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;

/**
 * Элемент отображения произвольного узла дерева.
 * 
 * @param <T> класс отображаемого объекта.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public class BrowserTreeCell<T> extends TreeCell<T> 
{
    private static final SnapshotParameters snapParams;
    
    static
    {
        snapParams = new SnapshotParameters();
        snapParams.setFill( Color.TRANSPARENT );
    }
    
    BrowserTreeCell( TreeView<T> treeView ) {
        setOnDragDetected( e -> {
            WritableImage ss = snapshot( snapParams, null ); //TODO make "unselected" image
            Dragboard dndb = startDragAndDrop( TransferMode.LINK );
            dndb.setDragView( ss );
            dndb.setContent( Collections.singletonMap( DataFormat.PLAIN_TEXT, "data" ));
            e.consume();
        });
    }

    @Override
    public void updateItem(T элемент, boolean empty) {
        super.updateItem(элемент, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
            setUserData(null);
        } else {
            TreeItem<T> item = getTreeItem();
            setText(item.toString()); //TODO null after project deleted
            setGraphic(item.getGraphic());
            setUserData(элемент);
        }
    }
    
}
