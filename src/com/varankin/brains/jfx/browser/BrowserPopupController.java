package com.varankin.brains.jfx.browser;

import static com.varankin.brains.jfx.JavaFX.icon;
import java.util.ResourceBundle;
import java.util.logging.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Builder;

/**
 * FXML-контроллер контекстного меню навигатора по проектам. 
 * 
 * @author &copy; 2015 Николай Варанкин
 */
public class BrowserPopupController implements Builder<ContextMenu>
{
    private static final Logger LOGGER = Logger.getLogger(
            BrowserPopupController.class.getName(),
            BrowserPopupController.class.getPackage().getName() + ".text" );
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getResourceBundle();

    private final BooleanProperty disableStart;
    private final BooleanProperty disablePause;
    private final BooleanProperty disableStop;
    private final BooleanProperty disableRemove;
    private final BooleanProperty disableProperties;
    
    private ActionProcessor processor;

    public BrowserPopupController() 
    {
        disableStart      = new SimpleBooleanProperty( this, "disableStart" );
        disablePause      = new SimpleBooleanProperty( this, "disablePause" );
        disableStop       = new SimpleBooleanProperty( this, "disableStop" );
        disableRemove     = new SimpleBooleanProperty( this, "disableRemove" );
        disableProperties = new SimpleBooleanProperty( this, "disableProperties" );
    }
    
    /**
     * Создает панель навигатора. 
     * Применяется в конфигурации без FXML.
     * 
     * @return панель навигатора. 
     */
    @Override
    public ContextMenu build()
    {
        
        MenuItem menuStart = new MenuItem(
                text( "browser.action.start" ), 
                icon( "icons16x16/start.png" ) );
        menuStart.setOnAction( this::onActionStart );
        menuStart.disableProperty().bind( disableStart );
        
        MenuItem menuPause = new MenuItem(
                text( "browser.action.pause" ), 
                icon( "icons16x16/pause.png" ) );
        menuPause.setOnAction( this::onActionPause );
        menuPause.disableProperty().bind( disablePause );
        
        MenuItem menuStop = new MenuItem(
                text( "browser.action.stop" ), 
                icon( "icons16x16/stop.png" ) );
        menuStop.setOnAction( this::onActionStop );
        menuStop.disableProperty().bind( disableStop );
        
        MenuItem menuRemove = new MenuItem(
                text( "browser.action.remove" ), 
                icon( "icons16x16/remove.png" ) );
        menuRemove.setOnAction( this::onActionRemove );
        menuRemove.disableProperty().bind( disableRemove );
        
        MenuItem menuProperties = new MenuItem(
                text( "browser.action.properties" ), 
                icon( "icons16x16/properties.png" ) );
        menuProperties.setOnAction( this::onActionProperties );
        menuProperties.disableProperty().bind( disableProperties );
        
        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll
        (
                menuStart,
                menuPause,
                menuStop,
                new SeparatorMenuItem(),
                menuRemove,
                new SeparatorMenuItem(),
                menuProperties
        );
        return menu;
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
        this.processor = processor;
        
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
