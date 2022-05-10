package com.varankin.brains.jfx.analyser;

import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Builder;

/**
 * FXML-контроллер панели диалога для выбора и установки параметров прорисовки отметок.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class ValuePropertiesController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValuePropertiesController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ValueProperties.css";
    private static final String CSS_CLASS = "value-properties";
    
    static final String RESOURCE_FXML = "/fxml/analyser/ValueProperties.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();
    
    private Value value;
    private Consumer<Value> action;
    
    @FXML private Button buttonOK;
    @FXML private Button buttonApply;
    @FXML private ValuePropertiesPaneController propertiesController;
    @FXML private ObservableConversionPaneController observableConversionPaneController;

    /**
     * Создает панель диалога для выбора и установки параметров прорисовки отметок.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель диалога.
     */
    @Override
    public BorderPane build()
    {
        buttonOK = new Button( LOGGER.text( "button.ok" ) );
        buttonOK.setOnAction( this::onActionOK );
        buttonOK.setDefaultButton( true );

        buttonApply = new Button( LOGGER.text( "button.apply" ) );
        buttonApply.setOnAction( this::onActionApply );
        buttonApply.setId( "buttonApply" );

        Button buttonCancel = new Button( LOGGER.text( "button.cancel" ) );
        buttonCancel.setOnAction( this::onActionCancel );
        buttonCancel.setCancelButton( true );

        HBox buttonBar = new HBox();
        buttonBar.getChildren().addAll( buttonOK, buttonCancel, buttonApply );

        Tab tabConversion = new Tab();
        observableConversionPaneController = new ObservableConversionPaneController();
        tabConversion.setContent( observableConversionPaneController.build() );
        tabConversion.setText( LOGGER.text( "observable.setup.conversion.title" ) );
        tabConversion.setClosable( false );
        
        propertiesController = new ValuePropertiesPaneController();
        Tab tabPainting = new Tab();
        tabPainting.setContent( propertiesController.build() );
        tabPainting.setText( LOGGER.text( "observable.setup.presentation.title" ) );
        tabPainting.setClosable( false );
        
        TabPane tabs = new TabPane();
        tabs.getTabs().addAll( tabPainting, tabConversion );

        BorderPane pane = new BorderPane();
        pane.setCenter( tabs );
        pane.setBottom( buttonBar );

        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );
        pane.getStyleClass().add( CSS_CLASS );
        
        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
        BooleanBinding changed = Bindings.or( 
                propertiesController.changedProperty(),
                observableConversionPaneController.changedProperty() );
        BooleanBinding valid = Bindings.and( 
                propertiesController.validProperty(),
                observableConversionPaneController.validProperty() );
        buttonOK.disableProperty().bind( Bindings.not( valid ) );
        buttonApply.disableProperty().bind( Bindings.not( Bindings.and( valid, changed ) ) );
    }
    
    @FXML
    void onActionOK( ActionEvent event )
    {
        event.consume();
        action.accept( value );
        buttonApply.getScene().getWindow().hide();
    }
    
    @FXML
    void onActionApply( ActionEvent event )
    {
        event.consume();
        action.accept( value );
    }
    
    @FXML
    void onActionCancel( ActionEvent event )
    {
        event.consume();
        buttonApply.getScene().getWindow().hide();
    }
    
    void setAction( Consumer<Value> consumer )
    {
        action = this::applyOptionsToValue;
        action = action.andThen( consumer );
    }

    void setValue( Value value )
    {
        this.value = value;
        observableConversionPaneController.copyOptions( value );
        propertiesController.copyOptions( value );
    }
    
    private void applyOptionsToValue( Value value )
    {
        propertiesController.applyOptions( value );
        observableConversionPaneController.applyOptions( value );
    }

}
