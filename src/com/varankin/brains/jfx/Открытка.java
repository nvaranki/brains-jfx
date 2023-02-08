package com.varankin.brains.jfx;

import com.varankin.brains.Конфигурация;
import com.varankin.util.Текст;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Экранная форма "О программе".
 *
 * @author &copy; 2023 Николай Варанкин
 */
class Открытка extends Stage
{
    private final ImageView логотип;
    private final Text заметка, версия, права;
    private final Hyperlink сайт;
    private final Button закрыть;

    Открытка( JavaFX jfx, boolean dispose )
    {
        Текст словарь = Текст.ПАКЕТЫ.словарь( Открытка.class.getPackage(), null, jfx.контекст.специфика );
        InputStream stream = getClass().getClassLoader().getResourceAsStream( "images/dragon.jpg" );
        логотип = new ImageView( new Image( stream ) );
        заметка = new Text( словарь.текст( "Card.memo" ));
        версия = new Text( словарь.текст( "Card.version" ) + jfx.контекст.конфигурация.параметр( Конфигурация.Параметры.VERSION ) );
        права = new Text( словарь.текст( "Card.copyright" ));
        закрыть = new Button( словарь.текст( "Card.close" ));
        закрыть.setOnAction( new StageCloseHandler<>( this, dispose ) );
        сайт = new Hyperlink ( "www.varankin.com" );
        //сайт.setVisited( false );
        String html = jfx.контекст.конфигурация.параметр( "Card.company", "http://varankin.com/" );//TODO
        сайт.setOnAction( new HtmlLinkHandler( html, jfx ) );
                
        initOwner( jfx.платформа );
        initStyle( StageStyle.UTILITY );
        initModality( Modality.APPLICATION_MODAL );
        setTitle( словарь.текст( "Card.title" ) );
        setResizable( false );
        Scene scene = new Scene( new Layout( jfx ), 350.0, 160.0, Color.WHITE );
        //Позиционер.настроить( (Window)this );
        setScene( scene );
        setOnCloseRequest( new StageCloseHandler<>( this, dispose ) );
        закрыть.requestFocus();
     }
    
    private class Layout extends GridPane
    {
        Layout( JavaFX jfx )
        {
            double gap = jfx.getDefaultGap();
            setHgap( gap );
            setVgap( gap );
            setPadding( new Insets( gap ) );

            add( логотип, 0, 0, 1, 4 );
            add( заметка, 1, 0 );
            add( версия, 1, 1 );
            add( сайт, 1, 2 );
            add( права, 1, 3 );
            add( закрыть, 0, 4, 2, 1 );
            
            setHalignment( логотип, HPos.CENTER );
            setValignment( логотип, VPos.CENTER );
            setHalignment( закрыть, HPos.RIGHT );
        }
    }
    
    static private class HtmlLinkHandler implements EventHandler<ActionEvent>, Runnable
    {
        static private final Logger LOGGER = Logger.getLogger( Открытка.class.getName() );
        
        private final String href;
        private final JavaFX jfx;

        HtmlLinkHandler( String href, JavaFX jfx )
        {
            this.href = href;
            this.jfx = jfx;
        }
        
        @Override
        public void handle( ActionEvent __ )
        {
            //Platform.runLater( this );
            Thread thread = new Thread( this );
            thread.setDaemon( true );
            thread.start();
        }

        @Override
        public void run()
        {
            try
            {
                jfx.browse( href );
            }
            catch( IOException | URISyntaxException | UnsupportedOperationException ex )
            {
                LOGGER.log( Level.WARNING, ex.getLocalizedMessage() );
            }
        }

    }

}
