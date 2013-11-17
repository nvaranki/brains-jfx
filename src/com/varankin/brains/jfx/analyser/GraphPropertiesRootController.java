package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BoundWritableValue;
import com.varankin.brains.jfx.ChangedTrigger;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.concurrent.TimeUnit;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.*;
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
public final class GraphPropertiesRootController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( GraphPropertiesRootController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/GraphPropertiesRoot.css";
    private static final String CSS_CLASS = "graph-properties-root";

    private final ChangedTrigger changedFunction;

    private BooleanBinding changedBinding;
    private WritableValue<Long> rateValue;
    private WritableValue<TimeUnit> rateUnit;
    private WritableValue<Boolean> borderDisplay;
    private WritableValue<Color> borderColor;
    private WritableValue<Boolean> zeroDisplay;
    private WritableValue<Color> zeroColor;
    private WritableValue<Boolean> timeFlow;
    
    @FXML private Node properties;
    @FXML private Button buttonApply;
    @FXML private GraphPropertiesPaneController propertiesController;

    public GraphPropertiesRootController()
    {
        changedFunction = new ChangedTrigger();
        rateValue = new SimpleObjectProperty<>();
        rateUnit = new SimpleObjectProperty<>();
        borderDisplay = new SimpleObjectProperty<>();
        borderColor = new SimpleObjectProperty<>();
        zeroDisplay = new SimpleObjectProperty<>();
        zeroColor = new SimpleObjectProperty<>();
        timeFlow = new SimpleObjectProperty<>();
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
                propertiesController.rateValueProperty(),
                propertiesController.rateUnitProperty(),
                propertiesController.borderDisplayProperty(),
                propertiesController.borderColorProperty(),
                propertiesController.zeroDisplayProperty(),
                propertiesController.zeroColorProperty(),
                propertiesController.timeFlowProperty() );
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
        
    void bindRateValueProperty( Property<Long> property )
    {
        rateValue = new BoundWritableValue<>( property, propertiesController.rateValueProperty() );
    }

    void bindRateUnitProperty( Property<TimeUnit> property )
    {
        rateUnit = new BoundWritableValue<>( property, propertiesController.rateUnitProperty() );
    }

    void bindBorderDisplayProperty( Property<Boolean> property )
    {
        borderDisplay = new BoundWritableValue<>( property, propertiesController.borderDisplayProperty() );
    }

    void bindBorderColorProperty( Property<Color> property )
    {
        borderColor = new BoundWritableValue<>( property, propertiesController.borderColorProperty() );
    }

    void bindZeroDisplayProperty( Property<Boolean> property )
    {
        zeroDisplay = new BoundWritableValue<>( property, propertiesController.zeroDisplayProperty() );
    }

    void bindZeroColorProperty( Property<Color> property )
    {
        zeroColor = new BoundWritableValue<>( property, propertiesController.zeroColorProperty() );
    }

    void bindFlowModeProperty( Property<Boolean> property )
    {
        timeFlow = new BoundWritableValue<>( property, propertiesController.timeFlowProperty() );
    }

    private void applyChanges()
    {
        // установить текущие значения, если они отличаются
        JavaFX.setDistinctValue( propertiesController.rateValueProperty().getValue(), rateValue );
        JavaFX.setDistinctValue( propertiesController.rateUnitProperty().getValue(), rateUnit );
        JavaFX.setDistinctValue( propertiesController.borderDisplayProperty().getValue(), borderDisplay );
        JavaFX.setDistinctValue( propertiesController.borderColorProperty().getValue(), borderColor );
        JavaFX.setDistinctValue( propertiesController.zeroDisplayProperty().getValue(), zeroDisplay );
        JavaFX.setDistinctValue( propertiesController.zeroColorProperty().getValue(), zeroColor );
        JavaFX.setDistinctValue( propertiesController.timeFlowProperty().getValue(), timeFlow );
        // установить статус
        changedFunction.setValue( false );
        changedBinding.invalidate();
    }
    
    void reset()
    {
        // сбросить прежние значения и установить текущие значения
        JavaFX.resetValue( rateValue.getValue(), propertiesController.rateValueProperty() );
        JavaFX.resetValue( rateUnit.getValue(), propertiesController.rateUnitProperty() );
        JavaFX.resetValue( borderDisplay.getValue(), propertiesController.borderDisplayProperty() );
        JavaFX.resetValue( borderColor.getValue(), propertiesController.borderColorProperty() );
        JavaFX.resetValue( zeroDisplay.getValue(), propertiesController.zeroDisplayProperty() );
        JavaFX.resetValue( zeroColor.getValue(), propertiesController.zeroColorProperty() );
        JavaFX.resetValue( timeFlow.getValue(), propertiesController.timeFlowProperty() );
        propertiesController.resetColorPicker();
        // установить статус
        changedFunction.setValue( false );
        changedBinding.invalidate();
    }

}
