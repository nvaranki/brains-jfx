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
 * Диалог для выбора и установки параметров оси значений.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class ValueRulerPropertiesStage extends Stage
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValueRulerPropertiesStage.class );
    
    private final ValueRulerPropertiesController controller;

    ValueRulerPropertiesStage()
    {
        Parent root;
        if( JavaFX.getInstance().useFxmlLoader() )
            try
            {
                java.net.URL location = getClass().getResource( 
                        ValueRulerPropertiesController.RESOURCE_FXML );
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
            controller = new ValueRulerPropertiesController();
            root = controller.build();
        }

        initStyle( StageStyle.DECORATED );
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );
        
        setResizable( true );
        setMinHeight( 270d );
        setMinWidth( 400d );
        setHeight( 270d ); //TODO save/restore size&pos
        setWidth( 400d );
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

    ValueRulerPropertiesController getController()
    {
        return controller;
    }
    
}
