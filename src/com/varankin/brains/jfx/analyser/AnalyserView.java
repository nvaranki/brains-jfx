package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 *
 * @author Николай
 */
public class AnalyserView extends VBox
{

    public AnalyserView( JavaFX jfx )
    {
        setFillWidth( true );
        setSpacing( 10 );
        getChildren().add( new TimeLine( jfx ) );
        getChildren().add( new Separator( Orientation.HORIZONTAL ) );
        getChildren().add( new TimeLine( jfx ) );
//        getChildren().add( new Separator( Orientation.HORIZONTAL ) );
//        getChildren().add( new TimeLine( jfx ) );
//        getChildren().add( new Separator( Orientation.HORIZONTAL ) );
//        getChildren().add( new TimeLine( jfx ) );
//        getChildren().add( new TimeLine( jfx ) );
//        getChildren().add( new TimeLine( jfx ) );
//        getChildren().add( new TimeLine( jfx ) );
    }
    
}
