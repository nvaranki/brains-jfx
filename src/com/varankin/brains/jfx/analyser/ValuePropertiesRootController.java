package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.PropertyGate;
import com.varankin.brains.jfx.ChangedTrigger;
import com.varankin.util.LoggerX;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Builder;

/**
/**
 * FXML-контроллер панели диалога для выбора и установки параметров прорисовки отметок.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class ValuePropertiesRootController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValuePropertiesRootController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ValuePropertiesRoot.css";
    private static final String CSS_CLASS = "value-properties-root";
    
    private final PropertyGate<Color> colorGate;
    private final PropertyGate<int[][]> patternGate;
    private final PropertyGate<Integer> scaleGate;
    private final ChangedTrigger changedFunction;

    private BooleanBinding changedBinding;
    
    @FXML private Pane properties;
    @FXML private Button buttonApply;
    @FXML private ValuePropertiesPaneController propertiesController;

    public ValuePropertiesRootController()
    {
        changedFunction = new ChangedTrigger();
        colorGate = new PropertyGate<>();
        patternGate = new PropertyGate<>();
        scaleGate = new PropertyGate<>();
    }
    
    /**
     * Создает панель диалога для выбора и установки параметров прорисовки отметок.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public BorderPane build()
    {
        propertiesController = new ValuePropertiesPaneController();
        
        properties = propertiesController.build();
        properties.setId( "properties" );

        Button buttonOK = new Button( LOGGER.text( "button.ok" ) );
        buttonOK.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionOK( event );
            }
        } );
        buttonOK.setDefaultButton( true );

        buttonApply = new Button( LOGGER.text( "button.apply" ) );
        buttonApply.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionApply( event );
            }
        } );
        buttonApply.setId( "buttonApply" );

        Button buttonCancel = new Button( LOGGER.text( "button.cancel" ) );
        buttonCancel.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionCancel( event );
            }
        } );
        buttonCancel.setCancelButton( true );

        HBox buttonBar = new HBox();
        buttonBar.getChildren().addAll( buttonOK, buttonCancel, buttonApply );

        BorderPane pane = new BorderPane();
        pane.setCenter( properties );
        pane.setBottom( buttonBar );

        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );
        pane.getStyleClass().add( CSS_CLASS );
        
        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
        changedBinding = Bindings.createBooleanBinding( changedFunction, 
                propertiesController.colorProperty(),
                propertiesController.patternProperty() /*,
                propertiesController.scaleProperty() is not relevant */ );
        buttonApply.disableProperty().bind( Bindings.not( changedBinding ) );
    }
    
    @FXML
    void onActionOK( ActionEvent event )
    {
        applyChanges();
        buttonApply.getScene().getWindow().hide();
    }
    
    @FXML
    void onActionApply( ActionEvent event )
    {
        applyChanges();
    }
    
    @FXML
    void onActionCancel( ActionEvent event )
    {
        buttonApply.getScene().getWindow().hide();
    }
    
    void bindColorProperty( Property<Color> property )
    {
        colorGate.bind( property, propertiesController.colorProperty() );
    }

    void bindPatternProperty( Property<int[][]> property )
    {
        patternGate.bind( property, propertiesController.patternProperty() );
    }

    void bindScaleProperty( Property<Integer> property )
    {
        scaleGate.bind( property, propertiesController.scaleProperty() );
    }

    private void applyChanges()
    {
        // установить текущие значения, если они отличаются
        colorGate.pullDistinctValue();
        patternGate.pullDistinctValue();
        scaleGate.pullDistinctValue();
        // установить статус
        changedFunction.setValue( false );
        changedBinding.invalidate();
    }

    void reset()
    {
        // сбросить прежние значения и установить текущие значения
        colorGate.forceReset();
        patternGate.forceReset();
        scaleGate.forceReset();
        propertiesController.resetColorPicker();
        // установить статус
        changedFunction.setValue( false );
        changedBinding.invalidate();
    }

}
