package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.КлассJava;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.ArrayList;
import java.util.Collection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Builder;

/**
 * FXML-контроллер панели закладок для установки параметров класса Java.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class PropertiesClassJavaController implements Builder<TabPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( PropertiesClassJavaController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/PropertiesClassJava.css";
    private static final String CSS_CLASS = "properties-class-java";

    private final Collection<AttributeAgent> agents;

    private volatile КлассJava класс;
    
    @FXML TabClassJavaController codeController;
    @FXML TabElementController elementController;

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
        codeController = new TabClassJavaController();
        elementController = new TabElementController();
        
        Tab proc = new Tab( LOGGER.text( "properties.class.tab.code" ) );
        proc.setClosable( false );
        proc.setContent( codeController.build() );
        
        Tab elem = new Tab( LOGGER.text( "properties.common.tab.element" ) );
        elem.setClosable( false );
        elem.setContent( elementController.build() );
        
        TabPane pane = new TabPane();
        pane.getTabs().addAll( proc, elem );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
        agents.addAll( codeController.getAgents() );
        agents.addAll( elementController.getAgents() );
        codeController.classNameProperty().bind( elementController.nameProperty() );
    }
    
    @FXML
    void onActionApply( ActionEvent event )
    {
        //applied.setValue( false );
        JavaFX.getInstance().execute( new ScreenToStorageTask( класс, agents ) );
    }

    void reset( КлассJava класс )
    {
        this.класс = класс;
        codeController.reset( класс );
        elementController.reset( класс );
        JavaFX.getInstance().execute( new StorageToScreenTask( класс, agents ) );
    }
    
}
