package com.varankin.brains.jfx;

import com.varankin.util.jfx.AbstractJfxAction;
import com.varankin.util.Текст;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * Конструктор меню приложения.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class MenuFactory 
{
    private final JavaFX jfx;

    MenuFactory( JavaFX jfx )
    {
        this.jfx = jfx;
    }
    
    /**
     * Создает всплывающее меню по модели.
     * 
     * @param модель окончательная модель меню.
     * @return всплывающее меню.
     */
    ContextMenu createContextMenu( MenuNode модель )
    {
        ContextMenu menu = new ContextMenu();
        модель.populate( menu.getItems(), jfx );
        return menu;
    }
    
    /**
     * Создает стандартное меню по модели.
     * 
     * @param модель окончательная модель меню.
     * @return стандартное меню.
     */
    Menu createMenu( MenuNode модель )
    {
        Menu menu = new Menu( модель.node.textProperty().get() );
        модель.populate( menu.getItems(), jfx );
        return menu;
    }

    /**
     * Узел модели иерархического меню.
     */
    static class MenuNode
    {
        private final AbstractJfxAction node;
        private final MenuNode childs[];

        MenuNode( AbstractJfxAction node, MenuNode... childs )
        {
            this.node = node;
            this.childs = childs;
        }

        private MenuItem toMenuItem( JavaFX jfx )
        {
            if( childs.length > 0 )
            {
                return toMenu( jfx );
            }
            else
            {
                MenuItem item = new MenuItem();
                item.setOnAction( node );
                item.setMnemonicParsing( false );
                item.setAccelerator( node.shortcut );
                item.textProperty().bind( node.textProperty() );
                item.graphicProperty().bind( node.iconProperty() );
                item.disableProperty().bind( node.disableProperty() );
                return item; 
            }
        }

        private Menu toMenu( JavaFX jfx )
        {
            Menu menu = new Menu( node.textProperty().get() );
            populate( menu.getItems(), jfx );
            return menu;
        }
        
        private void populate( Collection<MenuItem> items, JavaFX jfx )
        {
            for( MenuNode child : childs )
                if( child != null )
                    items.add( child.toMenuItem( jfx ) );
                else
                    items.add( new SeparatorMenuItem() );
        }

    }

    /**
     * Вспомогательное {@linkplain Action действие} для элемента раскрытия подменю.
     */
    static class SubMenuAction extends AbstractJfxAction
    {
        SubMenuAction( Текст словарь )
        {
            super( словарь );
        }

        SubMenuAction( Class класс, String суффикс, Map<Locale.Category,Locale> специфика )
        {
            this( Текст.ПАКЕТЫ.словарь( класс.getPackage(), класс.getSimpleName() + суффикс, специфика ) );
        }

        @Override
        public void handle( ActionEvent _ )
        {
        }
    }

}
