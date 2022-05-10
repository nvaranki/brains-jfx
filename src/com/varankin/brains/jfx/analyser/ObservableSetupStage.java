package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import java.util.function.Consumer;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.varankin.brains.jfx.analyser.ObservableSetupController.*;

/**
 * Диалог выбора параметров рисования наблюдаемого значения.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
final class ObservableSetupStage extends Stage
{
    private final ObservableSetupController controller;

    ObservableSetupStage( Consumer<Value> action )
    {
        BuilderFX<Parent,ObservableSetupController> builder = new BuilderFX<>();
        builder.init( ObservableSetupController.class, RESOURCE_FXML, RESOURCE_BUNDLE );
        controller = builder.getController();
        controller.setAction( action );
        
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );

        setResizable( true );
        setMinHeight( 220d );
        setMinWidth( 400d );
        setHeight( 220d ); //TODO save/restore size&pos
        setWidth( 400d );
        setScene( new Scene( builder.getNode() ) );
    }

    ObservableSetupController getController()
    {
        return controller;
    }

}
