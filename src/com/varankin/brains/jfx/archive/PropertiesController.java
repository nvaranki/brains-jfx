package com.varankin.brains.jfx.archive;

import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Builder;

/**
 * FXML-контроллер диалога свойств элемента архива.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class PropertiesController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( PropertiesController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/Properties.css";
    private static final String CSS_CLASS = "properties";
    
    static final String RESOURCE_FXML = "/fxml/archive/Properties.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();
    
    private final ReadOnlyStringWrapper titleProperty;
    
    private BooleanBinding changedBinding;
    
    @FXML private Pane properties;
    @FXML private Button buttonOK, buttonApply;
    //@FXML private PropertiesPaneController propertiesController;

    public PropertiesController()
    {
        titleProperty = new ReadOnlyStringWrapper();
    }
    
    /**
     * Создает панель диалога свойств элемента архива.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель диалога.
     */
    @Override
    public BorderPane build()
    {
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
        //pane.setCenter( properties );
        pane.setBottom( buttonBar );

        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );
        pane.getStyleClass().add( CSS_CLASS );
        
        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
    }
    
    @FXML
    void onActionOK( ActionEvent event )
    {
        event.consume();
    }
    
    @FXML
    void onActionApply( ActionEvent event )
    {
        event.consume();
    }
    
    @FXML
    void onActionCancel( ActionEvent event )
    {
        buttonApply.getScene().getWindow().hide();
        event.consume();
    }

    ReadOnlyStringProperty titleProperty()
    {
        return titleProperty.getReadOnlyProperty();
    }

    void reset(  )
    {
        // сбросить прежние значения и установить текущие значения
        titleProperty.setValue( "Properties ?" ); //LOGGER.text( "properties.title", legend.getText() )
    }
    
}
