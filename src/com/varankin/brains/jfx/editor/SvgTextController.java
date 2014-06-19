package com.varankin.brains.jfx.editor;

import com.varankin.brains.artificial.io.xml.Xml;
import static com.varankin.brains.artificial.io.xml.XmlSvg.*;
import com.varankin.brains.db.*;
import com.varankin.brains.jfx.JavaFX;
import static com.varankin.brains.jfx.editor.InPlaceEditorBuilder.*;
import com.varankin.util.LoggerX;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.*;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Builder;

/**
 * FXML-контроллер текста SVG. 
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class SvgTextController implements Builder<Text>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( SvgTextController.class );
    //private static final String RESOURCE_CSS  = "/fxml/editor/SvgText.css";
    private static final String CSS_CLASS = "svg-text";
    
    private final Неизвестный ЭЛЕМЕНТ;
    
    private SvgTextFieldController tfc;
    private EventHandler<? super MouseEvent> handlerMouseClick;
    
    @FXML private Text text;
    
    public SvgTextController( Неизвестный элемент ) 
    {
        ЭЛЕМЕНТ = элемент;
    }

    /**
     * Создает {@linkplain Node графический элемент} для отображения текста. 
     * Применяется в конфигурации без FXML.
     * 
     * @return графический элемент.
     */
    @Override
    public Text build()
    {
        text = new Text();
        
        text.getStyleClass().add( CSS_CLASS );
        //text.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return text;
    }
        
    @FXML
    protected void initialize()
    {
        text.setX( asDouble( ЭЛЕМЕНТ, SVG_ATTR_X, 0d ) );
        text.setY( asDouble( ЭЛЕМЕНТ, SVG_ATTR_Y, 0d ) );
        for( Неизвестный н : ЭЛЕМЕНТ.прочее() )
            if( н.тип().название() == null )
                text.setText( н.атрибут( Xml.XML_TEXT, "?" ) );
            else if( н instanceof Инструкция )
                text.setText( ((Инструкция)н).выполнить() );
        if( text.getText().isEmpty() )
            text.setText( "?" );
        text.setFill( asColor( ЭЛЕМЕНТ, SVG_ATTR_FILL, null ) );
        text.setStroke( asColor( ЭЛЕМЕНТ, SVG_ATTR_STROKE, null ) );
        text.setOnMouseClicked( handlerMouseClick );
    }
    
    void setEditable( boolean значение )
    {
        handlerMouseClick = значение ? this::handleMouseClick : null;
    }

    private void handleMouseClick( MouseEvent event )
    {
        Parent parent = text.getParent();
        ObservableList<Node> children;
        if( parent instanceof Group )
            children = ((Group)parent).getChildren();
        else if( parent instanceof Pane )
            children = ((Pane)parent).getChildren();
        else
            return;

        if( tfc == null ) tfc = new SvgTextFieldController( ЭЛЕМЕНТ );
        
        TextField editor = tfc.build();
        editor.setTranslateX( text.getX() );
        editor.setTranslateY( text.getY() - text.getBaselineOffset() );
        Terminator gu = new Terminator( text, editor, children );
        editor.setOnAction( (ActionEvent e) ->
        {
            String input = editor.getText().trim();
            JavaFX.getInstance().getExecutorService().submit(
                new DbUpdater( ЭЛЕМЕНТ, input, gu) );
            event.consume();
        });
        editor.setOnKeyPressed( (KeyEvent e) ->
        {
            if( KeyCode.ESCAPE.equals( e.getCode() ) )
            {
                children.remove( editor );
                text.visibleProperty().setValue( true );
                event.consume();
            }
        } );
        text.visibleProperty().setValue( false );
        children.add( editor );
        editor.requestFocus();
        event.consume();
    }
    
    //<editor-fold defaultstate="collapsed" desc="классы">
    
    private static class DbUpdater extends Task<String>
    {
        final Неизвестный ЭЛЕМЕНТ;
        final Архив архив;
        final String input;
        final Terminator guiUpdater;
        
        DbUpdater( Неизвестный ЭЛЕМЕНТ, String input, Terminator guiUpdater )
        {
            this.ЭЛЕМЕНТ = ЭЛЕМЕНТ;
            this.архив = JavaFX.getInstance().контекст.архив;
            this.input = input;
            this.guiUpdater = guiUpdater;
        }
        
        @Override
        public String call() throws Exception
        {
            Транзакция транзакция = архив.транзакция();
            транзакция.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, архив );
            boolean завершено = false;
            String update = null;
            try
            {
                ЭЛЕМЕНТ.прочее().clear();
                if( input.matches( "\\{.*\\@.*\\}" ) )
                {
                    Инструкция инструкция = ЭЛЕМЕНТ.addInstructionBlock( "xpath", input );
                    update = инструкция.выполнить();
                }
                else
                {
                    ЭЛЕМЕНТ.addTextBlock( input );
                    update = input;
                }
                завершено = true;
            }
            finally
            {
                транзакция.завершить( завершено );
            }
            return update;
        }
        
        @Override protected void succeeded()
        {
            super.succeeded();
            try
            {
                guiUpdater.update = get();
                guiUpdater.run();
            }
            catch( InterruptedException ex )
            {
                Logger.getLogger( SvgTextController.class.getName() ).log( Level.SEVERE, null, ex );
            }
            catch( ExecutionException ex )
            {
                Logger.getLogger( SvgTextController.class.getName() ).log( Level.SEVERE, null, ex );
            }
        }
        
    }
    
    private static class Terminator implements Runnable
    {
        final Collection<Node> children;
        final Text text;
        final Node editor;
        String update;
        
        Terminator( Text text, Node editor, Collection<Node> children )
        {
            this.children = children;
            this.text = text;
            this.editor = editor;
        }
        
        @Override
        public void run()
        {
            text.setText( update );
            children.remove( editor );
            text.visibleProperty().setValue( true );
        }
    }
    
    //</editor-fold>
    
}
