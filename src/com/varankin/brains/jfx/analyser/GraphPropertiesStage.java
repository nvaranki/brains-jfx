package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import java.util.function.Consumer;
import javafx.scene.*;
import javafx.stage.*;

import static com.varankin.brains.jfx.analyser.GraphPropertiesController.*;

/**
 * Диалог для выбора и установки параметров рисования графика.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
final class GraphPropertiesStage extends Stage
{
    GraphPropertiesStage( Consumer<GraphPropertiesController> reset, Consumer<GraphPropertiesController> action )
    {
        BuilderFX<Parent,GraphPropertiesController> builder = new BuilderFX<>();
        builder.init( GraphPropertiesController.class, RESOURCE_FXML, RESOURCE_BUNDLE );
        final GraphPropertiesController controller = builder.getController();
        controller.setAction( action );
        
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );
        
        setResizable( true );
        setMinHeight( 270d );
        setMinWidth( 400d );
        setHeight( 270d ); //TODO save/restore size&pos
        setWidth( 400d );
        setOnShowing( (e) ->  reset.accept( controller ) );
        setScene( new Scene( builder.getNode() ) );
    }

}
