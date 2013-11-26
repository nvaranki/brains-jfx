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

    TimeRulerPropertiesStage()
    {
        final TimeRulerPropertiesRootController rootController;
        Parent root;
        if( JavaFX.getInstance().useFxmlLoader() )
            try
            {
                java.net.URL location = getClass().getResource( 
                            TimeRulerPropertiesRootController.RESOURCE_FXML );
                ResourceBundle resources = LOGGER.getLogger().getResourceBundle();
                FXMLLoader fxmlLoader = new FXMLLoader( location, resources );
                root = (Parent)fxmlLoader.load();
                rootController = fxmlLoader.getController();
            }
            catch( IOException ex )
            {
                throw new RuntimeException( ex );
            }
        else
        {
            rootController = new TimeRulerPropertiesRootController();
            root = rootController.build();
        }
        
        setOnShowing( new EventHandler<WindowEvent>() 
        {
            @Override
            public void handle( WindowEvent event )
            {
                rootController.reset();
            }
        } );
        
        initStyle( StageStyle.DECORATED );
        setResizable( true );
        setMinHeight( 220d );
        setMinWidth( 400d );
        setHeight( 220d ); //TODO save/restore size&pos
        setWidth( 400d );
        setScene( new Scene( root ) );
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );
    }
    
}
