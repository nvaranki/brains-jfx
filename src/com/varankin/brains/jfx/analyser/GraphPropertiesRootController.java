package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.ValueSetter;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.concurrent.TimeUnit;
import javafx.beans.binding.Bindings;
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
        buttonApply.disableProperty().bind( Bindings.not( propertiesController.changedProperty() ) );
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
        
    private void applyChanges()
    {
        JavaFX.setChangedValue( propertiesController.getRateValueConverted(), rateValue );
        JavaFX.setChangedValue( propertiesController.getRateUnitProperty(), rateUnit );
        JavaFX.setChangedValue( propertiesController.getBorderDisplayProperty(), borderDisplay );
        JavaFX.setChangedValue( propertiesController.getBorderColorProperty(), borderColor );
        JavaFX.setChangedValue( propertiesController.getZeroDisplayProperty(), zeroDisplay );
        JavaFX.setChangedValue( propertiesController.getZeroColorProperty(), zeroColor );
        JavaFX.setChangedValue( propertiesController.getTimeFlowProperty(), timeFlow );
        propertiesController.changedProperty().setValue( Boolean.FALSE );
    }
    
    void setRateValueProperty( final ObjectProperty<Long> property )
    {
        rateValue = property;
        property.addListener( new ChangeListener<Long>() 
        {
            @Override
            public void changed( ObservableValue<? extends Long> observable, Long oldValue, Long newValue )
            {
                propertiesController.getRateValueProperty().setValue( Long.toString( property.getValue() ) );
            }
        } );
    }

    void setRateUnitProperty( ObjectProperty<TimeUnit> property )
    {
        rateUnit = property;
        property.addListener( new ValueSetter<>( propertiesController.getRateUnitProperty() ) );
    }

    void setBorderDisplayProperty( BooleanProperty property )
    {
        borderDisplay = property;
        property.addListener( new ValueSetter<>( propertiesController.getBorderDisplayProperty() ) );
    }

    void setBorderColorProperty( ObjectProperty<Color> property )
    {
        borderColor = property;
        property.addListener( new ValueSetter<>( propertiesController.getBorderColorProperty() ) );
    }

    void setZeroDisplayProperty( BooleanProperty property )
    {
        zeroDisplay = property;
        property.addListener( new ValueSetter<>( propertiesController.getZeroDisplayProperty() ) );
    }

    void setZeroColorProperty( ObjectProperty<Color> property )
    {
        zeroColor = property;
        property.addListener( new ValueSetter<>( propertiesController.getZeroColorProperty() ) );
    }

    void setFlowModeProperty( BooleanProperty property )
    {
        timeFlow = property;
        property.addListener( new ValueSetter<>( propertiesController.getTimeFlowProperty() ) );
    }

    void reset()
    {
        propertiesController.getRateValueProperty().setValue( Long.toString( rateValue.getValue() ) );
        propertiesController.getRateUnitProperty().setValue( rateUnit.getValue() );
        propertiesController.getBorderDisplayProperty().setValue( borderDisplay.getValue() );
        propertiesController.getBorderColorProperty().setValue( borderColor.getValue() );
        propertiesController.getZeroDisplayProperty().setValue( zeroDisplay.getValue() );
        propertiesController.getZeroColorProperty().setValue( zeroColor.getValue() );
        propertiesController.getTimeFlowProperty().setValue( timeFlow.getValue() );
        propertiesController.reset();
    }
}
