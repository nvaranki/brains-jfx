package com.varankin.brains.jfx.analyser;

import com.varankin.util.LoggerX;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javafx.beans.binding.ObjectBinding;
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

    private final BooleanProperty changedProperty;
    private final ObjectProperty<TimeUnit> rateUnitProperty; // <--> selectionModel.selectedItemProperty
    
    private ObservableValue<Long> rateValueLong; // String <--> Long

    @FXML private TextField rateValue;
    @FXML private ComboBox<TimeUnit> rateUnit;
    @FXML private CheckBox borderDisplay;
    @FXML private ColorPicker borderColor;
    @FXML private CheckBox zeroDisplay;
    @FXML private ColorPicker zeroColor;
    @FXML private CheckBox timeFlow;
    
    public GraphPropertiesPaneController()
    {
        changedProperty = new SimpleBooleanProperty( false );
        rateUnitProperty = new SimpleObjectProperty<>();
    }

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
        
        borderColor = new ColorPicker();
        borderColor.setId( "borderColor" );
        
        zeroDisplay = new CheckBox();
        zeroDisplay.setId( "zeroDisplay" );
        
        zeroColor = new ColorPicker();
        zeroColor.setId( "zeroColor" );
        
        timeFlow = new CheckBox();
        timeFlow.setId( "timeFlow" );
        
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
        rateValueLong = new ObjectBinding<Long>() 
        {
            {
                super.bind( rateValue.textProperty() );
            }
            
            @Override
            protected Long computeValue()
            {
                String text = rateValue.textProperty().getValue();
                try
                {
                    return Long.valueOf( text );
                }
                catch( NumberFormatException _ )
                {
                    LOGGER.log( "001003001S", text );
                    return null;
                }
            }
        };
        
        rateUnit.getItems().addAll( Arrays.asList( TimeUnit.values() ) );
        rateUnit.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<TimeUnit>() 
        {
            @Override
            public void changed( ObservableValue<? extends TimeUnit> observable, 
                                TimeUnit oldValue, TimeUnit newValue )
            {
                if( newValue != oldValue )
                    rateUnitProperty.setValue( newValue );
            }
        } );
        
        rateUnitProperty.addListener( new ChangeListener<TimeUnit>() 
        {
            @Override
            public void changed( ObservableValue<? extends TimeUnit> observable, 
                                TimeUnit oldValue, TimeUnit newValue )
            {
                if( newValue != oldValue )
                {
                    int index = rateUnit.getItems().indexOf( newValue );
                    if( index >= 0 )
                        rateUnit.getSelectionModel().select( index );
                    else
                        LOGGER.getLogger().log( Level.FINE, "Unsupported TimeUnit.{}", newValue.name() );
                }
            }
        } );
        
        borderColor.disableProperty().bind( borderDisplay.disabledProperty() );
        
        zeroColor.disableProperty().bind( zeroDisplay.disabledProperty() );
    }
    
    BooleanProperty changedProperty()
    {
        return changedProperty;
    }

    StringProperty getRateValueProperty()
    {
        return rateValue.textProperty();
    }

    ObservableValue<Long> getRateValueConverted()
    {
        return rateValueLong;
    }

    ObjectProperty<TimeUnit> getRateUnitProperty()
    {
        return rateUnitProperty;
    }

    BooleanProperty getBorderDisplayProperty()
    {
        return borderDisplay.selectedProperty();
    }

    ObjectProperty<Color> getBorderColorProperty()
    {
        return borderColor.valueProperty();
    }

    BooleanProperty getZeroDisplayProperty()
    {
        return zeroDisplay.selectedProperty();
    }

    ObjectProperty<Color> getZeroColorProperty()
    {
        return zeroColor.valueProperty();
    }

    BooleanProperty getTimeFlowProperty()
    {
        return timeFlow.selectedProperty();
    }

    /**
     * @deprecated RT-34098
     */
    void reset()
    {
        borderColor.fireEvent( new ActionEvent() );
        zeroColor.fireEvent( new ActionEvent() );
    }
    
}
