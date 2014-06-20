package com.varankin.brains.jfx.editor;

import com.varankin.brains.artificial.io.xml.Xml;
import com.varankin.brains.db.Архив;
import com.varankin.brains.db.Инструкция;
import com.varankin.brains.db.Неизвестный;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.jfx.JavaFX;
import static com.varankin.brains.jfx.editor.InPlaceEditorBuilder.childrenOf;
import com.varankin.util.LoggerX;
import java.util.Collection;
import java.util.logging.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.util.Builder;

/**
 * FXML-контроллер редактируемого текста SVG. 
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class SvgTextFieldController implements Builder<TextField>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( SvgTextFieldController.class );
    //private static final String RESOURCE_CSS  = "/fxml/editor/SvgTextField.css";
    private static final String CSS_CLASS = "svg-text-field";
    
    private final Неизвестный ЭЛЕМЕНТ;
    private final Text text;
    private final Collection<Node> children;
    private final boolean instruction;
    
    @FXML private TextField editor;
    
    public SvgTextFieldController( Неизвестный элемент, Text text_, boolean instruction ) 
    {
        ЭЛЕМЕНТ = элемент;
        text = text_;
        children = childrenOf( text.getParent() );
        this.instruction = instruction;
    }

    /**
     * Создает {@linkplain Node графический элемент} для отображения текста. 
     * Применяется в конфигурации без FXML.
     * 
     * @return графический элемент.
     */
    @Override
    public TextField build()
    {
        editor = new TextField();
        editor.setOnAction( this::onAction );
        editor.setOnKeyPressed( this::onKeyPressed );
        
        editor.getStyleClass().add( CSS_CLASS );
        //text.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return editor;
    }
    
    @FXML
    protected void initialize()
    {
        editor.setText( getContent() );
    }
    
    @FXML
    private void onAction( ActionEvent e )
    {
        String input = editor.getText().trim();
        if( input.isEmpty() )
        {
            //TODO ???
        }
        else
            JavaFX.getInstance().getExecutorService().submit( new DbUpdater( input ) );
        e.consume();
    }
    
    @FXML
    private void onKeyPressed( KeyEvent e )
    {
        if( KeyCode.ESCAPE.equals( e.getCode() ) )
        {
            children.remove( editor );
            text.setVisible( true );
            e.consume();
        }
    }
    
    private String getContent()
    {
        String t = "?";
        for( Неизвестный н : ЭЛЕМЕНТ.прочее() )
            if( н.тип().название() == null )
                t = н.атрибут( Xml.XML_TEXT, "?" );
            else if( н instanceof Инструкция )
                if( instruction )
                    t = ((Инструкция)н).код();
                else
                    t = ((Инструкция)н).выполнить();
        return t;
    }
    
    private void setContent( String input )
    {
        Инструкция инструкция = null;
        Неизвестный текст = null;
        for( Неизвестный н : ЭЛЕМЕНТ.прочее() )
            if( н.тип().название() == null )
                текст = н;
            else if( н instanceof Инструкция )
                инструкция = ((Инструкция)н);

        if( input.matches( "\\{.*\\@.*\\}" ) )
            if( инструкция != null )
            {
                инструкция.код( input );
            }
            else
            {
                ЭЛЕМЕНТ.прочее().clear();
                ЭЛЕМЕНТ.инструкция( "xpath", input );
            }
        else if( инструкция != null )
        {
            if( !инструкция.определить( input ) )
                LOGGER.log( Level.SEVERE, "Failure to setup attribute by instruction." );
        }
        else if( текст != null )
        {
            текст.определить( Xml.XML_TEXT, null, input );
        }
        else
        {
            ЭЛЕМЕНТ.прочее().clear();
            ЭЛЕМЕНТ.addTextBlock( input );
        }
    }
    
    private class DbUpdater extends Task<Void>
    {
        final String input;
        
        DbUpdater( String input )
        {
            this.input = input;
        }
        
        @Override
        public Void call() throws Exception
        {
            Архив архив = JavaFX.getInstance().контекст.архив;
            Транзакция транзакция = архив.транзакция();
            транзакция.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, архив );
            boolean завершено = false;
            try
            {
                setContent( input );
                завершено = true;
            }
            finally
            {
                транзакция.завершить( завершено );
            }
            return null;
        }
        
        @Override protected void succeeded()
        {
            super.succeeded();
            children.remove( editor );
            text.setVisible( true );
        }
        
        @Override protected void failed() 
        { 
            super.failed();
            Throwable ex = getException();
            if( ex != null )
                LOGGER.log( Level.SEVERE, "Failure to update text in database: {0}.", ex.getMessage() );
            else
                LOGGER.log( Level.SEVERE, "Failure to update text in database." );
        }
        
    }
    
}
