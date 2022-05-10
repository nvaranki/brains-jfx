package com.varankin.brains.jfx.browser;

import static com.varankin.brains.jfx.JavaFX.icon;
import java.util.ResourceBundle;
import java.util.logging.*;
import javafx.scene.control.*;
import javafx.util.Builder;

/**
 * FXML-контроллер контекстного меню навигатора по проектам. 
 * 
 * @author &copy; 2015 Николай Варанкин
 */
public final class BrowserPopupController 
        extends AbstractActionController 
        implements Builder<ContextMenu>
{
    private static final Logger LOGGER = Logger.getLogger(
            BrowserPopupController.class.getName(),
            BrowserPopupController.class.getPackage().getName() + ".text" );
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getResourceBundle();

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
        menuStart.disableProperty().bind( disableStartProperty() );
        
        MenuItem menuPause = new MenuItem(
                text( "browser.action.pause" ), 
                icon( "icons16x16/pause.png" ) );
        menuPause.setOnAction( this::onActionPause );
        menuPause.disableProperty().bind( disablePauseProperty() );
        
        MenuItem menuStop = new MenuItem(
                text( "browser.action.stop" ), 
                icon( "icons16x16/stop.png" ) );
        menuStop.setOnAction( this::onActionStop );
        menuStop.disableProperty().bind( disableStopProperty() );
        
        MenuItem menuRemove = new MenuItem(
                text( "browser.action.remove" ), 
                icon( "icons16x16/remove.png" ) );
        menuRemove.setOnAction( this::onActionRemove );
        menuRemove.disableProperty().bind( disableRemoveProperty() );
        
        MenuItem menuProperties = new MenuItem(
                text( "browser.action.properties" ), 
                icon( "icons16x16/properties.png" ) );
        menuProperties.setOnAction( this::onActionProperties );
        menuProperties.disableProperty().bind( disablePropertiesProperty() );
        
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

}
