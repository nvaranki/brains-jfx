package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbТекстовыйБлок;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;

import java.util.logging.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Builder;

/**
 * FXML-контроллер редактируемого текста SVG. 
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public class InPlaceTextController implements Builder<Node>
{
    private static final LoggerX LOGGER = LoggerX.getLogger(InPlaceText2Controller.class );
    //private static final String RESOURCE_CSS  = "/fxml/editor/SvgTextField.css";
    private static final String CSS_CLASS = "svg-text-field";
    
    private final DbТекстовыйБлок ЭЛЕМЕНТ;
    
    @FXML private TextField text;
    
    InPlaceTextController( DbТекстовыйБлок блок )
    {
        this.ЭЛЕМЕНТ = блок;
    }
    
    /**
     * Создает {@linkplain Node графический элемент} для отображения текста. 
     * Применяется в конфигурации без FXML.
     * 
     * @return графический элемент.
     */
    @Override
    public Node build()
    {
        text = new TextField();
        text.setText( "Loading..." );
        text.setOnKeyPressed( this::onKeyPressed );
        text.setOnAction( this::onAction );
        
        text.getStyleClass().add( CSS_CLASS );
        //text.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return text;
    }
    
    @FXML
    protected void initialize()
    {
        JavaFX.getInstance().execute( new ScreenUpdateTask() );
    }
    
    @FXML
    private void onAction( ActionEvent e )
    {
        String data = text.getText();
        if( data.trim().isEmpty() )
            LOGGER.getLogger().log( Level.SEVERE, "Text wasn't set" );
        else
            JavaFX.getInstance().getExecutorService().submit( new DbUpdateTask( data ) );
        e.consume();
    }
    
    @FXML
    private void onKeyPressed( KeyEvent e )
    {
        if( KeyCode.ESCAPE.equals( e.getCode() ) )
        {
            text.setVisible( false );
            e.consume();
        }
    }
    
    private class DbUpdateTask extends Task<Void>
    {
        final String data;
        
        DbUpdateTask( String input )
        {
            this.data = input;
        }
        
        @Override
        public Void call() throws Exception
        {
            try( Транзакция транзакция = ЭЛЕМЕНТ.транзакция() )
            {
                транзакция.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, ЭЛЕМЕНТ );
                ЭЛЕМЕНТ.текст( data );
                транзакция.завершить( true );
            }
            return null;
        }
        
        @Override protected void succeeded()
        {
            text.setVisible( false );
        }
        
        @Override protected void failed() 
        { 
            Throwable exception = getException();
            String msg = exception == null ? null : exception.getMessage() != null ? exception.getMessage() : 
                    exception.getClass().getSimpleName();
            LOGGER.getLogger().log( Level.SEVERE, "Failure to update database with text{0}.", 
                    msg != null ? ": " + msg : "" );
        }
        
    }
    
    private class ScreenUpdateTask extends Task<String>
    {
        @Override
        protected String call() throws Exception
        {
            String data;
            try( Транзакция транзакция = ЭЛЕМЕНТ.транзакция() )
            {
                транзакция.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, ЭЛЕМЕНТ );
                data = ЭЛЕМЕНТ.текст();
            }
            return data;
        }
        
        @Override
        protected void succeeded()
        {
            String data = getValue();
            text.setText( data );
        }
        
        @Override
        protected void failed()
        {
            Throwable exception = getException();
            String msg = exception == null ? null : exception.getMessage() != null ? exception.getMessage() : 
                    exception.getClass().getSimpleName();
            LOGGER.getLogger().log( Level.SEVERE, "Failure to update editable text{0}.", 
                    msg != null ? ": " + msg : "" );
        }
        
    }
    
}
