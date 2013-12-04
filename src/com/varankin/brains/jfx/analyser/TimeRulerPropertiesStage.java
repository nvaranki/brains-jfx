package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;

/**
 * Диалог для выбора и установки параметров оси времени.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class TimeRulerPropertiesStage extends Stage
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TimeRulerPropertiesStage.class );
    
    private final TimeRulerPropertiesController controller;

    TimeRulerPropertiesStage()
    {
        Parent root;
        if( JavaFX.getInstance().useFxmlLoader() )
            try
            {
                java.net.URL location = getClass().getResource( 
                            TimeRulerPropertiesController.RESOURCE_FXML );
                ResourceBundle resources = LOGGER.getLogger().getResourceBundle();
                FXMLLoader fxmlLoader = new FXMLLoader( location, resources );
                root = (Parent)fxmlLoader.load();
                controller = fxmlLoader.getController();
            }
            catch( IOException ex )
            {
                throw new RuntimeException( ex );
            }
        else
        {
            controller = new TimeRulerPropertiesController();
            root = controller.build();
        }
        
        initStyle( StageStyle.DECORATED );
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );
        
        setResizable( true );
        setMinHeight( 270d );
        setMinWidth( 380d );
        setHeight( 270d ); //TODO save/restore size&pos
        setWidth( 380d );
        setScene( new Scene( root ) );
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
