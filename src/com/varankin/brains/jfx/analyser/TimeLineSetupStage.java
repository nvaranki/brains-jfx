package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import java.util.function.Consumer;
import javafx.scene.*;
import javafx.stage.*;

import static com.varankin.brains.jfx.analyser.TimeLineSetupController.*;

/**
 * Диалог выбора параметров рисования графика.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
final class TimeLineSetupStage extends Stage
{
    private boolean ready;
    
    TimeLineSetupStage( Consumer<TimeLineSetupController> reset, Consumer<TimeLineSetupController> action )
    {
        BuilderFX<Parent,TimeLineSetupController> builder = new BuilderFX<>();
        builder.init( TimeLineSetupController.class, RESOURCE_FXML, RESOURCE_BUNDLE );
        TimeLineSetupController controller = builder.getController();
        controller.setAction( action );
        
        initStyle( StageStyle.DECORATED );
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );

        setResizable( true );
        setMinHeight( 350d );
        setMinWidth( 470d );
        setHeight( 350d ); //TODO save/restore size&pos
        setWidth( 470d );
        setScene( new Scene( builder.getNode() ) );
        setOnShowing( (e) -> { if( !ready) { reset.accept( controller ); ready = true; } } );
    }

}
