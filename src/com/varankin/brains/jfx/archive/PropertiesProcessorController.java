package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.db.FxПроцессор;
import com.varankin.util.LoggerX;

import java.util.ArrayList;
import java.util.Collection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Builder;

/**
 * FXML-контроллер панели закладок для установки параметров процессора.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public class PropertiesProcessorController implements Builder<TabPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( PropertiesProcessorController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/PropertiesProcessor.css";
    private static final String CSS_CLASS = "properties-processor";

    private final Collection<AttributeAgent> agents;

    private volatile FxПроцессор процессор;
    
    @FXML TabProcessorController processorController;
    @FXML TabElementController elementController;
    @FXML TabAttrsController attrsController;

    public PropertiesProcessorController()
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
        processorController = new TabProcessorController();
        elementController = new TabElementController();
        attrsController = new TabAttrsController();
        
        Tab proc = new Tab( LOGGER.text( "properties.processor.tab.processor" ) );
        proc.setClosable( false );
        proc.setContent( processorController.build() );
        
        Tab elem = new Tab( LOGGER.text( "properties.common.tab.element" ) );
        elem.setClosable( false );
        elem.setContent( elementController.build() );
        
        Tab attrs = new Tab( LOGGER.text( "properties.tab.attrs.title" ) );
        attrs.setClosable( false );
        attrs.setContent( attrsController.build() );
        
        TabPane pane = new TabPane();
        pane.getTabs().addAll( proc, elem, attrs );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
        agents.addAll( processorController.getAgents() );
        agents.addAll( elementController.getAgents() );
        agents.addAll( attrsController.getAgents() );
    }
    
    @FXML
    void onActionApply( ActionEvent event )
    {
        //applied.setValue( false );
        new ScreenToStorageTask( процессор.getSource(), agents ).run();
    }

    void reset( FxПроцессор процессор )
    {
        this.процессор = процессор;
        processorController.reset( процессор.getSource() );
        elementController.reset( процессор );
        attrsController.reset( процессор );
        JavaFX.getInstance().execute( new StorageToScreenTask( процессор.getSource(), agents ) );
    }
    
}
