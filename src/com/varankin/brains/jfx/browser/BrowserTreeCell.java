package com.varankin.brains.jfx.browser;

import java.util.Collections;
import java.util.logging.Logger;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;

/**
 * Элемент отображения произвольного узла дерева.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public class BrowserTreeCell<T> extends TreeCell<T> 
{
    private static final Logger LOGGER = Logger.getLogger(
            BrowserTreeCell.class.getName(),
            BrowserTreeCell.class.getPackage().getName() + ".text" );

    BrowserTreeCell(TreeView<T> treeView) {
        setOnDragDetected((MouseEvent e) -> {
            LOGGER.fine("C: OnDragDetected");
            SnapshotParameters snapParams = new SnapshotParameters();
            snapParams.setFill(Color.TRANSPARENT);
            Dragboard dndb = startDragAndDrop(TransferMode.LINK);
            dndb.setDragView(snapshot(snapParams, null));
            dndb.setContent(Collections.singletonMap(DataFormat.PLAIN_TEXT, "data"));
            //dragImageView.startFullDrag();
            e.consume();
        });
    } //dragImageView.startFullDrag();

    @Override
    public void updateItem(T элемент, boolean empty) {
        super.updateItem(элемент, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
            setUserData(null);
        } else {
            TreeItem<T> item = getTreeItem();
            setText(item.toString());
            setGraphic(item.getGraphic());
            setUserData(элемент);
        }
    }
    
}
