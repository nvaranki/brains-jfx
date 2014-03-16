package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.stage.*;

import static com.varankin.brains.jfx.analyser.ValueRulerPropertiesController.*;

/**
 * Диалог для выбора и установки параметров оси значений.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class ValueRulerPropertiesStage extends Stage
{
    private final ValueRulerPropertiesController controller;

    ValueRulerPropertiesStage()
    {
        BuilderFX<Parent,ValueRulerPropertiesController> builder = new BuilderFX<>();
        builder.init( ValueRulerPropertiesController.class, RESOURCE_FXML, RESOURCE_BUNDLE );
        controller = builder.getController();

        initStyle( StageStyle.DECORATED );
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );
        
        setResizable( true );
        setMinHeight( 290d );
        setMinWidth( 420d );
        setHeight( 290d ); //TODO save/restore size&pos
        setWidth( 420d );
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

    ValueRulerPropertiesController getController()
    {
        return controller;
    }
    
}
