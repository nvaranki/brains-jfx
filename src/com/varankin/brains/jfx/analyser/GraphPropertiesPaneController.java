package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.ListeningComboBoxSetter;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.ValueSetter;
import com.varankin.util.LoggerX;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Builder;

/**
 * Панель выбора и установки параметров рисования графика.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class GraphPropertiesPaneController implements Builder<Node>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( GraphPropertiesPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/GraphPropertiesPane.css";
    private static final String CSS_CLASS = "graph-properties-pane";

    private final ObjectProperty<TimeUnit> rateUnitProperty; // <--> selectionModel.selectedItemProperty
    private final ObjectProperty<Long> rateValueProperty;
    private final ChangeListener<TimeUnit> rateUnitSelectionListener;
    private final ChangeListener<String> rateValueInputListener;
    private final ChangeListener<Long> rateValuePropertyListener;
    
    private ChangeListener<TimeUnit> rateUnitPropertyListener;

    @FXML private TextField rateValue;
    @FXML private ComboBox<TimeUnit> rateUnit;
    @FXML private CheckBox borderDisplay;
    @FXML private ColorPicker borderColor;
    @FXML private CheckBox zeroDisplay;
    @FXML private ColorPicker zeroColor;
    @FXML private CheckBox timeFlow;
    
    public GraphPropertiesPaneController()
    {
        rateValuePropertyListener = new RateValuePropertyListener();
        rateValueInputListener = new RateValueInputListener();
        rateValueProperty = new SimpleObjectProperty<>();
        rateUnitProperty = new SimpleObjectProperty<>();
        rateUnitSelectionListener = new ValueSetter<>( rateUnitProperty );
    }

    /**
     * Создает панель для выбора и установки параметров рисования графика.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public Node build()
    {
        rateValue = new TextField();
        rateValue.setId( "rateValue" );
        rateValue.setPrefColumnCount( 6 );
        rateValue.setFocusTraversable( true );

        rateUnit = new ComboBox<>();
        rateUnit.setId( "rateUnit" );
        rateUnit.setFocusTraversable( true );
        
        borderDisplay = new CheckBox();
        borderDisplay.setId( "borderDisplay" );
        borderDisplay.setFocusTraversable( true );
        borderDisplay.setIndeterminate( false );
        
        borderColor = new ColorPicker();
        borderColor.setId( "borderColor" );
        borderColor.setFocusTraversable( false ); //TODO true RT-21549
        
        zeroDisplay = new CheckBox();
        zeroDisplay.setId( "zeroDisplay" );
        zeroDisplay.setFocusTraversable( true );
        zeroDisplay.setIndeterminate( false );
        
        zeroColor = new ColorPicker();
        zeroColor.setId( "zeroColor" );
        zeroColor.setFocusTraversable( false ); //TODO true RT-21549
        
        timeFlow = new CheckBox();
        timeFlow.setId( "timeFlow" );
        timeFlow.setFocusTraversable( true );
        zeroDisplay.setIndeterminate( false );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "properties.graph.rate" ) ), 0, 0 );
        pane.add( rateValue, 1, 0 );
        pane.add( rateUnit, 2, 0 );
        pane.add( new Label( LOGGER.text( "properties.graph.border" ) ), 0, 1 );
        pane.add( borderDisplay, 1, 1 );
        pane.add( borderColor, 2, 1 );
        pane.add( new Label( LOGGER.text( "properties.graph.zero" ) ), 0, 2 );
        pane.add( zeroDisplay, 1, 2 );
        pane.add( zeroColor, 2, 2 );
        pane.add( new Label( LOGGER.text( "properties.graph.flow" ) ), 0, 3 );
        pane.add( timeFlow, 1, 3 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
        rateValue.textProperty().addListener( new WeakChangeListener<>( rateValueInputListener ) );
        rateValueProperty.addListener( new WeakChangeListener<>( rateValuePropertyListener ) );
        
        rateUnit.getItems().addAll( Arrays.asList( TimeUnit.values() ) );
        rateUnit.getSelectionModel().selectedItemProperty()
                .addListener( new WeakChangeListener<>( rateUnitSelectionListener ) );
        rateUnitPropertyListener = new ListeningComboBoxSetter<>( rateUnit );
        rateUnitProperty.addListener( new WeakChangeListener<>( rateUnitPropertyListener ) );
        
        borderColor.disableProperty().bind( Bindings.not( borderDisplay.selectedProperty() ) );
        
        zeroColor.disableProperty().bind( Bindings.not( zeroDisplay.selectedProperty() ) );
    }
    
    Property<Long> rateValueProperty()
    {
        return rateValueProperty;
    }

    Property<TimeUnit> rateUnitProperty()
    {
        return rateUnitProperty;
    }

    Property<Boolean> borderDisplayProperty()
    {
        return borderDisplay.selectedProperty();
    }

    Property<Color> borderColorProperty()
    {
        return borderColor.valueProperty();
    }

    Property<Boolean> zeroDisplayProperty()
    {
        return zeroDisplay.selectedProperty();
    }

    Property<Color> zeroColorProperty()
    {
        return zeroColor.valueProperty();
    }

    Property<Boolean> timeFlowProperty()
    {
        return timeFlow.selectedProperty();
    }

    /**
     * @deprecated RT-34098
     */
    void resetColorPicker()
    {
        borderColor.fireEvent( new ActionEvent() );
        zeroColor.fireEvent( new ActionEvent() );
    }

    private class RateValuePropertyListener implements ChangeListener<Long>
    {
        @Override
        public void changed( ObservableValue<? extends Long> observable, 
                            Long oldValue, Long newValue )
        {
            rateValue.textProperty().setValue( newValue != null ? Long.toString( newValue ) : null );
        }
    }

    private class RateValueInputListener implements ChangeListener<String>
    {
        @Override
        public void changed( ObservableValue<? extends String> observable, 
                            String oldValue, String newValue )
        {
            try
            {
                Long value = Long.valueOf( newValue );
                if( value < 0 ) throw new NumberFormatException( "Negative" );
                rateValueProperty.setValue( value );
                rateValue.setStyle( null );
            }
            catch( NumberFormatException ex )
            {
                if( newValue != null ) LOGGER.log( "001003001W", newValue, ex.getMessage() );
                rateValue.setStyle( JavaFX.STYLE_WRONG_TEXT_VALUE );
            }
        }
    }
    
}
