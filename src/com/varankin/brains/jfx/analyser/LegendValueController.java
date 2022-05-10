package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Builder;

/**
 * FXML-контроллер элемента управления прорисовкой отметок.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class LegendValueController implements Builder<CheckBox>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( LegendValueController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/LegendValue.css";
    private static final String CSS_CLASS = "legend-value";

    static final String RESOURCE_FXML = "/fxml/analyser/LegendValue.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();
    
    private ValuePropertiesStage properties;
    
    @FXML private CheckBox legend;
    @FXML private ContextMenu popup;
    @FXML private MenuItem menuItemShow;
    @FXML private MenuItem menuItemHide;
    @FXML private MenuItem menuItemRemove;
    @FXML private MenuItem menuItemProperties;

    /**
     * Создает элемент управления прорисовкой отметок.
     * Применяется в конфигурации без FXML.
     * 
     * @return элемент управления.
     */
    @Override
    public CheckBox build()
    {
        menuItemShow = new MenuItem();
        menuItemShow.setOnAction( this::onActionShow );
        
        menuItemHide = new MenuItem();
        menuItemHide.setOnAction( this::onActionHide );
        
        menuItemRemove = new MenuItem();
        menuItemRemove.setOnAction( this::onActionRemove );
        
        menuItemProperties = new MenuItem( LOGGER.text( "control.popup.properties" ) );
        menuItemProperties.setOnAction( this::onActionProperties );
        menuItemProperties.setGraphic( JavaFX.icon( "icons16x16/properties.png" ) );
        
        popup = new ContextMenu();
        popup.setId( "popup" );
        popup.getItems().addAll( menuItemShow, menuItemHide, menuItemRemove, menuItemProperties );

        legend = new CheckBox();
        legend.setId( "legend" );
        legend.setSelected( false );
        legend.setContextMenu( popup );
        
        legend.getStyleClass().add( CSS_CLASS );
        legend.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return legend;
    }
    
    @FXML
    protected void initialize()
    {
        menuItemShow.textProperty().bind( new TextWithName( "control.popup.show" ) );
        menuItemShow.disableProperty().bind( legend.selectedProperty() );

        menuItemHide.textProperty().bind( new TextWithName( "control.popup.hide" ) );
        menuItemHide.disableProperty().bind( Bindings.not( legend.selectedProperty() ) );
        
        menuItemRemove.textProperty().bind( new TextWithName( "control.popup.remove" ) );
                
        legend.getProperties().addListener( this::onValueAddRemove ); // set/getUserData(Value)
    }

    ContextMenu getContextMenu() 
    {
        return popup;
    }

    @FXML
    private void onActionShow( ActionEvent event )
    {
        legend.selectedProperty().setValue( Boolean.TRUE );
        event.consume();
    }
    
    @FXML
    private void onActionHide( ActionEvent event )
    {
        legend.selectedProperty().setValue( Boolean.FALSE );
        event.consume();
    }
    
    @FXML
    private void onActionRemove( ActionEvent event )
    {
        // остановить прорисовку
        legend.selectedProperty().setValue( Boolean.FALSE );
        // убрать с экрана
        Parent parent = legend.getParent();
        if( parent instanceof Pane )
            ((Pane)parent).getChildren().remove( legend );
        else
            LOGGER.log( "001002002W", legend.getText() );
        // освободить GUI
        legend.setUserData( null );
        // TODO what to do with open queue?
        event.consume();
    }
        
    @FXML
    private void onActionProperties( ActionEvent event )
    {
        if( properties == null )
        {
            properties = new ValuePropertiesStage( this::applyProperties );
            properties.initStyle( StageStyle.DECORATED );
            properties.initModality( Modality.NONE );
            properties.initOwner( JavaFX.getInstance().платформа );
            properties.setTitle( LOGGER.text( "properties.value.title", legend.getText() ) );
        }
        properties.getController().setValue( (Value)legend.getUserData() );
        properties.show();
        event.consume();
    }
        
    private void applyProperties( Value value )
    {
        Parent parent = legend.getParent();
        if( parent instanceof Pane )
        {
            List<Node> children = ((Pane)parent).getChildren();
            children.set( children.indexOf( legend ), legend ); // unbind-bind @TimeLineController
        }
        else
        {
            LOGGER.getLogger().warning( "Some properties cannot be applied." );
        }
    }
    
    private  void onValueAddRemove( MapChangeListener.Change<? extends Object, ? extends Object> c )
    {
        if( c.wasRemoved() )
        {
            Object o = c.getValueRemoved();
            if( o instanceof Value )
            {
                Value value = (Value)o;
                legend.textProperty().unbind();
                legend.graphicProperty().unbind();
                legend.selectedProperty().setValue( Boolean.FALSE );
                value.enabledProperty().unbind();
                value.observableProperty().setValue( null );
                value.convertorProperty().setValue( null );
            }
        }
        if( c.wasAdded())
        {
            Object o = c.getValueAdded();
            if( o instanceof Value )
            {
                Value value = (Value)o;
                legend.textProperty().bind( value.titleProperty() );
                legend.graphicProperty().bind( value.graphicProperty() );
                value.enabledProperty().bind( legend.selectedProperty() );
                legend.selectedProperty().setValue( Boolean.TRUE );
            }
        }
    }
    
    private class TextWithName extends StringBinding
    {
        final String msg;
        
        TextWithName( String msg )
        {
            super.bind( legend.textProperty() );
            this.msg = msg;
        }

        @Override
        protected String computeValue()
        {
            return LOGGER.text( msg, legend.textProperty().getValue() );
        }
        
    }

}
