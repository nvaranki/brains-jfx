package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.ChangedTrigger;
import com.varankin.util.LoggerX;
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
import javafx.util.Builder;

/**
 * FXML-контроллер панели диалога для выбора и установки параметров оси значений.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class ValueRulerPropertiesRootController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValueRulerPropertiesRootController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ValueRulerPropertiesRoot.css";
    private static final String CSS_CLASS = "value-ruler-properties-root";
    
    static final String RESOURCE_FXML = "/fxml/analyser/ValueRulerPropertiesRoot.fxml";
    
    private final ChangedTrigger changedFunction;

    private BooleanBinding changedBinding;
    
    @FXML private Pane properties;
    @FXML private Button buttonApply;
    @FXML private ValueRulerPropertiesPaneController propertiesController;

    public ValueRulerPropertiesRootController()
    {
        changedFunction = new ChangedTrigger();
    }
    
    /**
     * Создает панель диалога для выбора и установки параметров оси значений.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public BorderPane build()
    {
        propertiesController = new ValueRulerPropertiesPaneController();
        
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
//        buttonApply.disableProperty().bind( Bindings.not( changedBinding ) );
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
//        changedBinding.invalidate();
    }

    void reset()
    {
        // сбросить прежние значения и установить текущие значения
        // установить статус
        changedFunction.setValue( false );
//        changedBinding.invalidate();
    }
    
}
