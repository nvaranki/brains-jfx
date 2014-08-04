package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.ТекстовыйБлок;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.Arrays;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели ввода и редактирования текста.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class TextController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TextController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/Text.css";
    private static final String CSS_CLASS = "properties-text";

    private final AttributeAgent codeAgent;

    private ТекстовыйБлок блок;
    
    @FXML private TextArea text;

    public TextController()
    {
        codeAgent = new CodeAgent();
    }
    
    /**
     * Создает панель ввода и редактирования текста.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель ввода и редактирования.
     */
    @Override
    public GridPane build()
    {
        text = new TextArea();
        text.setFocusTraversable( true );
        text.setPrefColumnCount( 40 );
        text.setPrefRowCount( 15 );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "properties.text" ) ), 0, 0 );
        pane.add( text, 0, 1, 2, 1 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
    }
    
    @FXML
    void onActionApply( ActionEvent event )
    {
        //applied.setValue( false );
        JavaFX.getInstance().execute( new ScreenToStorageTask( блок, Arrays.asList( codeAgent ) ) );
    }

    void reset( ТекстовыйБлок блок )
    {
        this.блок = блок;
        JavaFX.getInstance().execute( new StorageToScreenTask( блок, Arrays.asList( codeAgent ) ) );
    }
    
    private class CodeAgent implements AttributeAgent
    {
        volatile String текст;

        @Override
        public void fromScreen()
        {
            текст = text.getText();
        }
        
        @Override
        public void toScreen()
        {
            text.setText( текст );
        }
        
        @Override
        public void fromStorage()
        {
            текст = блок.текст();
        }
        
        @Override
        public void toStorage()
        {
            блок.текст( текст );
        }

    }
    
}
