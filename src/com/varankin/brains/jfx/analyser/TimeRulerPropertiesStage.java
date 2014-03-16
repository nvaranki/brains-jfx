package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.stage.*;

import static com.varankin.brains.jfx.analyser.TimeRulerPropertiesController.*;

/**
 * Диалог для выбора и установки параметров оси времени.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
final class TimeRulerPropertiesStage extends Stage
{
    private final TimeRulerPropertiesController controller;

    TimeRulerPropertiesStage()
    {
        BuilderFX<Parent,TimeRulerPropertiesController> builder = new BuilderFX<>();
        builder.init( TimeRulerPropertiesController.class, RESOURCE_FXML, RESOURCE_BUNDLE );
        controller = builder.getController();
        
        initStyle( StageStyle.DECORATED );
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );
        
        setResizable( true );
        setMinHeight( 290d );
        setMinWidth( 430d );
        setHeight( 290d ); //TODO save/restore size&pos
        setWidth( 430d );
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

    TimeRulerPropertiesController getController()
    {
        return controller;
    }
    
}
