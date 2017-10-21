package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.db.FxNameSpace;
import com.varankin.util.LoggerX;

import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров объекта пространства имен XML.
 * 
 * @author &copy; 2017 Николай Варанкин
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
    @FXML private ListView<String> variants;

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
        
        variants = new ListView<>();
        variants.setId( "variants" );
        variants.setEditable( false );
        
        uri = new TextField();
        uri.setId( "uri" );
        uri.setPrefColumnCount( 24 );
        
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setMinWidth( 90 );
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow( Priority.ALWAYS );
        
        RowConstraints rc0 = new RowConstraints();
        RowConstraints rc1 = new RowConstraints();
        RowConstraints rc2 = new RowConstraints();
        rc2.setMaxHeight( 100 );
        
        GridPane pane = new GridPane();
        pane.getColumnConstraints().addAll( cc0, cc1 );
        pane.getRowConstraints().addAll( rc0, rc1, rc2 );
        pane.add( new Label( LOGGER.text( "properties.ns.uri" ) ), 0, 0 );
        pane.add( uri, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.ns.prefix" ) ), 0, 1 );
        pane.add( prefix, 1, 1 );
        pane.add( new Label( LOGGER.text( "properties.ns.variants" ) ), 0, 2 );
        pane.add( variants, 1, 2 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
        variants.getSelectionModel().setSelectionMode( SelectionMode.SINGLE ); //TODO
        variants.setItems( FXCollections.observableArrayList() ); //TODO
    }
    
    void set( FxNameSpace xmlNameSpace )
    {
        if( this.xmlNameSpace != null )
        {
            prefix.textProperty().unbindBidirectional( this.xmlNameSpace.название() );
            uri.textProperty().unbindBidirectional( this.xmlNameSpace.uri() );
            variants.itemsProperty().unbind();
        }
        if( xmlNameSpace != null )
        {
            prefix.textProperty().bindBidirectional( xmlNameSpace.название() );
            uri.textProperty().bindBidirectional( xmlNameSpace.uri() );
            variants.itemsProperty().bind( Bindings.createObjectBinding( 
                    () -> FXCollections.observableArrayList( xmlNameSpace.варианты().getValue() ), 
                    xmlNameSpace.варианты(), xmlNameSpace.название() ) );//getValue().setAll( c ); //TODO
        }
        this.xmlNameSpace = xmlNameSpace;
    }
    
}
