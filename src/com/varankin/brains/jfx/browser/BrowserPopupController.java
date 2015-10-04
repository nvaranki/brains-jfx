package com.varankin.brains.jfx.browser;

import java.util.ResourceBundle;
import java.util.logging.*;
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

    public BrowserPopupController() 
    {
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
        
        ContextMenu menu = new ContextMenu();
//        menu.getItems().addAll
//        (
//                menuLoad,
//                new SeparatorMenuItem(),
//                menuPreview,
//                menuEdit,
//                menuRemove,
//                menuNew,
//                menuProperties,
//                new SeparatorMenuItem(),
//                menuImportFile,
//                menuImportNet,
//                menuExportXml,
//                menuExportPic
//        );
        return menu;
    }

    void setProcessor( ActionProcessor processor ) 
    {
    }
    
}
