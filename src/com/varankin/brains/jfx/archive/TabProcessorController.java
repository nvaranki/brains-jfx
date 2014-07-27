package com.varankin.brains.jfx.archive;

import com.varankin.brains.artificial.ПроцессорРасчета.Стратегия;
import com.varankin.brains.db.Процессор;
import com.varankin.util.LoggerX;
import java.util.Arrays;
import java.util.Collection;
import javafx.beans.binding.Bindings;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров процессора.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class TabProcessorController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabProcessorController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabProcessorController.css";
    private static final String CSS_CLASS = "properties-tab-processor";

    private final LongProperty pauseProperty;
    private final AttributeAgent pauseAgent, restartAgent, strategyAgent;

    private Процессор процессор;
    
    @FXML private TextField pause;
    @FXML private CheckBox restart;
    @FXML private ComboBox<Стратегия> strategy;

    public TabProcessorController()
    {
        pauseProperty = new SimpleLongProperty();
        pauseAgent = new PauseAgent();
        restartAgent = new RestartAgent();
        strategyAgent = new StrategyAgent();
    }
    
    /**
     * Создает панель выбора и установки параметров процессора.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель выбора и установки параметров.
     */
    @Override
    public GridPane build()
    {
        pause = new TextField();
        pause.setFocusTraversable( true );
        
        restart = new CheckBox();
        restart.setFocusTraversable( true );
        
        strategy = new ComboBox<>( FXCollections.observableArrayList( Стратегия.values() ));
        strategy.setFocusTraversable( true );
        strategy.setEditable( false );
        strategy.setVisibleRowCount( Стратегия.values().length );
        strategy.setCellFactory( ( ListView<Стратегия> lv ) -> new ListCellСтратегия() );
        strategy.setButtonCell( new ListCellСтратегия() );
        
        GridPane pane = new GridPane();
        pane.setId( "processor" );
        pane.add( new Label( LOGGER.text( "properties.processor.pause" ) ), 0, 0 );
        pane.add( pause, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.processor.pause.ms" ) ), 2, 0 );
        pane.add( new Label( LOGGER.text( "properties.processor.restart" ) ), 0, 1 );
        pane.add( restart, 1, 1 );
        pane.add( new Label( LOGGER.text( "properties.processor.strategy" ) ), 0, 2 );
        pane.add( strategy, 1, 2 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
        pauseProperty.bind( Bindings.createLongBinding( () -> convertPause(), pause.textProperty() ) );
    }
    
    Collection<AttributeAgent> getAgents()
    {
        return Arrays.asList( pauseAgent, restartAgent, strategyAgent );
    }

    void reset( Процессор процессор )
    {
        this.процессор = процессор;
    }
    
    private Long convertPause()
    {
        try
        {
            return Long.valueOf( pause.getText() );
        }
        catch( NumberFormatException | NullPointerException ex )
        {
            return 0L;
        }
    }
    
    private static class ListCellСтратегия extends ListCell<Стратегия>
    {
        @Override
        public void updateItem( Стратегия item, boolean empty ) 
        {
            super.updateItem( item, empty );
            setText( empty ? null : LOGGER.text( "properties.processor.strategy." + item.ordinal() ) );
        }
    }
    
    private class PauseAgent implements AttributeAgent
    {
        volatile long пауза;

        @Override
        public void fromScreen()
        {
            пауза = pauseProperty.get();
        }
        
        @Override
        public void toScreen()
        {
            pause.setText( Long.toString( пауза ) );
        }
        
        @Override
        public void fromStorage()
        {
            пауза = процессор.пауза();
        }
        
        @Override
        public void toStorage()
        {
            процессор.пауза( пауза );
        }

    }
    
    private class RestartAgent implements AttributeAgent
    {
        volatile boolean рестарт;

        @Override
        public void fromScreen()
        {
            рестарт = restart.selectedProperty().get();
        }
        
        @Override
        public void toScreen()
        {
            restart.setSelected( рестарт );
        }
        
        @Override
        public void fromStorage()
        {
            рестарт = процессор.рестарт();
        }
        
        @Override
        public void toStorage()
        {
            процессор.рестарт( рестарт );
        }

    }
    
    private class StrategyAgent implements AttributeAgent
    {
        volatile Стратегия стратегия;

        @Override
        public void fromScreen()
        {
            стратегия = strategy.getValue();
        }
        
        @Override
        public void toScreen()
        {
            strategy.getSelectionModel().select( стратегия );
        }
        
        @Override
        public void fromStorage()
        {
            стратегия = процессор.стратегия();
        }
        
        @Override
        public void toStorage()
        {
            процессор.стратегия( стратегия );
        }

    }
    
}
