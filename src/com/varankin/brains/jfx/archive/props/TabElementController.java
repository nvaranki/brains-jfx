package com.varankin.brains.jfx.archive.props;

import com.varankin.brains.db.DbЭлемент;
import com.varankin.brains.jfx.db.FxЭлемент;
import com.varankin.util.LoggerX;
import java.util.Collection;

import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки общих параметров.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
public class TabElementController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabElementController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabElement.css";
    private static final String CSS_CLASS = "properties-tab-element";

    static final String RESOURCE_FXML = "/fxml/archive/TabElement.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxЭлемент<? extends DbЭлемент> элемент;
    
    @FXML private Label path;
    @FXML private TextField name;
    @FXML private ListView<String> assembly;

    /**
     * Создает панель выбора и установки общих параметров.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель выбора и установки параметров.
     */
    @Override
    public GridPane build()
    {
        path = new Label();
        
        name = new TextField();
        name.setId( "name" );
        name.setFocusTraversable( true );
        
        assembly = new ListView<>();
        assembly.setId( "assembly" );
        assembly.setEditable( false ); //TODO
        
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setMinWidth( 90 );
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow( Priority.ALWAYS );
        
        RowConstraints rc0 = new RowConstraints();
        RowConstraints rc1 = new RowConstraints();
        RowConstraints rc2 = new RowConstraints();
        rc2.setMaxHeight( 100 );
        
        GridPane pane = new GridPane();
        pane.setId( "element" );
        pane.getColumnConstraints().addAll( cc0, cc1 );
        pane.getRowConstraints().addAll( rc0, rc1, rc2 );
        pane.add( new Label( LOGGER.text( "properties.element.path" ) ), 0, 0 );
        pane.add( path, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.element.name" ) ), 0, 1 );
        pane.add( name, 1, 1 );
        pane.add( new Label( LOGGER.text( "properties.assembly.name" ) ), 0, 2 );
        pane.add( assembly, 1, 2 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
        assembly.getSelectionModel().setSelectionMode( SelectionMode.SINGLE ); //TODO
        assembly.setItems( FXCollections.observableArrayList() ); //TODO
    }
    
    void set( FxЭлемент<?> элемент )
    {
        if( this.элемент != null )
        {
            name.textProperty().unbindBidirectional( this.элемент.название() );
            path.textProperty().unbind();
            assembly.itemsProperty().getValue().clear();
        }
        if( элемент != null )
        {
            name.textProperty().bindBidirectional( элемент.название() );
            path.textProperty().bind( элемент.положение() );
            Collection<String> c = элемент.сборки().getValue();
            if( c != null )
                assembly.itemsProperty().getValue().setAll( c ); //TODO
        }
        this.элемент = элемент;
    }
    
}
