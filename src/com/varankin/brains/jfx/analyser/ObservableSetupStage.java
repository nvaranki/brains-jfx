package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import static com.varankin.brains.jfx.analyser.ObservableSetupController.*;
import com.varankin.property.PropertyMonitor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Диалог выбора параметров рисования наблюдаемого значения.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
final class ObservableSetupStage extends Stage
{
    private final ObservableSetupController controller;

    ObservableSetupStage()
    {
        BuilderFX<Parent,ObservableSetupController> builder = new BuilderFX<>();
        builder.init( ObservableSetupController.class, RESOURCE_FXML, RESOURCE_BUNDLE );
        controller = builder.getController();
        
        initStyle( StageStyle.DECORATED );
        getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );

        setResizable( true );
        setMinHeight( 220d );
        setMinWidth( 400d );
        setHeight( 220d ); //TODO save/restore size&pos
        setWidth( 400d );
        setScene( new Scene( builder.getNode() ) );
        setOnShowing( (WindowEvent e) -> { controller.setApproved( false ); } );
    }

    /**
     * Устанавливает монитор наблюдаемого значения.
     * 
     * @param value монитор.
     */
    void setMonitor( PropertyMonitor value )
    {
        controller.setMonitor( value );
    }

    /**
     * Создает новое значение, отображаемое на графике.
     */ 
    Value createValueInstance()
    {
        return controller.createValueInstance(  );
    }

}