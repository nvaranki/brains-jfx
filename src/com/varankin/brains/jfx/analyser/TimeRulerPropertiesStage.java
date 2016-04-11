package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import java.util.function.Consumer;
import javafx.scene.*;
import javafx.stage.*;

import static com.varankin.brains.jfx.analyser.TimeRulerPropertiesController.*;

/**
 * Диалог для выбора и установки параметров оси времени.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
final class TimeRulerPropertiesStage extends Stage
{
    TimeRulerPropertiesStage( Consumer<TimeRulerPropertiesController> reset, Consumer<TimeRulerPropertiesController> action )
    {
        BuilderFX<Parent,TimeRulerPropertiesController> builder = new BuilderFX<>();
        builder.init( TimeRulerPropertiesController.class, RESOURCE_FXML, RESOURCE_BUNDLE );
        final TimeRulerPropertiesController controller = builder.getController();
        controller.setAction( action );
        
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );
        
        setResizable( true );
        setMinHeight( 320d );
        setMinWidth( 470d );
        setHeight( 320d ); //TODO save/restore size&pos
        setWidth( 470d );
        setScene( new Scene( builder.getNode() ) );
        setOnShowing( (e) -> reset.accept( controller ) );
    }

}
