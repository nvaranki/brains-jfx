package com.varankin.brains.jfx.browser;

import static com.varankin.brains.jfx.JavaFX.icon;
import java.util.ResourceBundle;
import java.util.logging.*;
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
public final class BrowserToolBarController 
        extends AbstractActionController 
        implements Builder<ToolBar>
{
    private static final Logger LOGGER = Logger.getLogger(
            BrowserToolBarController.class.getName(),
            BrowserToolBarController.class.getPackage().getName() + ".text" );
    private static final String RESOURCE_CSS  = "/fxml/browser/BrowserToolBar.css";
    private static final String CSS_CLASS = "browser-toolbar";

    public static final String RESOURCE_FXML  = "/fxml/browser/BrowserToolBar.fxml";
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getResourceBundle();

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
        buttonStart.disableProperty().bind( disableStartProperty() );
        
        Button buttonPause = new Button();
        buttonPause.setTooltip( new Tooltip( text( "browser.action.pause" ) ) );
        buttonPause.setGraphic( icon( "icons16x16/pause.png" ) );
        buttonPause.setOnAction( this::onActionPause );
        buttonPause.disableProperty().bind( disablePauseProperty() );
        
        Button buttonStop = new Button();
        buttonStop.setTooltip( new Tooltip( text( "browser.action.stop" ) ) );
        buttonStop.setGraphic( icon( "icons16x16/stop.png" ) );
        buttonStop.setOnAction( this::onActionStop );
        buttonStop.disableProperty().bind( disableStopProperty() );
        
        Button buttonRemove = new Button();
        buttonRemove.setTooltip( new Tooltip( text( "browser.action.remove" ) ) );
        buttonRemove.setGraphic( icon( "icons16x16/remove.png" ) );
        buttonRemove.setOnAction( this::onActionRemove );
        buttonRemove.disableProperty().bind( disableRemoveProperty() );
        
        Button buttonProperties = new Button();
        buttonProperties.setTooltip( new Tooltip( text( "browser.action.properties" ) ) );
        buttonProperties.setGraphic( icon( "icons16x16/properties.png" ) );
        buttonProperties.setOnAction( this::onActionProperties );
        buttonProperties.disableProperty().bind( disablePropertiesProperty() );
        
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

        return toolbar;
    }
    
}
