package com.varankin.brains.jfx.browser;

import static com.varankin.brains.jfx.JavaFX.icon;
import java.util.ResourceBundle;
import java.util.logging.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.util.Builder;

/**
 * FXML-контроллер набора инструментов навигатора по проектам.
 * 
 * @author &copy; 2015 Николай Варанкин
 */
public class BrowserToolBarController implements Builder<ToolBar>
{
    private static final Logger LOGGER = Logger.getLogger(
            BrowserToolBarController.class.getName(),
            BrowserToolBarController.class.getPackage().getName() + ".text" );
    private static final String RESOURCE_CSS  = "/fxml/browser/BrowserToolBar.css";
    private static final String CSS_CLASS = "browser-toolbar";

    public static final String RESOURCE_FXML  = "/fxml/browser/BrowserToolBar.fxml";
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getResourceBundle();

    private final BooleanProperty disableStart;
    private final BooleanProperty disablePause;
    private final BooleanProperty disableStop;
    private final BooleanProperty disableRemove;
    private final BooleanProperty disableProperties;

    private ActionProcessor processor;

    public BrowserToolBarController() 
    {
        disableStart      = new SimpleBooleanProperty( this, "disableStart" );
        disablePause      = new SimpleBooleanProperty( this, "disablePause" );
        disableStop       = new SimpleBooleanProperty( this, "disableStop" );
        disableRemove     = new SimpleBooleanProperty( this, "disableRemove" );
        disableProperties = new SimpleBooleanProperty( this, "disableProperties" );
    }
    
    /**
     * Создает набор инструментов навигатора по проектам.
     * Применяется в конфигурации без FXML.
     * 
     * @return набор инструментов. 
     */
    @Override
    public ToolBar build()
    {
        Button buttonStart = new Button();
        buttonStart.setTooltip( new Tooltip( text( "browser.action.start" ) ) );
        buttonStart.setGraphic( icon( "icons16x16/start.png" ) );
        buttonStart.setOnAction( this::onActionStart );
        buttonStart.disableProperty().bind( disableStart );
        
        Button buttonPause = new Button();
        buttonPause.setTooltip( new Tooltip( text( "browser.action.pause" ) ) );
        buttonPause.setGraphic( icon( "icons16x16/pause.png" ) );
        buttonPause.setOnAction( this::onActionPause );
        buttonPause.disableProperty().bind( disablePause );
        
        Button buttonStop = new Button();
        buttonStop.setTooltip( new Tooltip( text( "browser.action.stop" ) ) );
        buttonStop.setGraphic( icon( "icons16x16/stop.png" ) );
        buttonStop.setOnAction( this::onActionStop );
        buttonStop.disableProperty().bind( disableStop );
        
        Button buttonRemove = new Button();
        buttonRemove.setTooltip( new Tooltip( text( "browser.action.remove" ) ) );
        buttonRemove.setGraphic( icon( "icons16x16/remove.png" ) );
        buttonRemove.setOnAction( this::onActionRemove );
        buttonRemove.disableProperty().bind( disableRemove );
        
        Button buttonProperties = new Button();
        buttonProperties.setTooltip( new Tooltip( text( "browser.action.properties" ) ) );
        buttonProperties.setGraphic( icon( "icons16x16/properties.png" ) );
        buttonProperties.setOnAction( this::onActionProperties );
        buttonProperties.disableProperty().bind( disableProperties );
        
        
        ToolBar toolbar = new ToolBar();
        toolbar.setOrientation( Orientation.VERTICAL );
        toolbar.getItems().addAll
        (
                buttonStart,
                buttonPause,
                buttonStop,
                new Separator( Orientation.HORIZONTAL ),
                buttonRemove,
                new Separator( Orientation.HORIZONTAL ),
                buttonProperties
        );
        toolbar.getStyleClass().add( CSS_CLASS );
        toolbar.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return toolbar;
    }
    
    @FXML
    protected void initialize()
    {
    }
    
    @FXML
    private void onActionStart( ActionEvent event )
    {
        processor.onActionStart( event );
        event.consume();
    }
    
    @FXML
    private void onActionPause( ActionEvent event )
    {
        processor.onActionPause( event );
        event.consume();
    }
    
    @FXML
    private void onActionStop( ActionEvent event )
    {
        processor.onActionStop( event );
        event.consume();
    }
    
    @FXML
    private void onActionRemove( ActionEvent event )
    {
        processor.onActionRemove( event );
        event.consume();
    }
    
    @FXML
    private void onActionProperties( ActionEvent event )
    {
        processor.onActionProperties( event );
        event.consume();
    }
    
    public BooleanProperty disableStartProperty()      { return disableStart; }
    public BooleanProperty disablePauseProperty()      { return disablePause; }
    public BooleanProperty disableStopProperty()       { return disableStop; }
    public BooleanProperty disableRemoveProperty()     { return disableRemove; }
    public BooleanProperty disablePropertiesProperty() { return disableProperties; }

    public boolean getDisableStart()      { return disableStart.get(); }
    public boolean getDisablePause()      { return disablePause.get(); }
    public boolean getDisableStop()       { return disableStop.get(); }
    public boolean getDisableRemove()     { return disableRemove.get(); }
    public boolean getDisableProperties() { return disableProperties.get(); }

    void setProcessor( ActionProcessor processor ) 
    {
        this.processor = processor; // helps for onActionXxx()
        
        disableStart     .bind( processor.disableStartProperty() );
        disablePause     .bind( processor.disablePauseProperty() );
        disableStop      .bind( processor.disableStopProperty() );
        disableRemove    .bind( processor.disableRemoveProperty() );
        disableProperties.bind( processor.disablePropertiesProperty() );
    }
    
    private static String text( String ключ )
    {
        return RESOURCE_BUNDLE.getString( ключ );
    }

}
