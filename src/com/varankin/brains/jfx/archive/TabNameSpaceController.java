package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.db.FxNameSpace;
import com.varankin.util.LoggerX;

import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров объекта пространства имен XML.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class TabNameSpaceController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabNameSpaceController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabNameSpace.css";
    private static final String CSS_CLASS = "properties-xml-ns";

    static final String RESOURCE_FXML = "/fxml/archive/TabNameSpace.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxNameSpace xmlNameSpace;
    
    @FXML private TextField prefix;
    @FXML private TextField uri;

    /**
     * Создает панель выбора и установки параметров объекта пространства имен XML.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель выбора и установки параметров.
     */
    @Override
    public GridPane build()
    {
        prefix = new TextField();
        prefix.setId( "prefix" );
        prefix.setPrefColumnCount( 6 );
        
        uri = new TextField();
        uri.setId( "uri" );
        uri.setPrefColumnCount( 24 );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "properties.ns.prefix" ) ), 0, 0 );
        pane.add( prefix, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.ns.uri" ) ), 0, 1 );
        pane.add( uri, 1, 1 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
    }
    
    void set( FxNameSpace xmlNameSpace )
    {
        if( this.xmlNameSpace != null )
        {
            prefix.textProperty().unbindBidirectional( this.xmlNameSpace.название() );
            uri.textProperty().unbindBidirectional( this.xmlNameSpace.uri() );
        }
        if( xmlNameSpace != null )
        {
            prefix.textProperty().bindBidirectional( xmlNameSpace.название() );
            uri.textProperty().bindBidirectional( xmlNameSpace.uri() );
        }
        this.xmlNameSpace = xmlNameSpace;
    }
    
}
