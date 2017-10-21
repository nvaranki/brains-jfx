package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.AbstractFilter;
import com.varankin.brains.jfx.db.FxЗаметка;
import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Builder;
import javafx.util.StringConverter;

/**
 * FXML-контроллер панели выбора и установки параметров заметки.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
public final class TabNoteController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabNoteController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabNote.css";
    private static final String CSS_CLASS = "properties-tab-note";
    private static final StringConverter<Long> GDC = new GroupedDigitsConverter();

    static final String RESOURCE_FXML = "/fxml/archive/TabNote.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxЗаметка заметка;
    
    @FXML private TextArea text;
    @FXML private TextField level;

    /**
     * Создает панель выбора и установки параметров.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель выбора и установки параметров.
     */
    @Override
    public GridPane build()
    {
        level = new TextField();
        level.setId( "level" );
        
        text = new TextArea();
        text.setId( "text" );
        text.setWrapText( true );
        text.setEditable( false );
        
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setMinWidth( 90 );
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow( Priority.ALWAYS );
        
        RowConstraints rc0 = new RowConstraints();
        RowConstraints rc1 = new RowConstraints();
        RowConstraints rc2 = new RowConstraints();
        rc2.setVgrow( Priority.ALWAYS );

        GridPane pane = new GridPane();
        pane.getColumnConstraints().addAll( cc0, cc1 );
        pane.getRowConstraints().addAll( rc0, rc1, rc2 );
        pane.add( new Label( LOGGER.text( "properties.tab.note.level" ) ), 0, 0 );
        pane.add( level, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.tab.note.text" ) ), 0, 1 );
        pane.add( text, 0, 2, 2, 1 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
        level.setTextFormatter( new TextFormatter<>( GDC, null, new GroupedIntegerFilter() ) );
    }
    
    void set( FxЗаметка заметка )
    {
        if( this.заметка != null )
        {
            ((TextFormatter<Long>)level.getTextFormatter()).valueProperty()
                    .unbindBidirectional( this.заметка.глубина() );
            text.textProperty().unbind();
        }
        if( заметка != null )
        {
            ((TextFormatter<Long>)level.getTextFormatter()).valueProperty()
                    .bindBidirectional( заметка.глубина() );
            text.textProperty().bind( заметка.текст() );
        }
        this.заметка = заметка;
    }
    
    private static class GroupedDigitsConverter extends StringConverter<Long>
    {
        static final long SIZE = 100L; //TODO параметр конфигурации
        static final String FMT = " %0" + ( (int)Math.round( Math.log10( SIZE ) ) ) + "d";
        
        @Override
        public String toString( Long object )
        {
            if( object == null ) return "";
            StringBuilder sb = new StringBuilder();
            long v = object;
            do 
            {
                long part = v % SIZE;
                sb.insert(0, String.format(FMT, part ) );
                v /= SIZE;
            }
            while( v > 0L );
            return sb.toString();
        }

        @Override
        public Long fromString( String string )
        {
            if( string == null || string.trim().isEmpty() ) return null;
            else return Long.valueOf( string.replaceAll( "\\s+", "" ) );
        }
    };
    
    private static class GroupedIntegerFilter extends AbstractFilter
    {
        static final Pattern PATTERN = Pattern.compile( "\\s*(\\d\\s*)+" );

        public GroupedIntegerFilter() 
        {
            super( PATTERN, "filter.Integer" );
        }

    }

}
