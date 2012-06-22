package com.varankin.brains.jfx;

import com.varankin.util.Текст;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import javafx.beans.value.ObservableValue;

import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * Коструктор меню приложения.
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
        Menu menu = new Menu( модель.node.getText() );
        модель.populate( menu.getItems(), jfx );
        return menu;
    }

    /**
     * Узел модели иерархического меню.
     */
    static class MenuNode
    {
        private final Action node;
        private final MenuNode childs[];

        MenuNode( Action node, MenuNode... childs )
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
                item.setMnemonicParsing( false );
                item.setText( node.getText() );
                item.setAccelerator( node.shortcut );
                item.setGraphic( node.icon );
                item.setOnAction( node );
                item.disableProperty().bind( node.disableProperty() );
                jfx.getMenuItems( node ).add( item );
                return item; 
            }
        }

        private Menu toMenu( JavaFX jfx )
        {
            Menu menu = new Menu( node.getText() );
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
    static class SubMenuAction extends Action
    {
        SubMenuAction( Текст словарь )
        {
            super( null, словарь );
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
