package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Диалог для выбора и установки параметров прорисовки отметок.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
class ValuePropertiesStage extends Stage
{
    ValuePropertiesStage( DotPainter painter )
    {
        ValuePropertiesRoot root = new ValuePropertiesRoot();
        root.setPainter( painter );
        
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
