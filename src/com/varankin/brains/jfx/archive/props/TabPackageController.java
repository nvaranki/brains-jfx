package com.varankin.brains.jfx.archive.props;

import com.varankin.brains.jfx.db.FxПакет;
import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Builder;

/**
 * FXML-контроллер панели установки параметров пакета.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
public class TabPackageController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabPackageController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabPackage.css";
    private static final String CSS_CLASS = "properties-tab-package";

    static final String RESOURCE_FXML = "/fxml/archive/TabPackage.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxПакет пакет;
    
    @FXML private TextField version, name;
    
    /**
     * Создает панель установки параметров.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель установки параметров.
     */
    @Override
    public GridPane build()
    {
        name = new TextField();
        name.setId( "name" );
        name.setFocusTraversable( true );
        
        version = new TextField();
        version.setId( "version" );
        version.setFocusTraversable( true );
        version.setEditable( false );
        
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setMinWidth( 90 );
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow( Priority.ALWAYS );
        
        GridPane pane = new GridPane();
        pane.getColumnConstraints().addAll( cc0, cc1 );
        pane.add( new Label( LOGGER.text( "tab.package.name" ) ), 0, 0 );
        pane.add( name, 1, 0 );
        pane.add( new Label( LOGGER.text( "tab.package.version" ) ), 0, 1 );
        pane.add( version, 1, 1 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
    }
            
    void set( FxПакет пакет )
    {
        if( this.пакет != null )
        {
            name.textProperty().unbindBidirectional( this.пакет.название() );
            version.textProperty().unbind();
        }
        if( пакет != null )
        {
            name.textProperty().bindBidirectional( пакет.название() );
            version.textProperty().bind( пакет.версия() );
        }
        this.пакет = пакет;
    }
}
