package com.varankin.brains.jfx.analyser;

import com.varankin.util.LoggerX;
import java.util.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.*;

/**
 * Панель выбора параметров прорисовки отметок.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class ValuePropertiesPane extends GridPane
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValuePropertiesPane.class );
    private static final String RESOURCE_CSS  = "/fxml/analyzer/value-properties-pane.css";
    private static final String RESOURCE_FXML = "/fxml/analyzer/ValuePropertiesPane.fxml";

    @FXML private ImageView preview;
    @FXML private ColorPicker colorPicker;
    @FXML private ComboBox<Marker> markerPicker;
    @FXML private ComboBox<Integer> scalePicker;
    
    private final BooleanProperty changedProperty;
    
    public ValuePropertiesPane()
    {
        changedProperty = new SimpleBooleanProperty( false );
       
        //<editor-fold defaultstate="collapsed" desc="API Loader">
/*       
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
        
        double gap = com.varankin.brains.jfx.JavaFX.getInstance().getDefaultGap();
        setHgap( gap );
        setVgap( gap );
        setPadding( new javafx.geometry.Insets( gap ) );
        add( new Label( LOGGER.text( "properties.value.color" ) ), 0, 0 );
        add( colorPicker, 1, 0 );
        add( new Label( LOGGER.text( "properties.value.marker" ) ), 0, 1 );
        add( markerPicker, 1, 1 );
        add( new Label( LOGGER.text( "properties.value.pattern" ) ), 2, 0 );
        add( scalePicker, 2, 1 );
        add( preview, 3, 0, 1, 2 );
        
        getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );
        initialize();
*/
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="FXML Loader">
/**/
        java.net.URL location = getClass().getResource( RESOURCE_FXML );
        ResourceBundle resources = LOGGER.getLogger().getResourceBundle();
        javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader( location, resources );
        fxmlLoader.setRoot( ValuePropertiesPane.this );
        fxmlLoader.setController( ValuePropertiesPane.this );

        try 
        {
            fxmlLoader.load();
        } 
        catch( java.io.IOException exception ) 
        {
            throw new RuntimeException( exception );
        }
        
        //</editor-fold>
    }
    
    @FXML
    protected void initialize()
    {
        getStyleClass().add( "value-properties-pane" );

        colorPicker.valueProperty().addListener( new ColorPickerChangeListener() );

        markerPicker.getSelectionModel().selectedItemProperty()
                .addListener( new MarkerPickerChangeListener() );
        CellFactory cellFactory = new CellFactory();
        markerPicker.setCellFactory( cellFactory );
        markerPicker.setButtonCell( cellFactory.call( null ) );
        markerPicker.getItems().addAll( Arrays.asList( Marker.values() ) );

        scalePicker.getSelectionModel().selectedItemProperty()
                .addListener( new ScalePickerChangeListener() );
        scalePicker.setConverter( new ScalePickerConverter() );
        for( int i : new int[]{1,2,3,4,5,10} )
            scalePicker.getItems().add( i );
    }
            
    BooleanProperty changedProperty()
    {
        return changedProperty;
    }
    
    Color getColor()
    {
        return colorPicker.getValue();
    }
    
    void setColor( Color color )
    {
//        colorPicker.setValue( color );
        // the call commented out above doesn't work for 
        // (1) color reset
        // (2) colorPicker.valueProperty() update
        colorPicker.valueProperty().setValue( color );
        colorPicker.fireEvent( new ActionEvent() ); //RT-34098
    }
    
    Marker getMarker()
    {
        return markerPicker.getValue();
    }
    
    void setMarker( Marker marker )
    {
        markerPicker.setValue( marker );
    }
    
    void setScale( Integer scale )
    {
        scalePicker.setValue( scale );
    }
    
    private Image resample( Color color, int[][] pattern, int scale )
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
                        Image sample = DotPainter.sample( Color.BLACK, item.pattern );
                        setGraphic( new ImageView( sample ) );
                    }
                    else
                    {
                        WritableImage sample = new WritableImage( 16, 16 );
                        int width  = sample.widthProperty().intValue();
                        int height = sample.heightProperty().intValue();
                        PixelWriter writer = sample.getPixelWriter();
                        for( int x = 0; x < width; x++ )
                            for( int y = 0; y < height; y++ )
                                writer.setColor( x, y, Color.WHITE );
                        DotPainter.paint( 7, 7, sample, Color.BLACK, item.pattern );
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
                changedProperty.setValue( Boolean.TRUE );
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
                changedProperty.setValue( Boolean.TRUE );
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
    
    //</editor-fold>
}
