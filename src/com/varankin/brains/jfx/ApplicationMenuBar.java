package com.varankin.brains.jfx;

import com.varankin.biz.action.СогласованноеДействие;
import com.varankin.brains.appl.HistoricProvider;
import com.varankin.brains.appl.Импортировать;
import com.varankin.util.HistoryList;
import com.varankin.brains.appl.КаталогДействий;
import static com.varankin.brains.appl.КаталогДействий.Индекс.*;
import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.brains.jfx.MenuFactory.SubMenuAction;
import com.varankin.brains.Контекст;
import com.varankin.io.container.Provider;
import com.varankin.util.jfx.AbstractJfxAction;
import java.io.InputStream;

import java.util.*;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ListPropertyBase;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

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
        JavaFX jfx = context.jfx;
        Map<Locale.Category,Locale> специфика = jfx.контекст.специфика;
        
        ListProperty<Provider<InputStream>> providersXml 
                = new SimpleListProperty<>( FXCollections.<Provider<InputStream>>observableArrayList() );
        providersXml.add( null ); // --> ApplicationActionImportXml
        HistoryList<Provider<InputStream>> historyXml 
                = new HistoryList<>( providersXml, ApplicationActionImportXml.class );

        return new MenuNode[]
        {
            new MenuNode( new SubMenuAction( ApplicationMenuBar.class, ".1", специфика ),
                    new MenuNode( new SubMenuAction( ApplicationMenuBar.class, ".1.1", специфика ),
                        new MenuNode( new ApplicationActionRepositorySql( context ) ), 
                        new MenuNode( new ApplicationActionRepositoryXml( context ) ), 
                        null,
                        new MenuNode( new ApplicationActionRepositoryInThePast( context ) ) ),
                    new MenuNode( действие( Загрузить, jfx, "ApplicationActionLoad" ) ),
                    new MenuNode( действие( Очистить,  jfx, "ApplicationActionClean" ) ),
                    null,
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
                    new MenuNode( new ApplicationActionExit( context ) ) ),

            new MenuNode( new SubMenuAction( ApplicationMenuBar.class, ".0", специфика ),
                    new MenuNode( действие( Старт, jfx, "ApplicationActionStart" ) ),
                    new MenuNode( действие( Пауза, jfx, "ApplicationActionPause" ) ),
                    new MenuNode( действие( Стоп,  jfx, "ApplicationActionStop"  ) ) ),

            new MenuNode( new SubMenuAction( ApplicationMenuBar.class, ".5", специфика ),
//                    new MenuNode( actions.getAbout() ),
//                    new MenuNode( new SubMenu( ".5.1", специфика ),
//                        new MenuNode( actions.getAbout() ) ),
//                    null,
                    new MenuNode( new ApplicationActionAbout( context ) ) )
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
    
    static private MenuNode[] inlineMenuNodes( AbstractJfxAction[] actions, int индексРазделителя )
    {
        List<MenuNode> nodes = new ArrayList<>( actions.length + 1 );
        for( AbstractJfxAction a : actions )
            nodes.add( new MenuNode( a ) );
        nodes.add( индексРазделителя, null );
        return nodes.toArray( new MenuNode[nodes.size()] );
    }
    
}
