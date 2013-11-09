package com.varankin.brains.jfx.analyser;

import com.varankin.util.LoggerX;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;

/**
 * Панель выбора параметров прорисовки отметок.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class ValuePropertiesPane extends GridPane
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValuePropertiesPane.class );
    private static final String RESOURCE_CSS  = "/fxml/analyzer/ValuePropertiesPane.css";
    private static final String CSS_CLASS = "value-properties-pane";

    ValuePropertiesPane( ValuePropertiesPaneController controller )
    {
        ImageView preview = new ImageView();
        preview.setId( "preview" );
        preview.setPreserveRatio( true );

        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setId( "colorPicker" );
        
        ComboBox<Marker> markerPicker = new ComboBox<>();
        markerPicker.setId( "markerPicker" );
        markerPicker.setVisibleRowCount( 7 );
        
        ComboBox<Integer> scalePicker = new ComboBox<>();
        scalePicker.setId( "scalePicker" );
        
        add( new Label( LOGGER.text( "properties.value.color" ) ), 0, 0 );
        add( colorPicker, 1, 0 );
        add( new Label( LOGGER.text( "properties.value.marker" ) ), 0, 1 );
        add( markerPicker, 1, 1 );
        add( new Label( LOGGER.text( "properties.value.pattern" ) ), 2, 0 );
        add( scalePicker, 2, 1 );
        add( preview, 3, 0, 1, 2 );
        
        getStyleClass().add( CSS_CLASS );
        getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );
        
        controller.preview = preview;
        controller.colorPicker = colorPicker;
        controller.markerPicker = markerPicker;
        controller.scalePicker = scalePicker;
        controller.initialize();
    }       

}
