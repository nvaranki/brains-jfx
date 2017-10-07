package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.db.FxАрхив;
import com.varankin.util.LoggerX;
import java.text.DateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров архива.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
public final class TabArchiveController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabArchiveController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabArchive.css";
    private static final String CSS_CLASS = "properties-tab-archive";

    static final String RESOURCE_FXML = "/fxml/archive/TabArchive.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private final DateFormat DTF = DateFormat.getDateTimeInstance();
    
    private FxАрхив архив;
    
    @FXML private TextField uri, created, changed;

    /**
     * Создает панель выбора и установки параметров архива.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель выбора и установки параметров.
     */
    @Override
    public GridPane build()
    {
        uri = new TextField();
        uri.setId( "uri" );
        uri.setPrefColumnCount( 32 );
        uri.setEditable( false );
        
        created = new TextField();
        created.setId( "created" );
        created.setPrefColumnCount( 24 );
        created.setEditable( false );
        
        changed = new TextField();
        changed.setId( "changed" );
        changed.setPrefColumnCount( 24 );
        changed.setEditable( false );
        
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setMinWidth( 90 );
        
        GridPane pane = new GridPane();
        pane.getColumnConstraints().add( cc0 );
        pane.add( new Label( LOGGER.text( "archive.location" ) ), 0, 0 );
        pane.add( uri, 1, 0 );
        pane.add( new Label( LOGGER.text( "archive.created" ) ), 0, 1 );
        pane.add( created, 1, 1 );
        pane.add( new Label( LOGGER.text( "archive.changed" ) ), 0, 2 );
        pane.add( changed, 1, 2 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
    }
    
    void set( FxАрхив архив )
    {
        if( this.архив != null )
        {
            uri.textProperty().unbind();
            created.textProperty().unbind();
        }
        if( архив != null )
        {
            uri.textProperty().bind( архив.расположение() );
            created.textProperty().bind( Bindings.createStringBinding( () -> 
                { 
                    Date date = архив.создан().getValue(); 
                    return date != null ? DTF.format( date ) : null; 
                }, 
                архив.создан() ) );
            changed.textProperty().bind( Bindings.createStringBinding( () -> 
                { 
                    Date date = архив.изменен().getValue(); 
                    return date != null ? DTF.format( date ) : null; 
                }, 
                архив.изменен() ) );
        }
        this.архив = архив;
    }
    
}
