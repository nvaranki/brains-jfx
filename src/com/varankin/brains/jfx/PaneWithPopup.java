package com.varankin.brains.jfx;

import javafx.scene.control.ContextMenu;
import javafx.scene.layout.Pane;

/**
 * Класс для исправления проблемы FXML с контекстным меню в панели.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public class PaneWithPopup extends Pane 
{
    private ContextMenu contextMenu;

    public final void setContextMenu( ContextMenu menu ) 
    {
        contextMenu = menu;
    }

    public final ContextMenu getContextMenu() 
    {
        return contextMenu;
    }

}
