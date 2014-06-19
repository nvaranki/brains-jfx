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
import java.util.concurrent.ExecutionException;
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
    
    @FXML private TextField editor;
    
    public SvgTextFieldController( Неизвестный элемент, Text text_ ) 
    {
        ЭЛЕМЕНТ = элемент;
        text = text_;
        children = childrenOf( text.getParent() );
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
        
        editor.getStyleClass().add( CSS_CLASS );
        //text.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return editor;
    }
        
    @FXML
    protected void initialize()
    {
        for( Неизвестный н : ЭЛЕМЕНТ.прочее() )
            if( н.тип().название() == null )
                editor.setText( н.атрибут( Xml.XML_TEXT, "?" ) );
            else if( н instanceof Инструкция )
                editor.setText( н.атрибут( Xml.PI_ATTR_INSTRUCTION, "{?}" ) );

        editor.setOnAction( this::onAction );
        editor.setOnKeyPressed( this::onKeyPressed );
    }
    
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
    
    private void onKeyPressed( KeyEvent e )
    {
        if( KeyCode.ESCAPE.equals( e.getCode() ) )
        {
            children.remove( editor );
            text.setVisible( true );
            e.consume();
        }
    }
    
    private class DbUpdater extends Task<String>
    {
        final String input;
        
        DbUpdater( String input )
        {
            this.input = input;
        }
        
        @Override
        public String call() throws Exception
        {
            Архив архив = JavaFX.getInstance().контекст.архив;
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
                String update = get();
                if( update.trim().isEmpty() ) update = "?";
                text.setText( update );
                children.remove( editor );
                text.setVisible( true );
            }
            catch( InterruptedException | ExecutionException ex )
            {
                LOGGER.log( Level.SEVERE, "Failure to get text: {0}.", ex.getMessage() );
            }
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
