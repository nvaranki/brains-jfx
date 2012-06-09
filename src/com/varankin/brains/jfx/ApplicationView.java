package com.varankin.brains.jfx;

import com.varankin.biz.appl.LoggingHandler;
import com.varankin.brains.Контекст;
import com.varankin.util.Текст;
import java.io.IOException;
import java.util.logging.Level;
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
public class ApplicationView extends Scene
{

    public ApplicationView( JavaFX jfx )
    {
        super( newRoot( new Context( jfx ) ) );
        //Context context = new Context( контекст );
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
        public final JavaFX jfx;
        public final ActionFactory actions;
//        private Scene frame;

        private Context( JavaFX jfx )
        {
            this.jfx = jfx;
            this.actions = new ActionFactory( this );
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

    static private Region newRoot( Context context )
    {
        Контекст контекст = context.jfx.контекст;

        BorderPane root = new BorderPane();
        // меню приложения
        MenuBar mb = new ApplicationMenuBar( context );
        root.setTop( mb );
        
        // навигатор по модели данных
        BrowserView навигатор = new BrowserView( context );
        StackPane spbv = new StackPane();
        spbv.getChildren().add( навигатор );

        // окно просмотра
        TextArea просмотр = new TextArea( "preview" );
        Pane sppw = new StackPane();
        sppw.getChildren().add( просмотр );
        
        // консоль сообщений
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

        // блоки, регулируемые по размеру
        Integer шр = Integer.valueOf( контекст.параметр( "frame.divider.size",  "4" ) ); //TODO 
        SplitPane блок1 = new SplitPane();
        блок1.setOrientation( Orientation.HORIZONTAL );
        блок1.setDividerPosition( 0, 0.3 );
        //блок1.getDividers().get(0).setMaxWidth( шр.doubleValue() );
        блок1.getItems().addAll( spbv, sppw );
        SplitPane блок2 = new SplitPane();
        блок2.setOrientation( Orientation.VERTICAL );
        блок2.setDividerPosition( 0, 0.8 );
        блок2.getItems().addAll( блок1, spoc );
        root.setCenter( блок2 );
        
        return root;
    }
    
}
