package com.varankin.brains.jfx;

import com.varankin.brains.appl.КаталогДействий;

import static com.varankin.brains.appl.КаталогДействий.Индекс.*;

import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.brains.jfx.MenuFactory.SubMenuAction;
import com.varankin.brains.jfx.archive.MenuArchiveController;
import java.util.*;
import javafx.scene.control.*;

/**
 * Построитель главного меню приложения.
 *
 * @author &copy; 2016 Николай Варанкин
 */
class ApplicationMenuBar 
{
    static MenuBar create( JavaFX jfx )
    {
        MenuBar menuBar = new MenuBar();
        List<Menu> menus = menuBar.getMenus();
        for( MenuFactory.MenuNode node : model( jfx ) ) 
            menus.add( MenuFactory.createMenu( node ) );
        return menuBar;
    }

    /**
     * Создатель статической модели меню.
     * 
     * @param context контекст главного окна приложения.
     * @return модель меню.
     */
    static private MenuFactory.MenuNode[] model( JavaFX jfx )
    {
        Map<Locale.Category,Locale> специфика = jfx.контекст.специфика;
        

        return new MenuNode[]
        {
            new MenuNode( new SubMenuAction( ApplicationMenuBar.class, ".1", специфика ),
//                    new MenuNode( new SubMenuAction( ApplicationMenuBar.class, ".1.1", специфика ),
//                        new MenuNode( new ApplicationActionRepositorySql( context ) ), 
//                        new MenuNode( new ApplicationActionRepositoryXml( context ) ), 
//                        null,
//                        new MenuNode( new ApplicationActionRepositoryInThePast( context ) ) ),
//                    new MenuNode( действие( Загрузить, jfx, "ApplicationActionLoad" ) ),
//                    new MenuNode( действие( Очистить,  jfx, "ApplicationActionClean" ) ),
//                    null,
                    new MenuNode( new MenuArchiveController().build() ),
                    null,
                    new MenuNode( new ApplicationActionExit( jfx ) ) ),

            new MenuNode( new SubMenuAction( ApplicationMenuBar.class, ".0", специфика ),
                    new MenuNode( действие( Старт, jfx, "ApplicationActionStart" ) ),
                    new MenuNode( действие( Пауза, jfx, "ApplicationActionPause" ) ),
                    new MenuNode( действие( Стоп,  jfx, "ApplicationActionStop"  ) ) ),

            new MenuNode( new SubMenuAction( ApplicationMenuBar.class, ".3", специфика ),
                    new MenuNode( new ApplicationActionAnalyser( jfx ) ),
                    new MenuNode( new ApplicationActionEditor( jfx ) ) ),

            new MenuNode( new SubMenuAction( ApplicationMenuBar.class, ".5", специфика ),
//                    new MenuNode( actions.getAbout() ),
//                    new MenuNode( new SubMenu( ".5.1", специфика ),
//                        new MenuNode( actions.getAbout() ) ),
//                    null,
                    new MenuNode( new ApplicationActionAbout( jfx ) ) )
        };
    }
    
    static private AbstractJfxAction действие( КаталогДействий.Индекс индекс, JavaFX jfx, String префикс )
    {
        return new CoordinatedAction<>( jfx.контекст.действие( индекс ), jfx.контекст, jfx, префикс );
    }

}
