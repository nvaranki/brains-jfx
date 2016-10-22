package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbИнструкция;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;

import java.util.logging.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

/**
 * FXML-контроллер редактируемого текста SVG. 
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public class InPlaceText2Controller implements Builder<Node>
{
    private static final LoggerX LOGGER = LoggerX.getLogger(InPlaceText2Controller.class );
    //private static final String RESOURCE_CSS  = "/fxml/editor/SvgTextField.css";
    private static final String CSS_CLASS = "svg-text-field";
    
    private final DbИнструкция ЭЛЕМЕНТ;
    
    @FXML private TextField text;
    @FXML private ComboBox<String> type;
    @FXML private Node редактор;
    
    InPlaceText2Controller( DbИнструкция инструкция )
    {
        this.ЭЛЕМЕНТ = инструкция;
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
        
        type = new ComboBox<>();
        type.getItems().addAll( "xpath" );
        type.setEditable( true );
        type.setMaxWidth( 75 );
        type.setOnKeyPressed( this::onKeyPressed );
//        type.setOnAction( this::onAction );
        
        редактор = new VBox( new HBox( text, type ) );
        
        редактор.getStyleClass().add( CSS_CLASS );
        //text.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return редактор;
    }
    
    @FXML
    protected void initialize()
    {
        JavaFX.getInstance().execute( new ScreenUpdateTask() );
    }
    
    @FXML
    private void onAction( ActionEvent e )
    {
        Data data = new Data( type.getEditor().getText(), text.getText() );
        if( data.processor.trim().isEmpty() )
            LOGGER.getLogger().log( Level.SEVERE, "Processor wasn't set" );
        else if( data.content.trim().isEmpty() )
            LOGGER.getLogger().log( Level.SEVERE, "Instruction wasn't set" );
        else if( ЭЛЕМЕНТ.выполнить() == null )
            LOGGER.getLogger().log( Level.SEVERE, "Instruction fails" );
        else 
        {
            if( ЭЛЕМЕНТ.выполнить().trim().isEmpty() )
                LOGGER.getLogger().log( Level.WARNING, "Instruction returns no text" );
            JavaFX.getInstance().getExecutorService().submit( new DbUpdateTask( data ) );
        }
        e.consume();
    }
    
    @FXML
    private void onKeyPressed( KeyEvent e )
    {
        if( KeyCode.ESCAPE.equals( e.getCode() ) )
        {
            редактор.setVisible( false );
            e.consume();
        }
    }
    
    private class DbUpdateTask extends Task<Void>
    {
        final Data data;
        
        DbUpdateTask( Data input )
        {
            this.data = input;
        }
        
        @Override
        public Void call() throws Exception
        {
            try( Транзакция транзакция = ЭЛЕМЕНТ.транзакция() )
            {
                транзакция.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, ЭЛЕМЕНТ );
                ЭЛЕМЕНТ.процессор( data.processor );
                ЭЛЕМЕНТ.код( data.content );
                транзакция.завершить( true );
            }
            return null;
        }
        
        @Override protected void succeeded()
        {
            редактор.setVisible( false );
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
    
    private class ScreenUpdateTask extends Task<Data>
    {
        @Override
        protected Data call() throws Exception
        {
            Data data;
            try( Транзакция транзакция = ЭЛЕМЕНТ.транзакция() )
            {
                транзакция.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, ЭЛЕМЕНТ );
                data = new Data( ЭЛЕМЕНТ.процессор(), ЭЛЕМЕНТ.код() );
            }
            return data;
        }
        
        @Override
        protected void succeeded()
        {
            Data data = getValue();
            if( !type.getItems().contains( data.processor ) )
                type.getItems().add( data.processor );
            type.getSelectionModel().select( data.processor );
            text.setText( data.content );
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
    
    private static class Data
    {
        String processor, content;

        Data( String processor, String content )
        {
            this.processor = processor;
            this.content = content;
        }
    }
    
}
