package com.varankin.brains.jfx;

import com.varankin.brains.jfx.archive.ArchiveController;
import com.varankin.brains.jfx.browser.BrowserController;
import com.varankin.brains.Контекст;
import com.varankin.util.LoggingHandler;
import com.varankin.util.Текст;

import java.util.logging.*;
import javafx.beans.property.SimpleStringProperty;
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
 * @author &copy; 2023 Николай Варанкин
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

        int w = Integer.valueOf( JFX.контекст.конфигурация.параметр( "frame.width",  "800" ));
        int h = Integer.valueOf( JFX.контекст.конфигурация.параметр( "frame.height", "600" ));
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
        BuilderFX<TitledPane,BrowserController> фабрикаНавигатораПроектов = new BuilderFX<>();
        фабрикаНавигатораПроектов.init( BrowserController.class, 
            BrowserController.RESOURCE_FXML, BrowserController.RESOURCE_BUNDLE );
        TitledPane панель0 = фабрикаНавигатораПроектов.getNode();
//        TitledPane панель2 = навигаторПоАрхивуПроектов( spacing );
//        TitledPane панель3 = навигаторПоАрхивуБиблиотек( spacing );
        панель0.setPrefWidth( 250d );
//        панель2.setPrefWidth( 250d );
//        панель3.setPrefWidth( 250d );
        BuilderFX<TitledPane,ArchiveController> фабрикаНавигатораАрхива = new BuilderFX<>();
        фабрикаНавигатораАрхива.init( ArchiveController.class, 
            ArchiveController.RESOURCE_FXML, ArchiveController.RESOURCE_BUNDLE );
        TitledPane панель1 = фабрикаНавигатораАрхива.getNode();
        // панель обозревателей
        Accordion обозреватели = new Accordion();
        обозреватели.getPanes().addAll( панель0, /*панель0t,*/ панель1 );//TODO , панель2, панель3 );
        обозреватели.setExpandedPane( панель1 );
        обозреватели.setPrefWidth( 250d );
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
        views.add( new TitledSceneGraph( new Pane(), null, new SimpleStringProperty( "Quick start" ) ) );
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
        блок1.setDividerPosition( 1, 0.7 );
        блок1.getItems().addAll( createLeftBlock(), createRightBlock() );
        SplitPane блок2 = new SplitPane();
        блок2.setOrientation( Orientation.VERTICAL );
        блок2.setDividerPosition( 0, 0.8 );
        блок2.setDividerPosition( 1, 0.2 );
        блок2.getItems().addAll( блок1, createBottomBlock() );
        return блок2;
    }

    private Node createBottomBlock()
    {
        // консоль сообщений
        Контекст контекст = JFX.контекст;
        int limit = Integer.valueOf( контекст.конфигурация.параметр( "frame.console.rows.buffer", "500" ) ); //"50"//"5"
        TextArea табло = new Табло( limit );
        LoggingHandler информатор = new LoggingHandler( new LoggingAgent( табло ) ); //, RED, BLUE, null, null, null, null, null ) );
        //, RED, BLUE, null, null, null, null, null ) );
        информатор.setLevel( Level.INFO ); //TODO setup
        //TODO setup
        контекст.регистратор().addHandler( информатор );
        return табло;
    }

}
