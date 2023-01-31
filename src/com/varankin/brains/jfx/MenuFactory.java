package com.varankin.brains.jfx;

import com.varankin.util.Текст;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;

/**
 * Конструктор меню приложения.
 *
 * @author &copy; 2013 Николай Варанкин
 */
public class MenuFactory 
{
    /**
     * Создает всплывающее меню по модели.
     * 
     * @param модель окончательная модель меню.
     * @return всплывающее меню.
     */
    public static ContextMenu createContextMenu( MenuNode модель )
    {
        ContextMenu menu = new ContextMenu();
        модель.populate( menu.getItems() );
        return menu;
    }
    
    /**
     * Создает стандартное меню по модели.
     * 
     * @param модель окончательная модель меню.
     * @return стандартное меню.
     */
    public static Menu createMenu( MenuNode модель )
    {
        Menu menu = new Menu( модель.node.textProperty().getValue() );
        модель.populate( menu.getItems() );
        return menu;
    }

    /**
     * Узел модели иерархического меню.
     */
    public static class MenuNode
    {
        private final AbstractJfxAction node;
        private final MenuNode childs[];
        private MenuItem menuItem;

        @Deprecated
        public MenuNode( MenuItem menuItem, EventHandler<ActionEvent> handler )
        {
            this( (AbstractJfxAction)null );
            this.menuItem = menuItem;
            this.menuItem.setOnAction( handler );
        }
        
        public MenuNode( AbstractJfxAction node, MenuNode... childs )
        {
            this.node = node;
            this.childs = childs;
        }

        private MenuItem toMenuItem()
        {
            if( menuItem != null )
            {
                return menuItem;
            }
            else if( childs.length > 0 )
            {
                return toMenu();
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

        private Menu toMenu()
        {
            Menu menu = new Menu( node.textProperty().getValue() );
            populate( menu.getItems() );
            return menu;
        }
        
        private void populate( Collection<MenuItem> items )
        {
            for( MenuNode child : childs )
                if( child != null )
                    items.add( child.toMenuItem() );
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
        public void handle( ActionEvent __ )
        {
        }
    }

}
