package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.PropertyGate;
import com.varankin.brains.jfx.ChangedTrigger;
import com.varankin.brains.jfx.ObjectBindings;
import com.varankin.util.LoggerX;
import java.util.concurrent.TimeUnit;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Builder;

/**
 * FXML-контроллер панели диалога для выбора и установки параметров рисования графика.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class GraphPropertiesController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( GraphPropertiesController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/GraphProperties.css";
    private static final String CSS_CLASS = "graph-properties";
    
    static final String RESOURCE_FXML = "/fxml/analyser/GraphProperties.fxml";

    private final PropertyGate<Long> rateValueGate;
    private final PropertyGate<TimeUnit> rateUnitGate;
    private final PropertyGate<Boolean> borderDisplayGate;
    private final PropertyGate<Color> borderColorGate;
    private final PropertyGate<Boolean> zeroDisplayGate;
    private final PropertyGate<Color> zeroColorGate;
    private final PropertyGate<Boolean> timeFlowGate;
    private final ChangedTrigger changedFunction;

    private BooleanBinding changedBinding;
    
    @FXML private Node properties;
    @FXML private Button buttonOK, buttonApply;
    @FXML private GraphPropertiesPaneController propertiesController;

    public GraphPropertiesController()
    {
        changedFunction = new ChangedTrigger();
        rateValueGate = new PropertyGate<>();
        rateUnitGate = new PropertyGate<>();
        borderDisplayGate = new PropertyGate<>();
        borderColorGate = new PropertyGate<>();
        zeroDisplayGate = new PropertyGate<>();
        zeroColorGate = new PropertyGate<>();
        timeFlowGate = new PropertyGate<>();
    }
    
    /**
     * Создает панель диалога для выбора и установки параметров рисования графика.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public BorderPane build()
    {
        propertiesController = new GraphPropertiesPaneController();
        
        properties = propertiesController.build();
        properties.setId( "properties" );

        buttonOK = new Button( LOGGER.text( "button.ok" ) );
        buttonOK.setId( "buttonOK" );
        buttonOK.setDefaultButton( true );
        buttonOK.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionOK( event );
            }
        } );

        buttonApply = new Button( LOGGER.text( "button.apply" ) );
        buttonApply.setId( "buttonApply" );
        buttonApply.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionApply( event );
            }
        } );

        Button buttonCancel = new Button( LOGGER.text( "button.cancel" ) );
        buttonCancel.setCancelButton( true );
        buttonCancel.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionCancel( event );
            }
        } );

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
                propertiesController.rateValueProperty(),
                propertiesController.rateUnitProperty(),
                propertiesController.borderDisplayProperty(),
                propertiesController.borderColorProperty(),
                propertiesController.zeroDisplayProperty(),
                propertiesController.zeroColorProperty(),
                propertiesController.timeFlowProperty() );
        ReadOnlyBooleanProperty validProperty = propertiesController.validProperty();
        buttonOK.disableProperty().bind( Bindings.not( validProperty ) );
        buttonApply.disableProperty().bind( Bindings.not( Bindings.and( changedBinding, validProperty ) ) );
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
        
    void bindRateValueProperty( Property<Long> property )
    {
        rateValueGate.bind( property, propertiesController.rateValueProperty() );
    }

    void bindRateUnitProperty( Property<TimeUnit> property )
    {
        rateUnitGate.bind( property, propertiesController.rateUnitProperty() );
    }

    void bindBorderDisplayProperty( Property<Boolean> property )
    {
        borderDisplayGate.bind( property, propertiesController.borderDisplayProperty() );
    }

    void bindBorderColorProperty( Property<Color> property )
    {
        borderColorGate.bind( property, propertiesController.borderColorProperty() );
    }

    void bindZeroDisplayProperty( Property<Boolean> property )
    {
        zeroDisplayGate.bind( property, propertiesController.zeroDisplayProperty() );
    }

    void bindZeroColorProperty( Property<Color> property )
    {
        zeroColorGate.bind( property, propertiesController.zeroColorProperty() );
    }

    void bindFlowModeProperty( Property<Boolean> property )
    {
        timeFlowGate.bind( property, propertiesController.timeFlowProperty() );
    }

    private void applyChanges()
    {
        // установить текущие значения, если они отличаются
        rateValueGate.pullDistinctValue();
        rateUnitGate.pullDistinctValue();
        borderDisplayGate.pullDistinctValue();
        borderColorGate.pullDistinctValue();
        zeroDisplayGate.pullDistinctValue();
        zeroColorGate.pullDistinctValue();
        timeFlowGate.pullDistinctValue();
        // установить статус
        changedFunction.setValue( false );
        changedBinding.invalidate();
    }
    
    void reset()
    {
        // сбросить прежние значения и установить текущие значения
        rateValueGate.forceReset();
        rateUnitGate.forceReset();
        borderDisplayGate.forceReset();
        borderColorGate.forceReset();
        zeroDisplayGate.forceReset();
        zeroColorGate.forceReset();
        timeFlowGate.forceReset();
        propertiesController.resetColorPicker();
        // установить статус
        changedFunction.setValue( false );
        changedBinding.invalidate();
    }

}
