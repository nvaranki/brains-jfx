package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import javafx.scene.*;
import javafx.stage.*;

import static com.varankin.brains.jfx.archive.MultiplyController.*;
import javafx.event.ActionEvent;

/**
 * Диалог для выбора и установки параметров дублирования элементов.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
final class MultiplyStage extends Stage
{
    private final MultiplyController controller;
    
    MultiplyStage()
    {
        BuilderFX<Parent,MultiplyController> builder = new BuilderFX<>();
        builder.init( MultiplyController.class, RESOURCE_FXML, RESOURCE_BUNDLE );
        controller = builder.getController();
        
        getIcons().add( JavaFX.icon( "icons16x16/multiply.png" ).getImage() );
        
        setResizable( true );
        setMinHeight( 450d );
        setMinWidth( 330d );
        setHeight( 450d ); //TODO save/restore size&pos
        setWidth( 330d );
        setScene( new Scene( builder.getNode() ) );
        setOnCloseRequest( e -> controller.onActionCancel( new ActionEvent() ) );
    }
    /**
     * @return схема копирования или {@code null}.
     */
    TaskMultiplyАтрибутный.Схема getSchema()
    {
        return controller.getSchema();
    }
    
}
