package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.db.FxАрхив;
import com.varankin.util.LoggerX;

import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров архива.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class TabArchiveController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabArchiveController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabArchive.css";
    private static final String CSS_CLASS = "properties-tab-archive";

    static final String RESOURCE_FXML = "/fxml/archive/TabArchive.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxАрхив архив;
    
    @FXML private TextField uri;

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
        uri.setPrefColumnCount( 24 );
        uri.setEditable( false );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "properties.ns.uri" ) ), 0, 0 );
        pane.add( uri, 1, 0 );
        
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
        }
        if( архив != null )
        {
            uri.textProperty().bind( new SimpleStringProperty()/*TODO архив.uri()*/ );
        }
        this.архив = архив;
    }
    
}
