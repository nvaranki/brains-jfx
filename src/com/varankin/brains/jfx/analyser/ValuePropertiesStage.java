package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Диалог для выбора и установки параметров рисования отметок.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class ValuePropertiesStage extends Stage
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValuePropertiesStage.class );
    private static final String RESOURCE_FXML = "/fxml/analyser/ValuePropertiesRoot.fxml";

    ValuePropertiesStage( DotPainter painter )
    {
        final ValuePropertiesRootController rootController;
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
        {
            rootController = new ValuePropertiesRootController();
            root = rootController.build();
        }
        rootController.bindColorProperty( painter.colorProperty() );
        rootController.bindPatternProperty( painter.patternProperty() );
        rootController.bindScaleProperty( new SimpleObjectProperty( 3 ) );
        
        setOnShowing( new EventHandler<WindowEvent>() {

            @Override
            public void handle( WindowEvent event )
            {
                rootController.reset();
            }
        } );
        
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

}
