package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.Property;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.stage.*;

import static com.varankin.brains.jfx.analyser.GraphPropertiesController.*;

/**
 * Диалог для выбора и установки параметров рисования графика.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
final class GraphPropertiesStage extends Stage
{
    GraphPropertiesStage( 
            Property<Long> rateValueProperty, Property<TimeUnit> rateUnitProperty, 
            Property<Boolean> borderDisplayProperty, Property<Color> borderColorProperty, 
            Property<Boolean> zeroDisplayProperty, Property<Color> zeroColorProperty, 
            Property<Boolean> flowModeProperty )
    {
        BuilderFX<Parent,GraphPropertiesController> builder = new BuilderFX<>();
        builder.init( GraphPropertiesController.class, RESOURCE_FXML, RESOURCE_BUNDLE );
        final GraphPropertiesController rootController = builder.getController();
        rootController.bindRateValueProperty( rateValueProperty );
        rootController.bindRateUnitProperty( rateUnitProperty );
        rootController.bindBorderDisplayProperty( borderDisplayProperty );
        rootController.bindBorderColorProperty( borderColorProperty );
        rootController.bindZeroDisplayProperty( zeroDisplayProperty );
        rootController.bindZeroColorProperty( zeroColorProperty );
        rootController.bindFlowModeProperty( flowModeProperty );
        
        setOnShowing( new EventHandler<WindowEvent>() 
        {
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
        setScene( new Scene( builder.getNode() ) );
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );
    }

}
