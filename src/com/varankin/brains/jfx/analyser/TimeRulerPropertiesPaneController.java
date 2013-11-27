package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.ListeningComboBoxSetter;
import com.varankin.brains.jfx.ValueSetter;
import com.varankin.util.LoggerX;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров оси времени.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public class TimeRulerPropertiesPaneController implements Builder<Node>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValuePropertiesPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/TimeRulerPropertiesPane.css";
    private static final String CSS_CLASS = "time-ruler-properties-pane";

    private final ObjectProperty<Long> durationProperty, excessProperty;
    private final ObjectProperty<TimeUnit> unitProperty; // <--> selectionModel.selectedItemProperty
    private final ObjectProperty<Font> textFontProperty;
    private final ChangeListener<TimeUnit> unitSelectionListener;
    
    private ChangeListener<TimeUnit> unitPropertyListener;

    @FXML private TextField duration, excess;
    @FXML private ComboBox<TimeUnit> unit;
    @FXML private ColorPicker textColor, tickColor;
    @FXML private Control textFont;
    
    public TimeRulerPropertiesPaneController()
    {
        durationProperty = new SimpleObjectProperty<>();
        excessProperty = new SimpleObjectProperty<>();
        unitProperty = new SimpleObjectProperty<>();
        unitSelectionListener = new ValueSetter<>( unitProperty );
        textFontProperty = new SimpleObjectProperty<>();
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
        excess.setId( "valueMax" );
        excess.setPrefColumnCount( 6 );
        excess.setFocusTraversable( true );
        
        unit = new ComboBox<>();
        unit.setId( "unit" );
        
        textColor = new ColorPicker();
        textColor.setId( "textColor" );
        textColor.setFocusTraversable( false ); //TODO true RT-21549
        
        textFont = new TextField("Helvetica"); //TODO
        textFont.setId( "textFont" );
        textFont.setFocusTraversable( true );
        
        tickColor = new ColorPicker();
        tickColor.setId( "tickColor" );
        tickColor.setFocusTraversable( false ); //TODO true RT-21549
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "properties.ruler.time.duration" ) ), 0, 0 );
        pane.add( duration, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.ruler.time.excess" ) ), 0, 1 );
        pane.add( excess, 1, 1 );
        pane.add( unit, 3, 0 );
        pane.add( new Label( LOGGER.text( "properties.ruler.text.color" ) ), 0, 2 );
        pane.add( textColor, 1, 2 );
        pane.add( new Label( LOGGER.text( "properties.ruler.text.font" ) ), 2, 2 );
        pane.add( textFont, 3, 2 );
        pane.add( new Label( LOGGER.text( "properties.ruler.tick.color" ) ), 0, 3 );
        pane.add( tickColor, 1, 3 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {   
        unit.getItems().addAll( Arrays.asList( TimeUnit.values() ) );
        unit.getSelectionModel().selectedItemProperty()
                .addListener( new WeakChangeListener<>( unitSelectionListener ) );
        unitPropertyListener = new ListeningComboBoxSetter<>( unit );
        unitProperty.addListener( new WeakChangeListener<>( unitPropertyListener ) );
        
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
    
    Property<Font> textFontProperty()
    {
        return textFontProperty;
    }

    Property<Color> tickColorProperty()
    {
        return tickColor.valueProperty();
    }

    /**
     * @deprecated RT-34098
     */
    void resetColorPicker()
    {
    }

    
}
