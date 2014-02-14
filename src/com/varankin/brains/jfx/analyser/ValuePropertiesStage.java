package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.stage.*;

import static com.varankin.brains.jfx.analyser.ValuePropertiesController.*;

/**
 * Диалог для выбора и установки параметров рисования отметок.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
final class ValuePropertiesStage extends Stage
{
    private final ValuePropertiesController controller;

    ValuePropertiesStage()
    {
        BuilderFX<Parent,ValuePropertiesController> builder = new BuilderFX<>();
        builder.init( ValuePropertiesController.class, RESOURCE_FXML, RESOURCE_BUNDLE );
        controller = builder.getController();
        
        initStyle( StageStyle.DECORATED );
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );

        setResizable( true );
        setMinHeight( 150d );
        setMinWidth( 350d );
        setHeight( 150d ); //TODO save/restore size&pos
        setWidth( 350d );
        setScene( new Scene( builder.getNode() ) );
        setOnShowing( new EventHandler<WindowEvent>() 
        {
            @Override
            public void handle( WindowEvent event )
            {
                controller.reset();
            }
        } );
    }

    ValuePropertiesController getController()
    {
        return controller;
    }
    
}
