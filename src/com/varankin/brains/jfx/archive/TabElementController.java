package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.DbЭлемент;
import com.varankin.brains.jfx.db.FxЭлемент;
import com.varankin.util.LoggerX;

import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки общих параметров.
 * 
 * @author &copy; 2016 Николай Варанкин
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
        name.setFocusTraversable( true );
        name.setId( "name" );
        
        GridPane pane = new GridPane();
        pane.setId( "element" );
        pane.add( new Label( LOGGER.text( "properties.element.path" ) ), 0, 0 );
        pane.add( path, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.element.name" ) ), 0, 1 );
        pane.add( name, 1, 1 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
    }
    
    void set( FxЭлемент<?> элемент )
    {
        if( this.элемент != null )
        {
            name.textProperty().unbindBidirectional( this.элемент.название() );
            path.textProperty().unbind();
        }
        if( элемент != null )
        {
            name.textProperty().bindBidirectional( элемент.название() );
            path.textProperty().bind( элемент.положение() );
        }
        this.элемент = элемент;
    }
    
}
