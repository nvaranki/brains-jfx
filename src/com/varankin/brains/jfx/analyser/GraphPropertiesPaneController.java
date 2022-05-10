package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.*;
import com.varankin.brains.jfx.shared.EnumCallBack;
import com.varankin.brains.jfx.shared.EnumCell;
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
import javafx.scene.paint.Color;
import javafx.util.Builder;

/**
 * Панель выбора и установки параметров рисования графика.
 * 
 * @author &copy; 2020 Николай Варанкин
 */
public final class GraphPropertiesPaneController implements Builder<Node>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( GraphPropertiesPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/GraphPropertiesPane.css";
    private static final String CSS_CLASS = "graph-properties-pane";

    private final StringProperty labelProperty;
    private final ObjectProperty<Long> rateValueProperty;
    private final SingleSelectionProperty<TimeUnit> rateUnitProperty;
    private final ReadOnlyBooleanWrapper validProperty;

    @FXML private TextField label;
    @FXML private TextField rateValue;
    @FXML private ComboBox<TimeUnit> rateUnit;
    @FXML private CheckBox borderDisplay;
    @FXML private ColorPicker borderColor;
    @FXML private CheckBox zeroDisplay;
    @FXML private ColorPicker zeroColor;
    @FXML private CheckBox timeFlow;
    
    public GraphPropertiesPaneController()
    {
        labelProperty = new SimpleStringProperty();
        rateValueProperty = new SimpleObjectProperty<>();
        rateUnitProperty = new SingleSelectionProperty<>();
        validProperty = new ReadOnlyBooleanWrapper();
    }

    /**
     * Создает панель для выбора и установки параметров рисования графика.
     * Применяется в конфигурации без FXML.
     * 
     * @return созданная панель.
     */
    @Override
    public Node build()
    {
        label = new TextField();
        label.setId( "label" );
        label.setPrefColumnCount( 32 );
        label.setFocusTraversable( true );

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
        pane.add( new Label( LOGGER.text( "properties.graph.label" ) ), 0, 0 );
        pane.add( label, 1, 0, 2, 1 );
        pane.add( new Label( LOGGER.text( "properties.graph.rate" ) ), 0, 1 );
        pane.add( rateValue, 1, 1 );
        pane.add( rateUnit, 2, 1 );
        pane.add( new Label( LOGGER.text( "properties.graph.border" ) ), 0, 2 );
        pane.add( borderDisplay, 1, 2 );
        pane.add( borderColor, 2, 2 );
        pane.add( new Label( LOGGER.text( "properties.graph.zero" ) ), 0, 3 );
        pane.add( zeroDisplay, 1, 3 );
        pane.add( zeroColor, 2, 3 );
        pane.add( new Label( LOGGER.text( "properties.graph.flow" ) ), 0, 4 );
        pane.add( timeFlow, 1, 4 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
        labelProperty.bindBidirectional( label.textProperty() );
        Bindings.bindBidirectional( rateValue.textProperty(), rateValueProperty, 
                new PositiveLongConverter( rateValue ) );
        rateUnit.setCellFactory( new EnumCallBack<>( TimeUnit.class ) );
        rateUnit.setButtonCell( new EnumCell<>( TimeUnit.class ) );
        rateUnit.getItems().addAll( Arrays.asList( TimeUnit.values() ) );
        rateUnitProperty.setModel( rateUnit.getSelectionModel() );
        borderColor.disableProperty().bind( Bindings.not( borderDisplay.selectedProperty() ) );
        zeroColor.disableProperty().bind( Bindings.not( zeroDisplay.selectedProperty() ) );
        BooleanBinding validBinding = ObjectBindings.isNotNull( rateValueProperty() );
        validProperty.bind( validBinding );
    }
    
    StringProperty labelProperty()
    {
        return labelProperty;
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

    ReadOnlyBooleanProperty validProperty()
    {
        return validProperty.getReadOnlyProperty();
    }

    /**
     * @deprecated RT-34098
     */
    @Deprecated
    void resetColorPicker()
    {
        borderColor.fireEvent( new ActionEvent() );
        zeroColor.fireEvent( new ActionEvent() );
    }
    
}
