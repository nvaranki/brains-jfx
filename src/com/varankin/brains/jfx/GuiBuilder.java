package com.varankin.brains.jfx;

import com.varankin.brains.jfx.archive.ArchiveController;
import com.varankin.brains.jfx.browser.BrowserView;
import com.varankin.brains.Контекст;
import com.varankin.util.LoggingHandler;
import com.varankin.util.Текст;
import java.util.List;
import java.util.logging.*;
import javafx.beans.property.ReadOnlyStringProperty;
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
//        TitledPane панель2 = навигаторПоАрхивуПроектов( spacing );
//        TitledPane панель3 = навигаторПоАрхивуБиблиотек( spacing );
        панель0.setPrefWidth( 250d );
//        панель2.setPrefWidth( 250d );
//        панель3.setPrefWidth( 250d );
        // панель обозревателей
        Accordion обозреватели = new Accordion();
        обозреватели.getPanes().addAll( панель0, new ArchiveController().build() );//TODO , панель2, панель3 );
        обозреватели.setExpandedPane( панель0 );
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
        BrowserView view = new BrowserView( JFX );
        return навигатор( view, spacing, view.getActions(), view.titleProperty() );
    }

    private TitledPane навигаторПоАрхивуПроектов( int spacing )
    {
        ProjectCatalogView view = new ProjectCatalogView( JFX );
        return навигатор( view, spacing, view.getActions(), view.titleProperty() );
    }

    private TitledPane навигаторПоАрхивуБиблиотек( int spacing )
    {
        LibraryCatalogView view = new LibraryCatalogView( JFX );
        return навигатор( view, spacing, view.getActions(), view.titleProperty() );
    }

    private TitledPane навигатор( Control навигатор, int spacing, 
            List<AbstractJfxAction> actions, ReadOnlyStringProperty title )
    {
        TitledPane панель = new TitledPane();
        панель.textProperty().bind( title );
        String кп = навигатор.getClass().getSimpleName() + ".toolbar";
        if( Boolean.valueOf( JFX.контекст.параметр( кп, "true" ) ) )
        {
            ToolBar toolbar = new ToolBar();
            toolbar.setOrientation( Orientation.VERTICAL );
            for( AbstractJfxAction action : actions )
                if( action != null )
                    toolbar.getItems().add( action.makeButton() );
                else
                    toolbar.getItems().add( new Separator( Orientation.HORIZONTAL ) );
            Pane pane = new HBox( spacing );
            pane.setPrefWidth( 250d );
            HBox.setHgrow( навигатор, Priority.ALWAYS );
            pane.getChildren().addAll( toolbar, навигатор );
            панель.setContent( pane );
        }
        else
        {
            панель.setContent( навигатор );
        }
        навигатор.setPrefWidth( 250d );
        return панель;
    }

}
