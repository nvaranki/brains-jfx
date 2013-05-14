package com.varankin.brains.jfx;

import com.varankin.biz.appl.LoggingHandler;
import com.varankin.brains.Контекст;
import com.varankin.io.container.Provider;
import com.varankin.util.Текст;
import java.io.File;
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
    static final class Context
    {
        final JavaFX jfx;
        final Provider<File> exporter;

        private Context( JavaFX jfx )
        {
            this.jfx = jfx;
            this.exporter = new ExportFileSelector( jfx );
        }
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
            return ApplicationMenuBar.create( context );
        }
        
        static Node left( Context context )
        {
            int spacing = 3;
            ToolBar toolbar;

            // навигатор по модели данных рабочего проекта
            toolbar = new ToolBar();
            toolbar.setOrientation( Orientation.VERTICAL );
            BrowserView навигатор = new BrowserView( context, toolbar.getItems() ); 
            TitledPane панель0 = new TitledPane();
            панель0.textProperty().bind( навигатор.titleProperty() );
            if( Boolean.valueOf( context.jfx.контекст.параметр( "BrowserView.toolbar", "true" ) ) )
            {
                Pane pane = new HBox(spacing);
                HBox.setHgrow( навигатор, Priority.ALWAYS );
                pane.getChildren().addAll( toolbar, навигатор );
                панель0.setContent( pane );
            }
            else
            {
                панель0.setContent( навигатор );
            }

            // навигатор по архиву проектов
            toolbar = new ToolBar();
            toolbar.setOrientation( Orientation.VERTICAL );
            ProjectCatalogView архиватор2 = new ProjectCatalogView( context, toolbar.getItems() ); 
            TitledPane панель2 = new TitledPane();
            панель2.textProperty().bind( архиватор2.titleProperty() );
            if( Boolean.valueOf( context.jfx.контекст.параметр( "ProjectCatalogView.toolbar", "true" ) ) )
            {
                Pane pane = new HBox(spacing);
                HBox.setHgrow( архиватор2, Priority.ALWAYS );
                pane.getChildren().addAll( toolbar, архиватор2 );
                панель2.setContent( pane );
            }
            else
            {
                панель2.setContent( архиватор2 );
            }

            // навигатор по архиву библиотек
            toolbar = new ToolBar();
            toolbar.setOrientation( Orientation.VERTICAL );
            LibraryCatalogView архиватор3 = new LibraryCatalogView( context, toolbar.getItems() ); 
            TitledPane панель3 = new TitledPane();
            панель3.textProperty().bind( архиватор3.titleProperty() );
            if( Boolean.valueOf( context.jfx.контекст.параметр( "LibraryCatalogView.toolbar", "true" ) ) )
            {
                Pane pane = new HBox(spacing);
                HBox.setHgrow( архиватор3, Priority.ALWAYS );
                pane.getChildren().addAll( toolbar, архиватор3 );
                панель3.setContent( pane );
            }
            else
            {
                панель3.setContent( архиватор3 );
            }

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
