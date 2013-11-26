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

    ValueRulerPropertiesStage()
    {
        final ValueRulerPropertiesRootController rootController;
        Parent root;
        if( JavaFX.getInstance().useFxmlLoader() )
            try
            {
                java.net.URL location = getClass().getResource( 
                        ValueRulerPropertiesRootController.RESOURCE_FXML );
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
            rootController = new ValueRulerPropertiesRootController();
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
        setMinHeight( 190d );
        setMinWidth( 420d );
        setHeight( 190d ); //TODO save/restore size&pos
        setWidth( 420d );
        setScene( new Scene( root ) );
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );
    }
    
}
