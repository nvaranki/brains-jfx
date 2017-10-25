package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.db.FxТекстовыйБлок;
import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Builder;

/**
 * FXML-контроллер панели ввода и редактирования текста.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
public final class TabTextController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabTextController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabText.css";
    private static final String CSS_CLASS = "properties-tab-text";

    static final String RESOURCE_FXML = "/fxml/archive/TabText.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxТекстовыйБлок блок;
    
    @FXML private TextArea text;

    /**
     * Создает панель ввода и редактирования.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель ввода и редактирования.
     */
    @Override
    public GridPane build()
    {
        text = new TextArea();
        text.setId( "text" );
        text.setFocusTraversable( true );
        
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setHgrow( Priority.ALWAYS );
        
        RowConstraints rc0 = new RowConstraints();
        RowConstraints rc1 = new RowConstraints();
        rc1.setVgrow( Priority.ALWAYS );
        
        GridPane pane = new GridPane();
        pane.getColumnConstraints().addAll( cc0 );
        pane.getRowConstraints().addAll( rc0, rc1 );
        pane.add( new Label( LOGGER.text( "properties.text" ) ), 0, 0 );
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
    
    void set( FxТекстовыйБлок блок )
    {
        if( this.блок != null )
        {
            text.textProperty().unbindBidirectional( this.блок.текст() );
        }
        if( блок != null )
        {
            text.textProperty().bindBidirectional( блок.текст() );
        }
        this.блок = блок;
    }
    
}
