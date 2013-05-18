package com.varankin.brains.jfx;

import com.varankin.biz.action.СогласованноеДействие;
import com.varankin.brains.appl.HistoricProvider;
import com.varankin.brains.appl.Импортировать;
import com.varankin.brains.appl.КаталогДействий;
import static com.varankin.brains.appl.КаталогДействий.Индекс.*;
import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.brains.jfx.MenuFactory.SubMenuAction;
import com.varankin.io.container.Provider;
import com.varankin.util.HistoryList;
import java.io.InputStream;
import java.util.*;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;

/**
 * Построитель главного меню приложения.
 *
 * @author &copy; 2013 Николай Варанкин
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
        
        ListProperty<Provider<InputStream>> providersXml 
                = new SimpleListProperty<>( FXCollections.<Provider<InputStream>>observableArrayList() );
        providersXml.add( null ); // пустышка; видимый индекс истории начинается с 1
        HistoryList<Provider<InputStream>> historyXml 
                = new HistoryList<>( providersXml, ApplicationActionImportXml.class );

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
                    new MenuNode( new SubMenuAction( ApplicationMenuBar.class, ".1.2", специфика ),
                        new MenuNode( new ApplicationActionImportXml( 
                            (СогласованноеДействие)jfx.контекст.действие( ИмпортироватьXML ), 
                            new Импортировать.Контекст( jfx.контекст, null ),
                            jfx, new XmlFileSelector( jfx ), historyXml ) ),
                        new MenuNode( new ApplicationActionImportXml( 
                            (СогласованноеДействие)jfx.контекст.действие( ИмпортироватьXML ), 
                            new Импортировать.Контекст( jfx.контекст, null ),
                            jfx, new XmlUrlSelector( jfx ), historyXml ) ),
                        null,
                        new MenuNode( повторИмпортаXml( ИмпортироватьXML, jfx, historyXml, 1, providersXml ) ), 
                        new MenuNode( повторИмпортаXml( ИмпортироватьXML, jfx, historyXml, 2, providersXml ) ), 
                        new MenuNode( повторИмпортаXml( ИмпортироватьXML, jfx, historyXml, 3, providersXml ) ) ),
                    null,
                    new MenuNode( new ApplicationActionExit( jfx ) ) ),

            new MenuNode( new SubMenuAction( ApplicationMenuBar.class, ".0", специфика ),
                    new MenuNode( действие( Старт, jfx, "ApplicationActionStart" ) ),
                    new MenuNode( действие( Пауза, jfx, "ApplicationActionPause" ) ),
                    new MenuNode( действие( Стоп,  jfx, "ApplicationActionStop"  ) ) ),

            new MenuNode( new SubMenuAction( ApplicationMenuBar.class, ".5", специфика ),
//                    new MenuNode( actions.getAbout() ),
//                    new MenuNode( new SubMenu( ".5.1", специфика ),
//                        new MenuNode( actions.getAbout() ) ),
//                    null,
                    new MenuNode( new ApplicationActionAbout( jfx ) ) )
        };
    }
    
    static private AbstractJfxAction повторИмпортаXml( КаталогДействий.Индекс индекс, JavaFX jfx, 
            HistoryList<Provider<InputStream>> история, int позиция, 
            ListProperty<Provider<InputStream>> поставщики ) 
    {
        ApplicationActionHistory<Импортировать.Контекст,Provider<InputStream>> действие = 
            new ApplicationActionHistory<>(
                (СогласованноеДействие)jfx.контекст.действие( индекс ), 
                new Импортировать.Контекст( jfx.контекст, new HistoricProvider<>( история, позиция ) ),
                jfx, история, позиция );
        поставщики.addListener( действие );
        return действие;
    }
    
    static private AbstractJfxAction действие( КаталогДействий.Индекс индекс, JavaFX jfx, String префикс )
    {
        return new CoordinatedAction<>( jfx.контекст.действие( индекс ), jfx.контекст, jfx, префикс );
    }

}
