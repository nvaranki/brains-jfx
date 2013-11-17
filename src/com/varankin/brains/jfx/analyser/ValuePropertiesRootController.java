package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BoundWritableValue;
import com.varankin.brains.jfx.ChangedTrigger;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableValue;
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
    
//    private DotPainter painter;
    private final ChangedTrigger changedFunction;

    private BooleanBinding changedBinding;
    private WritableValue<Color> color;
    private WritableValue<int[][]> pattern;
    private WritableValue<Integer> scale;
    
    @FXML private Pane properties;
    @FXML private Button buttonApply;
    @FXML private ValuePropertiesPaneController propertiesController;

    public ValuePropertiesRootController()
    {
        changedFunction = new ChangedTrigger();
        color = new SimpleObjectProperty<>();
        pattern = new SimpleObjectProperty<>();
        scale = new SimpleObjectProperty<>( 3 );
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
                propertiesController.patternProperty(),
                propertiesController.scaleProperty() );
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
        color = new BoundWritableValue<>( property, propertiesController.colorProperty() );
    }

    void bindPatternProperty( Property<int[][]> property )
    {
        pattern = new BoundWritableValue<>( property, propertiesController.patternProperty() );
    }

    void bindScaleProperty( Property<Integer> property )
    {
        scale = new BoundWritableValue<>( property, propertiesController.scaleProperty() );
    }

    private void applyChanges()
    {
        // установить текущие значения, если они отличаются
        JavaFX.setDistinctValue( propertiesController.colorProperty().getValue(), color );
        JavaFX.setDistinctValue( propertiesController.patternProperty().getValue(), pattern );
        JavaFX.setDistinctValue( propertiesController.scaleProperty().getValue(), scale );
        // установить статус
        changedFunction.setValue( false );
        changedBinding.invalidate();
    }

    void reset()
    {
        // сбросить прежние значения и установить текущие значения
        JavaFX.resetValue( color.getValue(), propertiesController.colorProperty() );
        JavaFX.resetValue( pattern.getValue(), propertiesController.patternProperty() );
        JavaFX.resetValue( scale.getValue(), propertiesController.scaleProperty() );
        propertiesController.resetColorPicker();
        // установить статус
        changedFunction.setValue( false );
        changedBinding.invalidate();
    }

}
