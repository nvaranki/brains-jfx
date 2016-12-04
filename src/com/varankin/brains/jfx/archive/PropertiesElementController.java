package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.db.FxЭлемент;
import com.varankin.util.LoggerX;
import java.util.ArrayList;
import java.util.Collection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров элемента.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class PropertiesElementController implements Builder<TabPane> 
{
    private static final LoggerX LOGGER = LoggerX.getLogger( PropertiesElementController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/PropertiesElement.css";
    private static final String CSS_CLASS = "properties-element";

    private final Collection<AttributeAgent> agents;

    private volatile FxЭлемент элемент;
    
    @FXML TabElementController elementController;
    @FXML TabAttrsController attrsController;

    public PropertiesElementController()
    {
        agents = new ArrayList<>();
    }

    /**
     * Создает панель выбора и установки параметров процессора.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель выбора и установки параметров.
     */
    @Override
    public TabPane build()
    {
        elementController = new TabElementController();
        attrsController = new TabAttrsController();
        
        Tab elem = new Tab( LOGGER.text( "properties.common.tab.element" ) );
        elem.setClosable( false );
        elem.setContent( elementController.build() );
        
        Tab attrs = new Tab( LOGGER.text( "properties.tab.attrs.title" ) );
        attrs.setClosable( false );
        attrs.setContent( attrsController.build() );
        
        TabPane pane = new TabPane();
        pane.getTabs().addAll( elem, attrs );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
    }

    @FXML
    public void onActionApply( ActionEvent event )
    {
        //applied.setValue( false );
        new ScreenToStorageTask( элемент.getSource(), agents ).run();
    }
    
    //@Override
    public void reset( FxЭлемент элемент )
    {
        this.элемент = элемент;
        elementController.set( элемент );
        attrsController.set( элемент );
        JavaFX.getInstance().execute( new StorageToScreenTask( элемент.getSource(), agents ) );
    }
    
}
