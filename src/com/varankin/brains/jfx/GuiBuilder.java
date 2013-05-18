package com.varankin.brains.jfx;

import com.varankin.biz.appl.LoggingHandler;
import com.varankin.brains.Контекст;
import com.varankin.util.Текст;
import java.util.logging.*;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Построитель главной экранной формы приложения.
 *
 * @author &copy; 2013 Николай Варанкин
 */
class GuiBuilder 
{
    private final JavaFX JFX;
    private final Текст словарь;
    
    GuiBuilder( JavaFX jfx )
    {
        JFX = jfx;
        словарь = Текст.ПАКЕТЫ.словарь( GuiBuilder.class, JFX.контекст.специфика );
    }
    
    void createView( Stage stage )
    {
        stage.setTitle( словарь.текст( "Title" ) );

        int w = Integer.valueOf( JFX.контекст.параметр( "frame.width",  "800" ));
        int h = Integer.valueOf( JFX.контекст.параметр( "frame.height", "600" ));
        stage.setWidth( w );
        stage.setHeight( h );
        stage.setX( 100 );
        stage.setY( 50 );
        //Позиционер.настроить( stage );

        BorderPane pane = new BorderPane();
        pane.setTop( ApplicationMenuBar.create( JFX ) );
        pane.setCenter( createCenterBlock() );
        stage.setScene( new Scene( pane ) );
    }

    private Node createLeftBlock()
    {
        int spacing = 3;
        TitledPane панель0 = навигаторПоРабочемуПроекту( spacing );
        TitledPane панель2 = навигаторПоАрхивуПроектов( spacing );
        TitledPane панель3 = навигаторПоАрхивуБиблиотек( spacing );
        // панель обозревателей
        Accordion обозреватели = new Accordion();
        обозреватели.getPanes().addAll( панель0, панель2, панель3 );
        обозреватели.setExpandedPane( панель0 );
        return обозреватели;
    }

    private Node createRightBlock()
    {
        // окно просмотра
        TabPane просмотр = new TabPane();
        ObservableList<Tab> tabs = просмотр.getTabs(); //TODO tab removed listener
        //TODO tab removed listener
        ObservableList<TitledSceneGraph> views = JFX.getViews().getValue();
        tabs.addListener( new TabCloseMonitor( views ) );
        views.addListener( new TabPaneContentManager( просмотр ) );
        //            Pane sppw = new StackPane();
        //            sppw.getChildren().add( просмотр );
        return просмотр; //sppw;
        //sppw;
    }

    private Node createCenterBlock()
    {
        // три вложенных блока, регулируемых по размеру пользователем
        //            Integer шр = Integer.valueOf( context.jfx.контекст.параметр( "frame.divider.size",  "4" ) ); //TODO
        SplitPane блок1 = new SplitPane();
        блок1.setOrientation( Orientation.HORIZONTAL );
        блок1.setDividerPosition( 0, 0.3 );
        блок1.getItems().addAll( createLeftBlock(), createRightBlock() );
        SplitPane блок2 = new SplitPane();
        блок2.setOrientation( Orientation.VERTICAL );
        блок2.setDividerPosition( 0, 0.8 );
        блок2.getItems().addAll( блок1, createBottomBlock() );
        return блок2;
    }

    private Node createBottomBlock()
    {
        // консоль сообщений
        Контекст контекст = JFX.контекст;
        int limit = Integer.valueOf( контекст.параметр( "frame.console.rows.buffer", "5" ) ); //"50"
        //"50"
        TextArea табло = new Табло( limit );
        LoggingHandler информатор = new LoggingHandler( new LoggingAgent( табло ) ); //, RED, BLUE, null, null, null, null, null ) );
        //, RED, BLUE, null, null, null, null, null ) );
        информатор.setLevel( Level.INFO ); //TODO setup
        //TODO setup
        контекст.регистратор().addHandler( информатор );
        ScrollPane spoc = new ScrollPane();
        spoc.setContent( табло );
        spoc.setFitToHeight( true );
        spoc.setFitToWidth( true );
        return spoc;
    }

    private TitledPane навигаторПоРабочемуПроекту( int spacing )
    {
        // навигатор по модели данных рабочего проекта
        ToolBar toolbar = new ToolBar();
        toolbar.setOrientation( Orientation.VERTICAL );
        BrowserView навигатор = new BrowserView( JFX, toolbar.getItems() );
        TitledPane панель = new TitledPane();
        панель.textProperty().bind( навигатор.titleProperty() );
        if( Boolean.valueOf( JFX.контекст.параметр( "BrowserView.toolbar", "true" ) ) )
        {
            Pane pane = new HBox( spacing );
            HBox.setHgrow( навигатор, Priority.ALWAYS );
            pane.getChildren().addAll( toolbar, навигатор );
            панель.setContent( pane );
        }
        else
            панель.setContent( навигатор );
        return панель;
    }

    private TitledPane навигаторПоАрхивуПроектов( int spacing )
    {
        // навигатор по архиву проектов
        ToolBar toolbar = new ToolBar();
        toolbar.setOrientation( Orientation.VERTICAL );
        ProjectCatalogView архиватор = new ProjectCatalogView( JFX, toolbar.getItems() );
        TitledPane панель = new TitledPane();
        панель.textProperty().bind( архиватор.titleProperty() );
        if( Boolean.valueOf( JFX.контекст.параметр( "ProjectCatalogView.toolbar", "true" ) ) )
        {
            Pane pane = new HBox( spacing );
            HBox.setHgrow( архиватор, Priority.ALWAYS );
            pane.getChildren().addAll( toolbar, архиватор );
            панель.setContent( pane );
        }
        else
            панель.setContent( архиватор );
        return панель;
    }

    private TitledPane навигаторПоАрхивуБиблиотек( int spacing )
    {
        // навигатор по архиву библиотек
        ToolBar toolbar = new ToolBar();
        toolbar.setOrientation( Orientation.VERTICAL );
        LibraryCatalogView архиватор = new LibraryCatalogView( JFX, toolbar.getItems() );
        TitledPane панель = new TitledPane();
        панель.textProperty().bind( архиватор.titleProperty() );
        if( Boolean.valueOf( JFX.контекст.параметр( "LibraryCatalogView.toolbar", "true" ) ) )
        {
            Pane pane = new HBox( spacing );
            HBox.setHgrow( архиватор, Priority.ALWAYS );
            pane.getChildren().addAll( toolbar, архиватор );
            панель.setContent( pane );
        }
        else
            панель.setContent( архиватор );
        return панель;
    }

}
