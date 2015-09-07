package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.Параметр;
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
public class PropertiesParameterJavaController implements Builder<TabPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( PropertiesParameterJavaController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/PropertiesParameterJava.css";
    private static final String CSS_CLASS = "properties-parameter-java";

    private final Collection<AttributeAgent> agents;

    private volatile Параметр параметр;
    
    @FXML TabScalarController codeController;
    @FXML TabElementController elementController;

    public PropertiesParameterJavaController()
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
        codeController = new TabScalarController();
        elementController = new TabElementController();
        
        Tab tabParameter = new Tab( LOGGER.text( "properties.parameter.tab.code" ) );
        tabParameter.setClosable( false );
        tabParameter.setContent( codeController.build() );
        
        Tab tabElement = new Tab( LOGGER.text( "properties.common.tab.element" ) );
        tabElement.setClosable( false );
        tabElement.setContent( elementController.build() );
        
        TabPane pane = new TabPane();
        pane.getTabs().addAll( tabParameter, tabElement );
        
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
//        codeController.classNameProperty().bind( elementController.nameProperty() );
    }
    
    @FXML
    void onActionApply( ActionEvent event )
    {
        //applied.setValue( false );
        JavaFX.getInstance().execute( new ScreenToStorageTask( параметр, agents ) );
    }

    void reset( Параметр параметр )
    {
        this.параметр = параметр;
//        codeController.reset( параметр );
        elementController.reset( параметр );
        JavaFX.getInstance().execute( new StorageToScreenTask( параметр, agents ) );
    }
    
}
