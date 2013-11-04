package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import java.util.List;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;

/**
 * Графическая зона для рисования отметок. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
class DrawAreaPane extends Pane
{
    @Deprecated private final ContextMenu popup;

    DrawAreaPane( TimeLineController controller )
    {
        popup = new ContextMenu();
        
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio( true );
        imageView.imageProperty().bind( controller.writableImageProperty() );
        getChildren().add( imageView );
        
        setOnMouseClicked( new ContextMenuRaiser( popup, DrawAreaPane.this ) );
    }
    
    @Deprecated 
    void appendToPopup( List<MenuItem> items ) 
    {
        JavaFX.copyMenuItems( items, popup.getItems(), true );
    }
    
}
