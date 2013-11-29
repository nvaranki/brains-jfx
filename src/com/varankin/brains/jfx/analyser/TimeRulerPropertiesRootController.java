package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.ChangedTrigger;
import com.varankin.brains.jfx.ObjectBindings;
import com.varankin.brains.jfx.PropertyGate;
import com.varankin.util.LoggerX;
import java.util.concurrent.TimeUnit;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Builder;

/**
 * FXML-контроллер панели диалога для выбора и установки параметров оси времени.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class TimeRulerPropertiesRootController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TimeRulerPropertiesRootController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/TimeRulerPropertiesRoot.css";
    private static final String CSS_CLASS = "time-ruler-properties-root";
    
    static final String RESOURCE_FXML = "/fxml/analyser/TimeRulerPropertiesRoot.fxml";
    
    private final PropertyGate<Long> durationGate;
    private final PropertyGate<Long> excessGate;
    private final PropertyGate<TimeUnit> unitGate;
    private final PropertyGate<Color> textColorGate;
    private final PropertyGate<Font> textFontGate;
    private final PropertyGate<Color> tickColorGate;
    private final ChangedTrigger changedFunction;

    private BooleanBinding changedBinding;
    
    @FXML private Pane properties;
    @FXML private Button buttonOK, buttonApply;
    @FXML private TimeRulerPropertiesPaneController propertiesController;

    public TimeRulerPropertiesRootController()
    {
        durationGate = new PropertyGate<>();
        excessGate = new PropertyGate<>();
        unitGate = new PropertyGate<>();
        textColorGate = new PropertyGate<>();
        textFontGate = new PropertyGate<>();
        tickColorGate = new PropertyGate<>();
        changedFunction = new ChangedTrigger();
    }
    
    /**
     * Создает панель диалога для выбора и установки параметров оси значений.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public BorderPane build()
    {
        propertiesController = new TimeRulerPropertiesPaneController();
        
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
                propertiesController.durationProperty(),
                propertiesController.excessProperty(),
                propertiesController.unitProperty(),
                propertiesController.textColorProperty(),
                propertiesController.textFontProperty(),
                propertiesController.tickColorProperty());
        BooleanBinding validBinding = Bindings.and
        ( 
            ObjectBindings.isNotNull( propertiesController.durationProperty() ),
            ObjectBindings.isNotNull( propertiesController.excessProperty() )
        );
        buttonOK.disableProperty().bind( Bindings.not( validBinding ) );
        buttonApply.disableProperty().bind( Bindings.not( Bindings.and( changedBinding, validBinding ) ) );
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
        // установить текущие значения, если они отличаются
        // установить статус
        changedFunction.setValue( false );
        changedBinding.invalidate();
    }

    void reset()
    {
        // сбросить прежние значения и установить текущие значения
        // установить статус
        changedFunction.setValue( false );
        changedBinding.invalidate();
    }
    
}
