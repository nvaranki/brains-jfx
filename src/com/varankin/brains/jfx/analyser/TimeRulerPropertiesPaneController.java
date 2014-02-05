package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.*;
import com.varankin.brains.jfx.shared.FontPickerPaneController;
import com.varankin.util.LoggerX;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров оси времени.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public class TimeRulerPropertiesPaneController implements Builder<Node>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TimeRulerPropertiesPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/TimeRulerPropertiesPane.css";
    private static final String CSS_CLASS = "time-ruler-properties-pane";

    private final ObjectProperty<Long> durationProperty, excessProperty;
    private final SingleSelectionProperty<TimeUnit> unitProperty;
    private final ReadOnlyBooleanWrapper validProperty;

    @FXML private TextField duration, excess;
    @FXML private ComboBox<TimeUnit> unit;
    @FXML private ColorPicker textColor, tickColor;
    @FXML private Pane fontPicker;
    @FXML private FontPickerPaneController fontPickerController;
    
    public TimeRulerPropertiesPaneController()
    {
        durationProperty = new SimpleObjectProperty<>();
        excessProperty = new SimpleObjectProperty<>();
        unitProperty = new SingleSelectionProperty<>();
        validProperty = new ReadOnlyBooleanWrapper();
    }
    
    /**
     * Создает панель выбора и установки параметров оси значений.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public GridPane build()
    {
        duration = new TextField();
        duration.setId( "duration" );
        duration.setPrefColumnCount( 6 );
        duration.setFocusTraversable( true );
        
        excess = new TextField();
        excess.setId( "excess" );
        excess.setPrefColumnCount( 6 );
        excess.setFocusTraversable( true );
        
        unit = new ComboBox<>();
        unit.setId( "unit" );
        
        textColor = new ColorPicker();
        textColor.setId( "textColor" );
        textColor.setFocusTraversable( false ); //TODO true RT-21549
        
        tickColor = new ColorPicker();
        tickColor.setId( "tickColor" );
        tickColor.setFocusTraversable( false ); //TODO true RT-21549

        fontPickerController = new FontPickerPaneController();
        fontPicker = fontPickerController.build();
        fontPicker.setId( "fontPicker" );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "properties.ruler.time.duration" ) ), 0, 0 );
        pane.add( duration, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.ruler.time.excess" ) ), 0, 1 );
        pane.add( excess, 1, 1 );
        pane.add( unit, 2, 0, 2, 2 );
        pane.add( new Label( LOGGER.text( "properties.ruler.text.color" ) ), 0, 2 );
        pane.add( textColor, 1, 2 );
        pane.add( new Label( LOGGER.text( "properties.ruler.tick.color" ) ), 2, 2 );
        pane.add( tickColor, 3, 2 );
        pane.add( fontPicker, 0, 3, 4, 1 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
        Bindings.bindBidirectional( duration.textProperty(), durationProperty, 
                new DurationConverter( duration ) );
        Bindings.bindBidirectional( excess.textProperty(), excessProperty, 
                new ExcessConverter( excess ) );
        unit.getItems().addAll( Arrays.asList( TimeUnit.values() ) );
        unitProperty.setModel( unit.getSelectionModel() );
        BooleanBinding validBinding = Bindings.and
        ( 
            Bindings.and
            ( 
                Bindings.and
                ( 
                    ObjectBindings.isNotNull( durationProperty() ),
                    ObjectBindings.isNotNull( excessProperty() ) 
                ),
                Bindings.and
                ( 
                    ObjectBindings.isNotNull( unitProperty() ),
                    ObjectBindings.isNotNull( textColorProperty() )
                )
            ),
            Bindings.and
            ( 
                ObjectBindings.isNotNull( tickColorProperty() ),
                ObjectBindings.isNotNull( textFontProperty() )
            )
        );
        validProperty.bind( validBinding );
    }

    Property<Long> durationProperty()
    {
        return durationProperty;
    }

    Property<Long> excessProperty()
    {
        return excessProperty;
    }
    
    Property<TimeUnit> unitProperty()
    {
        return unitProperty;
    }

    Property<Color> textColorProperty()
    {
        return textColor.valueProperty();
    }
    
    Property<Color> tickColorProperty()
    {
        return tickColor.valueProperty();
    }

    Property<Font> textFontProperty()
    {
        return fontPickerController.fontProperty();
    }
    
    ReadOnlyBooleanProperty validProperty()
    {
        return validProperty.getReadOnlyProperty();
    }

    /**
     * @deprecated RT-34098
     */
    void resetColorPicker()
    {
        textColor.fireEvent( new ActionEvent() );
        tickColor.fireEvent( new ActionEvent() );
    }

    void reset()
    {
        unitProperty.setValue( TimeUnit.MILLISECONDS );
        durationProperty.setValue( 1000L );
        excessProperty.setValue( 20L );
        textColor.setValue( Color.BLACK );
        tickColor.setValue( Color.BLACK );
        fontPickerController.fontProperty().setValue( new Text().getFont() );
    }

    private class DurationConverter extends PositiveLongConverter
    {
        DurationConverter( Node node )
        {
            super( node );
        }

        @Override
        public Long fromString( String string )
        {
            Long value = super.fromString( string );
            Long excessPropertyValue = excessProperty.getValue();
            if( value != null && excessPropertyValue != null && value <= excessPropertyValue )
            {
                LOGGER.log( "001003005W", value, excessPropertyValue );
                highlight( value = null );
            }
            return value;
        }
    }

    private class ExcessConverter extends PositiveLongConverter
    {
        ExcessConverter( Node node )
        {
            super( node );
        }

        @Override
        public Long fromString( String string )
        {
            Long value = super.fromString( string );
            Long durationPropertyValue = durationProperty.getValue();
            if( value != null && durationPropertyValue != null && value >= durationPropertyValue )
            {
                LOGGER.log( "001003003W", value, durationPropertyValue );
                highlight( value = null );
            }
            return value;
        }
    }
    
}
