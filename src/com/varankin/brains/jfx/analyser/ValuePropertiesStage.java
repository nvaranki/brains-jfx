package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import java.util.function.Consumer;
import javafx.scene.*;
import javafx.stage.*;

import static com.varankin.brains.jfx.analyser.ValuePropertiesController.*;

/**
 * Диалог для выбора и установки параметров рисования отметок.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
final class ValuePropertiesStage extends Stage
{
    private final ValuePropertiesController controller;

    ValuePropertiesStage( Consumer<Value> action )
    {
        BuilderFX<Parent,ValuePropertiesController> builder = new BuilderFX<>();
        builder.init( ValuePropertiesController.class, RESOURCE_FXML, RESOURCE_BUNDLE );
        controller = builder.getController();
        controller.setAction( action );
        
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );

        setResizable( true );
        setMinHeight( 220d );
        setMinWidth( 400d );
        setHeight( 220d ); //TODO save/restore size&pos
        setWidth( 400d );
        setScene( new Scene( builder.getNode() ) );
    }

    ValuePropertiesController getController()
    {
        return controller;
    }
    
}
