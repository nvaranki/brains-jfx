package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.db.FxКлассJava;
import com.varankin.util.LoggerX;

import java.util.ArrayList;
import java.util.Collection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели закладок для установки параметров класса Java.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class PropertiesClassJavaController implements Builder<TabPane>  
{
    private static final LoggerX LOGGER = LoggerX.getLogger( PropertiesClassJavaController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/PropertiesClassJava.css";
    private static final String CSS_CLASS = "properties-class";

    private final Collection<AttributeAgent> agents;

    private volatile FxКлассJava класс;
    
    @FXML TabClassJavaController classController;
    @FXML TabElementController elementController;
    @FXML TabAttrsController attrsController;

    public PropertiesClassJavaController()
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
        classController = new TabClassJavaController();
        elementController = new TabElementController();
        attrsController = new TabAttrsController();
        
        Tab cls = new Tab( LOGGER.text( "properties.tab.class.title" ) );
        cls.setClosable( false );
        cls.setContent( classController.build() );
        
        Tab elem = new Tab( LOGGER.text( "properties.common.tab.element" ) );
        elem.setClosable( false );
        elem.setContent( elementController.build() );
        
        Tab attrs = new Tab( LOGGER.text( "properties.tab.attrs.title" ) );
        attrs.setClosable( false );
        attrs.setContent( attrsController.build() );
        
        TabPane pane = new TabPane();
        pane.getTabs().addAll( cls, elem, attrs );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
        agents.addAll( classController.getAgents() );
    }

    @FXML
    void onActionApply( ActionEvent event )
    {
        //applied.setValue( false );
        new ScreenToStorageTask( класс.getSource(), agents ).run();
    }

    void reset( FxКлассJava класс )
    {
        this.класс = класс;
        classController.reset( класс );
        attrsController.set( класс );
        elementController.set( класс );
        JavaFX.getInstance().execute( new StorageToScreenTask( класс.getSource(), agents ) );
    }
    
}
