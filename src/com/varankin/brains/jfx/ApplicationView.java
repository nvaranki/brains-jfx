package com.varankin.brains.jfx;

import com.varankin.biz.appl.LoggingHandler;
import com.varankin.brains.Контекст;
import com.varankin.util.Текст;
import java.util.logging.Level;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Главная экранная форма приложения.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationView extends Scene
{

    ApplicationView( JavaFX jfx )
    {
        super( new ContentPane( new Context( jfx ) ) );
        Текст словарь = Текст.ПАКЕТЫ.словарь( ApplicationView.class, jfx.контекст.специфика );
        
        //TODO Позиционер
        jfx.платформа.setWidth( 800 );
        jfx.платформа.setHeight( 500 );
        jfx.платформа.setX( 100 );
        jfx.платформа.setY( 50 );
        
        jfx.платформа.setTitle( словарь.текст( "Title" ) );
    }
    
    /**
     * Контекст {@linkplain ApplicationView главной экранной формы приложения}.
     */
    static public final class Context
    {
        final JavaFX jfx;
//        public final ActionFactory actions;
        final MenuFactory menuFactory;
//        private Scene frame;

        private Context( JavaFX jfx )
        {
            this.jfx = jfx;
//            this.actions = new ActionFactory( this );
            this.menuFactory = new MenuFactory( jfx );
        }
        
//        private void setScene( Scene value )
//        {
//            frame = value;
//        }
//        
//        public final Scene getScene()
//        {
//            return frame;
//        }
    }

    static private class ContentPane extends BorderPane
    {
        ContentPane( Context context )
        {
            setTop( top( context ) );
            setCenter( center( context ) );
        }
        
        static Node top( Context context )
        {
            // меню приложения
            MenuBar mb = new ApplicationMenuBar( context );
            return mb;
        }
        
        static Node left( Context context )
        {
            // навигатор по модели данных рабочего проекта
            BrowserView навигатор = new BrowserView( context ); 
            TitledPane панель0 = new TitledPane( "Project Explorer", навигатор );

            // навигатор по архиву проектов
            ProjectCatalogView архиватор2 = new ProjectCatalogView( context ); 
            TitledPane панель2 = new TitledPane();
            панель2.textProperty().bind( архиватор2.titleProperty() );
            панель2.setContent( архиватор2 );

            // навигатор по архиву библиотек
            LibraryCatalogView архиватор3 = new LibraryCatalogView( context ); 
            TitledPane панель3 = new TitledPane();
            панель3.textProperty().bind( архиватор3.titleProperty() );
            панель3.setContent( архиватор3 );

            // панель обозревателей
            Accordion обозреватели = new Accordion();
            обозреватели.getPanes().addAll( панель0, панель2, панель3 );
            обозреватели.setExpandedPane( панель0 );
            return обозреватели;
        }
        
        static Node right( Context context )
        {
            // окно просмотра
            TabPane просмотр = new TabPane();
            ObservableList<Tab> tabs = просмотр.getTabs(); //TODO tab removed listener
            ObservableList<TitledSceneGraph> views = context.jfx.getViews().getValue();
            tabs.addListener( new TabCloseMonitor( views ) );
            views.addListener( new TabPaneContentManager( просмотр ) );
//            Pane sppw = new StackPane();
//            sppw.getChildren().add( просмотр );
            return просмотр;//sppw;
        }
        
        static Node center( Context context )
        {
            // три вложенных блока, регулируемых по размеру пользователем
//            Integer шр = Integer.valueOf( context.jfx.контекст.параметр( "frame.divider.size",  "4" ) ); //TODO 
            
            SplitPane блок1 = new SplitPane();
            блок1.setOrientation( Orientation.HORIZONTAL );
            блок1.setDividerPosition( 0, 0.3 );
            блок1.getItems().addAll( left( context ), right( context ) );
            
            SplitPane блок2 = new SplitPane();
            блок2.setOrientation( Orientation.VERTICAL );
            блок2.setDividerPosition( 0, 0.8 );
            блок2.getItems().addAll( блок1, bottom( context ) );
            
            return блок2;
        }
        
        static Node bottom( Context context )
        {
//            // консоль сообщений
            Контекст контекст = context.jfx.контекст;
            int limit = Integer.valueOf( контекст.параметр( "frame.console.rows.buffer", "5" ) );//"50"
            TextArea табло = new Табло( limit );
            LoggingHandler информатор = new LoggingHandler( new LoggingAgent( 
                    табло ) );//, RED, BLUE, null, null, null, null, null ) );
            информатор.setLevel( Level.INFO ); //TODO setup
            контекст.регистратор().addHandler( информатор );
            ScrollPane spoc = new ScrollPane();
            spoc.setContent( табло );
            spoc.setFitToHeight( true );
            spoc.setFitToWidth( true );

            return spoc;
        }
        
    }
    
}
