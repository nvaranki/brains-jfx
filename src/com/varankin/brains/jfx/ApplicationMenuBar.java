package com.varankin.brains.jfx;

import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.brains.jfx.MenuFactory.SubMenuAction;

import java.util.*;
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
        for( MenuFactory.MenuNode node : model( context ) ) 
            menus.add( context.menuFactory.createMenu( node ) );
    }

    /**
     * Создатель статической модели меню.
     * 
     * @param context контекст главного окна приложения.
     * @return модель меню.
     */
    static private MenuFactory.MenuNode[] model( ApplicationView.Context context )
    {
        Map<Locale.Category,Locale> специфика = context.jfx.контекст.специфика;
        ActionFactory actions = context.actions;

        return new MenuNode[]
        {
            new MenuNode( new SubMenuAction( ApplicationMenuBar.class, ".1", специфика ),
                    new MenuNode( new SubMenuAction( ApplicationMenuBar.class, ".1.1", специфика ),
                        new MenuNode( actions.getRepositorySql() ), 
                        new MenuNode( actions.getRepositoryXml() ), 
                        null,
                        new MenuNode( actions.getRepositoryInThePast() ) ),
                    new MenuNode( actions.getLoad() ),
                    new MenuNode( actions.getClean() ),
                    null,
                    new MenuNode( new SubMenuAction( ApplicationMenuBar.class, ".1.2", специфика ),
                        inlineMenuNodes( actions.getImportXml(), 1 ) ),
                    null,
                    new MenuNode( actions.getExit() ) ),

            new MenuNode( new SubMenuAction( ApplicationMenuBar.class, ".0", специфика ),
                    new MenuNode( actions.getStart() ),
                    new MenuNode( actions.getPause() ),
                    new MenuNode( actions.getStop() ) ),

            new MenuNode( new SubMenuAction( ApplicationMenuBar.class, ".5", специфика ),
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
    
}
