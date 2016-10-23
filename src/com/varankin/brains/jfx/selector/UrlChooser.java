package com.varankin.brains.jfx.selector;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.StageCloseHandler;
import com.varankin.util.Текст;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * Интерактивная форма запроса URL.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
class UrlChooser extends Stage
{
    private static final Logger LOGGER = Logger.getLogger( UrlChooser.class.getName() );

    private final Текст словарь;
    private StringProperty urlText;
    private UrlProperty url;
    private TextField text;
    private Text status;

    UrlChooser( JavaFX jfx, boolean dispose )
    {
        url = new UrlProperty();
        словарь = Текст.ПАКЕТЫ.словарь( UrlChooser.class, jfx.контекст.специфика );
        urlText = new SimpleStringProperty();
        text = new TextField( словарь.текст( "sample" ) );
        urlText.bindBidirectional( text.textProperty() );
        text.setMinWidth( 500 );
        status = new Text();
        status.setFill( Color.RED );
        Button импорт = new Button( словарь.текст( "connect" ));
        Button отмена = new Button( словарь.текст( "cancel" ));
        импорт.setDefaultButton( true );
        отмена.setCancelButton( true );
        импорт.onActionProperty().setValue( new AcceptHandler() );
        отмена.onActionProperty().setValue( new CancelHandler() );
        //initOwner( jfx.платформа );
        initStyle( StageStyle.UTILITY );
        initModality( Modality.APPLICATION_MODAL );
        setTitle( словарь.текст( "title" ) );
        setResizable( true );
        setScene( new Scene( new Layout( new Text( "URL:" ), text, status, 
                импорт, отмена, jfx.getDefaultGap() ), 550.0, 80.0, Color.WHITE ) );
        setOnCloseRequest( new StageCloseHandler<WindowEvent>( this, dispose ) );
    }
    
    final StringProperty initialPathProperty()
    {
        return urlText;
    }
    
    URL showOpenDialog( Window owner )
    {
        if( getOwner() == null )
            initOwner( owner );
        else if( !getOwner().equals( owner ) )
            LOGGER.log( Level.FINE, "Window owner reset to {0}", owner );
        text.requestFocus();
        showAndWait();
        return url.getValue();
    }
    
    private class AcceptHandler implements EventHandler<ActionEvent>
    {
        @Override
        public void handle( ActionEvent __ )
        {
            status.setText( null );
            url.value = null;
            String value = urlText.getValue();
            if( value == null )
            {
                hide();
            }
            else if( value.contains( "<" ) || value.contains( ">" ) )
            {
                text.requestFocus();
                text.selectAll();
                status.setText( словарь.текст( "wrong" ) );
            }
            else
                try
                {
                    URL u = new URL( value );
                    String protocol = u.getProtocol();
                    String host = u.getHost();
                    String path = u.getPath();
                    if( protocol == null || protocol.isEmpty() || protocol.contains( "<" ) || protocol.contains( ">" ) )
                    {
                        text.requestFocus();
                        text.selectAll();
                        status.setText( словарь.текст( "wrong" ) );
                    }
                    else if( host == null || host.isEmpty() || host.contains( "<" ) || host.contains( ">" ) )
                    {
                        text.requestFocus();
                        text.selectAll();
                        status.setText( словарь.текст( "wrong" ) );
                    }
                    else if( path == null || path.isEmpty() || path.contains( "<" ) || path.contains( ">" ) )
                    {
                        text.requestFocus();
                        text.selectAll();
                        status.setText( словарь.текст( "wrong" ) );
                    }
                    else
                    {
                        url.value = u;
                        hide();
                    }
                } 
                catch( MalformedURLException ex )
                {
                    LOGGER.log( Level.SEVERE, null, ex );
                    text.requestFocus();
                    text.selectAll();
                    status.setText( словарь.текст( "wrong" ) );
                }
        }
    }
    
    private class CancelHandler implements EventHandler<ActionEvent>
    {
        @Override
        public void handle( ActionEvent __ )
        {
            status.setText( null );
            url.value = null;
            hide();
        }
    }
    
    private class UrlProperty extends ReadOnlyObjectPropertyBase<URL>
    {
        URL value;

        @Override
        public URL get()
        {
            return value;
        }

        @Override
        public Object getBean()
        {
            return UrlChooser.this;
        }

        @Override
        public String getName()
        {
            return "url";
        }
        
    }

    private class Layout extends GridPane
    {
        Layout( Node label, Node text, Node status, Node импорт, Node отмена, double gap )
        {
            setHgap( gap );
            setVgap( gap );
            setPadding( new Insets( gap ) );
            
            add( label, 0, 0 );
            add( text, 1, 0, 2, 1 );
            add( status, 0, 1, 2, 1 );
            setHalignment( status, HPos.LEFT );
            HBox bp = new HBox( gap );
            bp.getChildren().addAll( импорт, отмена );
            bp.setAlignment( Pos.CENTER_RIGHT );
            add( bp, 2, 1 );
            setHalignment( bp, HPos.RIGHT );
        }
    }
    
}
