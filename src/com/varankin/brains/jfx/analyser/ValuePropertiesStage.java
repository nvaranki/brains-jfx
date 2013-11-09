package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Диалог для выбора и установки параметров прорисовки отметок.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
class ValuePropertiesStage extends Stage
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValuePropertiesStage.class );
    private static final String RESOURCE_FXML = "/fxml/analyzer/ValuePropertiesRoot.fxml";

    private ValuePropertiesRootController rootController;
    
    ValuePropertiesStage()
    {
        Parent root;
        if( JavaFX.getInstance().useFxmlLoader() )
            try
            {
                java.net.URL location = getClass().getResource( RESOURCE_FXML );
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
            root = new ValuePropertiesRoot( rootController = new ValuePropertiesRootController() );
        
        initStyle( StageStyle.DECORATED );
        initModality( Modality.NONE );
        setResizable( true );
        setMinHeight( 150d );
        setMinWidth( 350d );
        setHeight( 150d ); //TODO save/restore size&pos
        setWidth( 350d );
        setScene( new Scene( root ) );
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );
    }

    void setPainter( DotPainter painter )
    {
        rootController.setPainter( painter );
    }
    
}
