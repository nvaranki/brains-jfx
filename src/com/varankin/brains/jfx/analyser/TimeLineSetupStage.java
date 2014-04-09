package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.stage.*;

import static com.varankin.brains.jfx.analyser.TimeLineSetupController.*;

/**
 * Диалог выбора параметров рисования графика.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
final class TimeLineSetupStage extends Stage
{
    private final TimeLineSetupController controller;

    TimeLineSetupStage()
    {
        BuilderFX<Parent,TimeLineSetupController> builder = new BuilderFX<>();
        builder.init( TimeLineSetupController.class, RESOURCE_FXML, RESOURCE_BUNDLE );
        controller = builder.getController();
        
        initStyle( StageStyle.DECORATED );
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );

        setResizable( true );
        setMinHeight( 320d );
        setMinWidth( 420d );
        setHeight( 320d ); //TODO save/restore size&pos
        setWidth( 420d );
        setScene( new Scene( builder.getNode() ) );
        setOnShowing( new EventHandler<WindowEvent>() 
        {
            @Override
            public void handle( WindowEvent __ )
            {
                controller.setApproved( false );
            }
        } );
    }

    TimeLineSetupController getController()
    {
        return controller;
    }
    
}
