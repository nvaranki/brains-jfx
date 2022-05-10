package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import java.util.function.Consumer;
import javafx.scene.*;
import javafx.stage.*;

import static com.varankin.brains.jfx.analyser.ValueRulerPropertiesController.*;

/**
 * Диалог для выбора и установки параметров оси значений.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
final class ValueRulerPropertiesStage extends Stage
{
    ValueRulerPropertiesStage( Consumer<ValueRulerPropertiesController> reset, Consumer<ValueRulerPropertiesController> action )
    {
        BuilderFX<Parent,ValueRulerPropertiesController> builder = new BuilderFX<>();
        builder.init( ValueRulerPropertiesController.class, RESOURCE_FXML, RESOURCE_BUNDLE );
        final ValueRulerPropertiesController controller = builder.getController();
        controller.setAction( action );

        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );
        
        setResizable( true );
        setMinHeight( 310d );
        setMinWidth( 460d );
        setHeight( 310d ); //TODO save/restore size&pos
        setWidth( 460d );
        setScene( new Scene( builder.getNode() ) );
        setOnShowing( (e) -> reset.accept( controller ) );
    }

}
