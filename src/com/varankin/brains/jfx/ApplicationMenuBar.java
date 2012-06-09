package com.varankin.brains.jfx;

import com.varankin.util.Текст;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

/**
 * Главное меню приложения.
 *
 * @author &copy; 2012 Николай Варанкин
 */
public class ApplicationMenuBar extends MenuBar
{
    ApplicationMenuBar( ApplicationView.Context context )
    {
        List<Menu> menus = getMenus();
        for( MenuNode node : model( context ) ) 
            menus.add( node.toMenu( context.jfx ) );
    }

    /**
     * Создатель статической модели меню.
     * 
     * @param context контекст главного окна приложения.
     * @return модель меню.
     */
    static private MenuNode[] model( ApplicationView.Context context )
    {
        Map<Locale.Category,Locale> специфика = context.jfx.контекст.специфика;
        ActionFactory actions = context.actions;

        return new MenuNode[]
        {
            new MenuNode( new SubMenu( ".1", специфика ),
                    new MenuNode( 
                        new SubMenu( ".1.1", специфика ),
                        new MenuNode( actions.getRepositorySql() ), 
                        new MenuNode( actions.getRepositoryXml() ), 
                        null,
                        new MenuNode( actions.getRepositoryInThePast() ) ),
                    new MenuNode( actions.getLoad() ),
                    new MenuNode( actions.getClean() ),
                    null,
                    new MenuNode( 
                        new SubMenu( ".1.2", специфика ),
                        inlineMenuNodes( actions.getImportXml(), 1 ) ),
                    null,
                    new MenuNode( actions.getExit() ) ),

            new MenuNode( new SubMenu( ".0", специфика ),
                    new MenuNode( actions.getStart() ),
                    new MenuNode( actions.getPause() ),
                    new MenuNode( actions.getStop() ) ),

            new MenuNode( new SubMenu( ".5", специфика ),
//                    new MenuNode( actions.getAbout() ),
//                    new MenuNode( new SubMenu( ".5.1", специфика ),
//                        new MenuNode( actions.getAbout() ) ),
//                    null,
                    new MenuNode( actions.getAbout() ) )
        };
        

    }
    
    static private MenuNode[] inlineMenuNodes( Action[] actions, int индексРазделителя )
    {
        List<MenuNode> nodes = new ArrayList<>( actions.length + 1 );
        for( Action a : actions )
            nodes.add( new MenuNode( a ) );
        nodes.add( индексРазделителя, null );
        return nodes.toArray( new MenuNode[nodes.size()] );
    }

    /**
     * Вспомогательное {@linkplain Action действие} для элемента раскрытия подменю.
     */
    static private class SubMenu extends Action
    {
        SubMenu( String суффикс, Map<Locale.Category,Locale> специфика )
        {
            super( null, Текст.ПАКЕТЫ.словарь( ApplicationMenuBar.class.getPackage(),
                    ApplicationMenuBar.class.getSimpleName() + суффикс, специфика ) );
        }

        @Override
        public void handle( ActionEvent _ )
        {
        }
    }

    /**
     * Узел модели иерархического меню.
     */
    static private class MenuNode
    {
        final Action node;
        final MenuNode childs[];

        MenuNode( Action node, MenuNode... childs )
        {
            this.node = node;
            this.childs = childs;
        }

        MenuItem toMenuItem( JavaFX jfx )
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
                item.setDisable( !node.isEnabled() );
                jfx.getMenuItems( node ).add( item );
                return item; 
            }
        }

        Menu toMenu( JavaFX jfx )
        {
            Menu menu = new Menu( node.getText() );
            List<MenuItem> items = menu.getItems();
            for( MenuNode child : childs )
                if( child != null )
                    items.add( child.toMenuItem( jfx ) );
                else
                    items.add( new SeparatorMenuItem() );
            return menu;
        }

    }

}
