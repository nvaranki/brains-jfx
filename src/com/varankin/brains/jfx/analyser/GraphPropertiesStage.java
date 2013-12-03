package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.Property;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.*;

/**
 * Диалог для выбора и установки параметров рисования графика.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class GraphPropertiesStage extends Stage
{
    private static final LoggerX LOGGER = LoggerX.getLogger( GraphPropertiesStage.class );
    private static final String RESOURCE_FXML = "/fxml/analyser/GraphPropertiesRoot.fxml";

    GraphPropertiesStage( 
            Property<Long> rateValueProperty, Property<TimeUnit> rateUnitProperty, 
            Property<Boolean> borderDisplayProperty, Property<Color> borderColorProperty, 
            Property<Boolean> zeroDisplayProperty, Property<Color> zeroColorProperty, 
            Property<Boolean> flowModeProperty )
    {
        final GraphPropertiesController rootController;
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
            rootController = new GraphPropertiesController();
            root = rootController.build();
        }
        rootController.bindRateValueProperty( rateValueProperty );
        rootController.bindRateUnitProperty( rateUnitProperty );
        rootController.bindBorderDisplayProperty( borderDisplayProperty );
        rootController.bindBorderColorProperty( borderColorProperty );
        rootController.bindZeroDisplayProperty( zeroDisplayProperty );
        rootController.bindZeroColorProperty( zeroColorProperty );
        rootController.bindFlowModeProperty( flowModeProperty );
        
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
        setMinHeight( 200d );
        setMinWidth( 400d );
        setHeight( 200d ); //TODO save/restore size&pos
        setWidth( 400d );
        setScene( new Scene( root ) );
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );
    }

}
