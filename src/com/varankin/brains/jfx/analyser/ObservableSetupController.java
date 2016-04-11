package com.varankin.brains.jfx.analyser;

import com.varankin.util.LoggerX;
import java.util.*;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;

/**
 * FXML-контроллер выбора параметров рисования наблюдаемого значения.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class ObservableSetupController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ObservableSetupController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ObservableSetup.css";
    private static final String CSS_CLASS = "observable-setup";
    
    static final String RESOURCE_FXML = "/fxml/analyser/ObservableSetup.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private Consumer<Value> action;
    private Value value;
    
    @FXML private Button buttonOK, buttonCancel;
    @FXML private ObservableConversionPaneController observableConversionPaneController;
    @FXML private ValuePropertiesPaneController valuePropertiesPaneController;

    @Override
    public Parent build()
    {

        buttonOK = new Button( LOGGER.text( "button.create" ) );
        buttonOK.setDefaultButton( true );
        buttonOK.setOnAction( this::onActionOK );

        buttonCancel = new Button( LOGGER.text( "button.cancel" ) );
        buttonCancel.setCancelButton( true );
        buttonCancel.setOnAction( this::onActionCancel );

        HBox buttonBar = new HBox();
        buttonBar.getChildren().addAll( buttonOK, buttonCancel );

        Tab tabConversion = new Tab();
        observableConversionPaneController = new ObservableConversionPaneController();
        tabConversion.setContent( observableConversionPaneController.build() );
        tabConversion.setText( LOGGER.text( "observable.setup.conversion.title" ) );
        tabConversion.setClosable( false );
        
        valuePropertiesPaneController = new ValuePropertiesPaneController();
        Tab tabPainting = new Tab();
        tabPainting.setContent( valuePropertiesPaneController.build() );
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
        BooleanBinding valid = Bindings.and( 
                valuePropertiesPaneController.validProperty(),
                observableConversionPaneController.validProperty() );
        buttonOK.disableProperty().bind( Bindings.not( valid ) );
    }
    
    @FXML
    void onActionOK( ActionEvent event )
    {
        event.consume();
        action.accept( value );
        buttonOK.getScene().getWindow().hide();
    }
    
    @FXML
    void onActionCancel( ActionEvent event )
    {
        event.consume();
        buttonOK.getScene().getWindow().hide();
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
        valuePropertiesPaneController.copyOptions( value );
    }

    private void applyOptionsToValue( Value value )
    {
        valuePropertiesPaneController.applyOptions( value );
        observableConversionPaneController.applyOptions( value );
    }
    
}
