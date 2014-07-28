package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.Процессор;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.ArrayList;
import java.util.Collection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров процессора.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class PropertiesProcessorController implements Builder<TabPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( PropertiesProcessorController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/PropertiesProcessor.css";
    private static final String CSS_CLASS = "properties-processor";

    private final Collection<AttributeAgent> agents;

    private volatile Процессор процессор;
    
    @FXML TabProcessorController processorController;
    @FXML TabElementController elementController;

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
        
        Tab proc = new Tab( LOGGER.text( "properties.processor.tab.processor" ) );
        proc.setClosable( false );
        proc.setContent( processorController.build() );
        
        Tab elem = new Tab( LOGGER.text( "properties.common.tab.element" ) );
        elem.setClosable( false );
        elem.setContent( elementController.build() );
        
//        Tab ext = new Tab( LOGGER.text( "properties.common.tab.external" ) );
//        ext.setClosable( false );
        
        TabPane pane = new TabPane();
        pane.getTabs().addAll( proc, elem );//, ext );
        
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
    }
    
    @FXML
    void onActionApply( ActionEvent event )
    {
        //applied.setValue( false );
        JavaFX.getInstance().execute( new ScreenToStorageTask( процессор, agents ) );
    }

    void reset( Процессор процессор )
    {
        this.процессор = процессор;
        processorController.reset( процессор );
        elementController.reset( процессор );
        JavaFX.getInstance().execute( new StorageToScreenTask( процессор, agents ) );
    }
    
}
