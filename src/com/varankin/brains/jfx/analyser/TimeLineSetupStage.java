package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Николай
 */
final class TimeLineSetupStage extends Stage
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TimeLineSetupStage.class );
    
    private final TimeLineSetupController controller;

    TimeLineSetupStage()
    {
        Parent root;
        if( JavaFX.getInstance().useFxmlLoader() )
            try
            {
                java.net.URL location = getClass().getResource( ValuePropertiesController.RESOURCE_FXML );
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
            controller = new TimeLineSetupController();
            root = controller.build();
        }
        
        initStyle( StageStyle.DECORATED );
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );

        setResizable( true );
        setMinHeight( 320d );
        setMinWidth( 400d );
        setHeight( 320d ); //TODO save/restore size&pos
        setWidth( 400d );
        setScene( new Scene( root ) );
    }

    TimeLineSetupController getController()
    {
        return controller;
    }
    
}
