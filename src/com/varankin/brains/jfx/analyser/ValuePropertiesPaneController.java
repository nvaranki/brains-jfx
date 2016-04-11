package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.ChangedTrigger;
import com.varankin.brains.jfx.SingleSelectionProperty;
import com.varankin.util.LoggerX;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
 * @author &copy; 2016 Николай Варанкин
 */
public final class ValuePropertiesPaneController implements Builder<Node>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValuePropertiesPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ValuePropertiesPane.css";
    private static final String CSS_CLASS = "value-properties-pane";

    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();
    
    private final SingleSelectionProperty<String> titleProperty;
    private final SimpleObjectProperty<int[][]> patternProperty; // <--> selectionModel.selectedItemProperty
    private final SingleSelectionProperty<Integer> scaleProperty;
    private final ReadOnlyBooleanWrapper validProperty;
    private final ColorPickerChangeListener colorPickerListener;
    private final ChangeListener<Marker> markerPickerChangeListener;
    private final ChangeListener<int[][]> markerPickerSetter;
    private final ChangeListener<Marker> patternPropertySetter;
    private final ChangeListener<Integer> scalePickerChangeListener;
    private final BooleanProperty changedProperty;
    private final ChangedTrigger changedFunction;

    private BooleanBinding changedBinding;
    
    @FXML private ComboBox<String> title;
    @FXML private ImageView preview;
    @FXML private ColorPicker colorPicker;
    @FXML private ComboBox<Marker> markerPicker;
    @FXML private ComboBox<Integer> scalePicker;
    
    public ValuePropertiesPaneController()
    {
        colorPickerListener = new ColorPickerChangeListener();
        markerPickerChangeListener = new MarkerPickerChangeListener();
        scalePickerChangeListener = new ScalePickerChangeListener();
        titleProperty = new SingleSelectionProperty<>();
        patternProperty = new SimpleObjectProperty<>();
        scaleProperty = new SingleSelectionProperty<>();
        validProperty = new ReadOnlyBooleanWrapper();
        markerPickerSetter = new PatternResolver();
        patternPropertySetter = new MarkerResolver();
        changedProperty = new SimpleBooleanProperty();
        changedFunction = new ChangedTrigger();
    }
    
    /**
     * Создает панель выбора параметров прорисовки отметок.
     * Применяется в конфигурации без FXML.
     * 
     * @return созданная панель.
     */
    @Override
    public GridPane build()
    {
        title = new ComboBox<>();
        title.setEditable( true );
        title.setId( "title" );
        title.setFocusTraversable( true );
        title.setVisibleRowCount( 5 );
        
        preview = new ImageView();
        preview.setId( "preview" );
        preview.setPreserveRatio( true );

        colorPicker = new ColorPicker();
        colorPicker.setId( "colorPicker" );
        colorPicker.setFocusTraversable( false ); //TODO true RT-21549
        
        markerPicker = new ComboBox<>();
        markerPicker.setId( "markerPicker" );
        markerPicker.setVisibleRowCount( 7 );
        
        scalePicker = new ComboBox<>();
        scalePicker.setId( "scalePicker" );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "observable.setup.value.name" ) ), 0, 0 );
        pane.add( title, 1, 0, 3, 1 );
        pane.add( new Label( LOGGER.text( "properties.value.color" ) ), 0, 1 );
        pane.add( colorPicker, 1, 1 );
        pane.add( new Label( LOGGER.text( "properties.value.marker" ) ), 0, 2 );
        pane.add( markerPicker, 1, 2 );
        pane.add( new Label( LOGGER.text( "properties.value.pattern" ) ), 2, 1 );
        pane.add( scalePicker, 2, 2 );
        pane.add( preview, 3, 1, 1, 2 );
        
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
        
        titleProperty.setModel( title.getSelectionModel() );

        patternProperty.addListener( new WeakChangeListener<>( markerPickerSetter ) );

        scalePicker.setConverter( new ScalePickerConverter() );
        scalePicker.getItems().addAll( new Integer[]{1,2,3,4,5,10} );
        
        scaleProperty.setModel( scalePicker.getSelectionModel() );
        scaleProperty.addListener( new WeakChangeListener<>( scalePickerChangeListener ) );

        BooleanBinding validBinding = 
            Bindings.and( 
                Bindings.isNotNull( title.getEditor().textProperty() ),
                Bindings.and( 
                    Bindings.isNotNull( colorPicker.valueProperty() ), 
                    Bindings.isNotNull( patternProperty ) ) );
        validProperty.bind( validBinding );

        changedProperty.bind( changedBinding = Bindings.createBooleanBinding( changedFunction, 
                titleProperty,
                title.getEditor().textProperty(),
                colorPicker.valueProperty(),
                patternProperty /*,
                scaleProperty is not relevant */ ) );
    }
    
    ReadOnlyBooleanProperty validProperty()
    {
        return validProperty.getReadOnlyProperty();
    }

    BooleanProperty changedProperty()
    {
        return changedProperty;
    }

    void copyOptions( Value value )
    {
        title.getItems().clear();
        if( title.getItems().addAll( value.метки ) )
            title.getSelectionModel().selectFirst();
        colorPicker.setValue( value.colorProperty().getValue() );
        patternProperty.setValue( value.patternProperty().getValue() );
        scalePicker.getSelectionModel().select( 3 ); //TODO
        resetColorPicker();
        changedFunction.setValue( false );
        changedBinding.invalidate();
    }

    void applyOptions( Value value )
    {
        value.patternProperty().setValue( patternProperty.getValue() );
        value.colorProperty().setValue( colorPicker.valueProperty().getValue() );
        value.titleProperty().setValue( title.getEditor().textProperty().getValue() );
        changedFunction.setValue( false );
        changedBinding.invalidate();
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
        public void changed( ObservableValue<? extends Color> __,
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
        public void changed( ObservableValue<? extends Marker> __,
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
        public void changed( ObservableValue<? extends Integer> __,
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
        public void changed( ObservableValue<? extends int[][]> __, 
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
