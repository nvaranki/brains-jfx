package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.db.FxЗаметка;
import com.varankin.util.LoggerX;

import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров заметки.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class TabNoteController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabNoteController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabNote.css";
    private static final String CSS_CLASS = "properties-tab-note";

    static final String RESOURCE_FXML = "/fxml/archive/TabNote.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxЗаметка заметка;
    
    @FXML private TextArea text;

    /**
     * Создает панель выбора и установки параметров.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель выбора и установки параметров.
     */
    @Override
    public GridPane build()
    {
        text = new TextArea();
        text.setId( "text" );
        text.setWrapText( true );
        text.setPrefRowCount( 5 );
//        text.setPrefColumnCount( 24 );
        text.setEditable( false );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "properties.tab.note.text" ) ), 0, 0 );
        pane.add( text, 0, 1 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
    }
    
    void set( FxЗаметка заметка )
    {
        if( this.заметка != null )
        {
            text.textProperty().unbind();
        }
        if( заметка != null )
        {
            text.textProperty().bind( заметка.текст() );
        }
        this.заметка = заметка;
    }
    
}
