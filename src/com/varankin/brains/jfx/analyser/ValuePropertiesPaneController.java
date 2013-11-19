package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.ListeningComboBoxSetter;
import com.varankin.brains.jfx.ValueSetter;
import com.varankin.util.LoggerX;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.*;

/**
 * FXML-контроллер панели выбора параметров прорисовки отметок.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class ValuePropertiesPaneController implements Builder<Node>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValuePropertiesPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ValuePropertiesPane.css";
    private static final String CSS_CLASS = "value-properties-pane";

    private final Property<int[][]> patternProperty; // <--> selectionModel.selectedItemProperty
    private final Property<Integer> scaleProperty;
    private final ColorPickerChangeListener colorPickerListener;
    private final MarkerPickerChangeListener markerPickerChangeListener;
    private final ScalePickerChangeListener scalePickerChangeListener;
    private final ChangeListener<Integer> scalePropertySetter;
    private final ChangeListener<int[][]> markerPickerSetter;
    private final ChangeListener<Marker> patternPropertySetter;
    
    private ListeningComboBoxSetter<Integer> scalePickerSetter;

    @FXML private ImageView preview;
    @FXML private ColorPicker colorPicker;
    @FXML private ComboBox<Marker> markerPicker;
    @FXML private ComboBox<Integer> scalePicker;
    
    public ValuePropertiesPaneController()
    {
        colorPickerListener = new ColorPickerChangeListener();
        markerPickerChangeListener = new MarkerPickerChangeListener();
        patternProperty = new SimpleObjectProperty<>();
        scalePickerChangeListener = new ScalePickerChangeListener();
        scaleProperty = new SimpleObjectProperty<>();
        scalePropertySetter = new ValueSetter<>( scaleProperty );
        markerPickerSetter = new PatternResolver();
        patternPropertySetter = new MarkerResolver();
    }
    
    /**
     * Создает панель выбора параметров прорисовки отметок.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public GridPane build()
    {
        preview = new ImageView();
        preview.setId( "preview" );
        preview.setPreserveRatio( true );

        colorPicker = new ColorPicker();
        colorPicker.setId( "colorPicker" );
        
        markerPicker = new ComboBox<>();
        markerPicker.setId( "markerPicker" );
        markerPicker.setVisibleRowCount( 7 );
        
        scalePicker = new ComboBox<>();
        scalePicker.setId( "scalePicker" );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "properties.value.color" ) ), 0, 0 );
        pane.add( colorPicker, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.value.marker" ) ), 0, 1 );
        pane.add( markerPicker, 1, 1 );
        pane.add( new Label( LOGGER.text( "properties.value.pattern" ) ), 2, 0 );
        pane.add( scalePicker, 2, 1 );
        pane.add( preview, 3, 0, 1, 2 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {   
        colorPicker.valueProperty().addListener( new WeakChangeListener<>( colorPickerListener ) );

        markerPicker.getSelectionModel().selectedItemProperty()
                .addListener( new WeakChangeListener<>( markerPickerChangeListener ) );
        markerPicker.getSelectionModel().selectedItemProperty()
                .addListener( new WeakChangeListener<>( patternPropertySetter ) );
        CellFactory cellFactory = new CellFactory();
        markerPicker.setCellFactory( cellFactory );
        markerPicker.setButtonCell( cellFactory.call( null ) );
        markerPicker.getItems().addAll( Arrays.asList( Marker.values() ) );
        
        patternProperty.addListener( new WeakChangeListener<>( markerPickerSetter ) );

        scalePicker.getSelectionModel().selectedItemProperty()
                .addListener( new WeakChangeListener<>( scalePickerChangeListener ) );
        scalePicker.getSelectionModel().selectedItemProperty()
                .addListener( new WeakChangeListener<>( scalePropertySetter ) );
        scalePicker.setConverter( new ScalePickerConverter() );
        for( int i : new int[]{1,2,3,4,5,10} )
            scalePicker.getItems().add( i );

        scalePickerSetter = new ListeningComboBoxSetter<>( scalePicker );
        scaleProperty.addListener( new WeakChangeListener<>( scalePickerSetter ) );
    }
    
    Property<Color> colorProperty()
    {
        return colorPicker.valueProperty();
    }

    Property<int[][]> patternProperty()
    {
        return patternProperty;
    }
    
    Property<Integer> scaleProperty()
    {
        return scaleProperty;
    }

    /**
     * @deprecated RT-34098
     */
    void resetColorPicker()
    {
        colorPicker.fireEvent( new ActionEvent() );
    }

    private static Image resample( Color color, int[][] pattern, int scale )
    {
        WritableImage sample = new WritableImage( 50, 50 );
        PixelWriter writer = sample.getPixelWriter();
        
        // граница рисунка
        for( int i = 1; i < 49; i++ )
        {
            writer.setColor( i, 0, Color.LIGHTGRAY );
            writer.setColor( i, 49, Color.LIGHTGRAY );
            writer.setColor( 0, i, Color.LIGHTGRAY );
            writer.setColor( 49, i, Color.LIGHTGRAY );
        }
        
        // рисунок в масштабе
        for( int[] dot : pattern )
        {
            int left = 25 - scale/2 + dot[0] * scale;
            int top  = 25 - scale/2 + dot[1] * scale;
            for( int r = 0; r < scale; r++ )
                for( int c = 0; c < scale; c++ )
                {
                    int x = left + c;
                    int y = top  + r;
                    if( 0 < x && x < 49 && 0 < y && y < 49 )
                        writer.setColor( x, y, color );
                }
        }
        
        return sample;
    }
    
    //<editor-fold defaultstate="collapsed" desc="классы">
    
    private static class CellFactory 
        implements Callback<ListView<Marker>,ListCell<Marker>>
    {
        @Override
        public ListCell<Marker> call( final ListView<Marker> param )
        {
            return new ListCell<Marker>()
            {
                @Override
                protected void updateItem( Marker item, boolean empty )
                {
                    // calling super here is very important - don't skip this!
                    super.updateItem( item, empty );
                    
                    setText( empty ? null : item.toString() );
                    
                    if( empty )
                    {
                        setGraphic( null );
                    }
                    else if( param != null )
                    {
                        int width = 16, height = 16, s = 15, z = s/2;
                        Color outlineColor = Color.LIGHTGRAY;
                        WritableImage sample = new WritableImage( width, height );
                        PixelWriter pixelWriter = sample.getPixelWriter();
                        for( int i = 1; i < s; i ++ )
                        {
                            pixelWriter.setColor( i, 0, outlineColor );
                            pixelWriter.setColor( i, s, outlineColor );
                            pixelWriter.setColor( 0, i, outlineColor );
                            pixelWriter.setColor( s, i, outlineColor );
                        }
                        DotPainter.paint( z, z, Color.BLACK, item.pattern, pixelWriter, width, height );
                        setGraphic( new ImageView( sample ) );
                    }
                    else
                    {
                        int width  = 16, height = 16;
                        WritableImage sample = new WritableImage( width, height );
                        PixelWriter writer = sample.getPixelWriter();
                        for( int x = 0; x < width; x++ )
                            for( int y = 0; y < height; y++ )
                                writer.setColor( x, y, Color.WHITE );
                        DotPainter.paint( 7, 7, Color.BLACK, item.pattern, writer, width, height );
                        setGraphic( new ImageView( sample ) );
                    }
                }
            };
        }
    }

    private class ColorPickerChangeListener implements ChangeListener<Color>
    {
        @Override
        public void changed( ObservableValue<? extends Color> _,
                            Color oldValue, Color newValue )
        {
            Color color = newValue;
            Marker marker = markerPicker.getSelectionModel().getSelectedItem();
            Integer scale = scalePicker.getSelectionModel().getSelectedItem();
            if( color != null && marker != null && scale != null )
            {
                preview.setImage( resample( color, marker.pattern, scale ) );
            }
        }
    }
    
    private class MarkerPickerChangeListener implements ChangeListener<Marker>
    {
        @Override
        public void changed( ObservableValue<? extends Marker> _,
                            Marker oldValue, Marker newValue )
        {
            Color color = colorPicker.getValue();
            Marker marker = newValue;
            Integer scale = scalePicker.getSelectionModel().getSelectedItem();
            if( color != null && marker != null && scale != null )
            {
                preview.setImage( resample( color, marker.pattern, scale ) );
            }
        }
    }
    
    private class ScalePickerChangeListener implements ChangeListener<Integer>
    {
        @Override
        public void changed( ObservableValue<? extends Integer> _,
                            Integer oldValue, Integer newValue )
        {
            Color color = colorPicker.getValue();
            Marker marker = markerPicker.getSelectionModel().getSelectedItem();
            Integer scale = newValue;
            if( color != null && marker != null && scale != null )
                preview.setImage( resample( color, marker.pattern, scale ) );
        }
    }
    
    private class ScalePickerConverter extends StringConverter<Integer>
    {
        @Override
        public String toString( Integer integer )
        {
            return Integer.toString( integer ) + 'x';
        }
        
        @Override
        public Integer fromString( String string )
        {
            return Integer.valueOf( string.replace( 'x', ' ' ) );
        }
    }
    
    private class MarkerResolver implements ChangeListener<Marker>
    {
        @Override
        public void changed( ObservableValue<? extends Marker> observable, 
                            Marker oldValue, Marker newValue )
        {
            patternProperty.setValue( newValue != null ? newValue.pattern : null );
        }
    }
    
    private class PatternResolver implements ChangeListener<int[][]>
    {
        @Override
        public void changed( ObservableValue<? extends int[][]> _, 
                            int[][] oldValue, int[][] newValue )
        {
            List<Marker> items = markerPicker.getItems();

            for( int i = 0, max = items.size(); i < max; i++ )
                if( Arrays.deepEquals( newValue, items.get( i ).pattern ) )
                {
                    markerPicker.getSelectionModel().select( i );
                    return;
                }
            
            if( newValue != null )
                LOGGER.getLogger().fine( "Custom pattern cannot be set." );
            markerPicker.getSelectionModel().clearSelection();
        }
    }
            
    //</editor-fold>
    
}
