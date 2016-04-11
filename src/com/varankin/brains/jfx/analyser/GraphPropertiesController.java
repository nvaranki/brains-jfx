package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.ChangedTrigger;
import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Builder;

/**
 * FXML-контроллер панели диалога для выбора и установки параметров рисования графика.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class GraphPropertiesController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( GraphPropertiesController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/GraphProperties.css";
    private static final String CSS_CLASS = "graph-properties";
    
    static final String RESOURCE_FXML = "/fxml/analyser/GraphProperties.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private final ChangedTrigger changedFunction;

    private BooleanBinding changedBinding;
    private Consumer<GraphPropertiesController> action;
    
    @FXML private Node properties;
    @FXML private Button buttonOK, buttonApply;
    @FXML private GraphPropertiesPaneController propertiesController;

    public GraphPropertiesController()
    {
        changedFunction = new ChangedTrigger();
    }
    
    /**
     * Создает панель диалога для выбора и установки параметров рисования графика.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель диалога.
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
        buttonOK.setOnAction( this::onActionOK );

        buttonApply = new Button( LOGGER.text( "button.apply" ) );
        buttonApply.setId( "buttonApply" );
        buttonApply.setOnAction( this::onActionApply );

        Button buttonCancel = new Button( LOGGER.text( "button.cancel" ) );
        buttonCancel.setCancelButton( true );
        buttonCancel.setOnAction( this::onActionCancel );

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
                propertiesController.labelProperty(),
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
        event.consume();
        action.accept( this );
        buttonApply.getScene().getWindow().hide();
    }
    
    @FXML
    void onActionApply( ActionEvent event )
    {
        event.consume();
        action.accept( this );
    }
    
    @FXML
    void onActionCancel( ActionEvent event )
    {
        event.consume();
        buttonApply.getScene().getWindow().hide();
    }
    
    GraphPropertiesPaneController propertiesController()
    {
        return propertiesController;
    }
        
    void setAction( Consumer<GraphPropertiesController> consumer )
    {
        action = consumer;
    }

    void setModified( boolean status )
    {
        changedFunction.setValue( status );
        changedBinding.invalidate();
    }

}
