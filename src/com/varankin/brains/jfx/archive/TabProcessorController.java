package com.varankin.brains.jfx.archive;

import com.varankin.brains.artificial.ПроцессорРасчета.Стратегия;
import com.varankin.brains.db.DbПроцессор;
import com.varankin.util.LoggerX;
import java.util.Arrays;
import java.util.Collection;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
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
    private static final String RESOURCE_CSS  = "/fxml/archive/TabProcessor.css";
    private static final String CSS_CLASS = "properties-tab-processor";

    private final LongProperty pauseProperty, delayProperty;
    private final IntegerProperty inlineProperty;
    private final AttributeAgent pauseAgent, restartAgent, strategyAgent;
    private final AttributeAgent delayAgent, inlineAgent, purgeAgent, collapseAgent;

    private DbПроцессор процессор;
    
    @FXML private TextField pause;
    @FXML private TextField delay;
    @FXML private TextField inline;
    @FXML private CheckBox restart;
    @FXML private CheckBox purge;
    @FXML private CheckBox collapse;
    @FXML private ComboBox<Стратегия> strategy;

    public TabProcessorController()
    {
        pauseProperty = new SimpleLongProperty();
        delayProperty = new SimpleLongProperty();
        inlineProperty = new SimpleIntegerProperty();
        pauseAgent = new PauseAgent();
        restartAgent = new RestartAgent();
        strategyAgent = new StrategyAgent();
        delayAgent = new DelayAgent();
        inlineAgent = new InlineAgent();
        purgeAgent = new PurgeAgent();
        collapseAgent = new CollapseAgent();
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
        
        delay = new TextField();
        delay.setFocusTraversable( true );
        
        inline = new TextField();
        inline.setFocusTraversable( true );
        
        restart = new CheckBox();
        restart.setFocusTraversable( true );
        
        purge = new CheckBox();
        purge.setFocusTraversable( true );
        
        collapse = new CheckBox();
        collapse.setFocusTraversable( true );
        
        strategy = new ComboBox<>();
        strategy.setFocusTraversable( true );
        strategy.setEditable( false );
        
        GridPane pane = new GridPane();
        pane.setId( "processor" );
        pane.add( new Label( LOGGER.text( "properties.processor.pause" ) ), 0, 0 );
        pane.add( new Label( LOGGER.text( "properties.processor.pause.ms" ) ), 2, 0 );
        pane.add( pause, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.processor.restart" ) ), 0, 1 );
        pane.add( restart, 1, 1 );
        pane.add( new Label( LOGGER.text( "properties.processor.strategy" ) ), 0, 2 );
        pane.add( strategy, 1, 2 );
        pane.add( new Label( LOGGER.text( "properties.processor.purge" ) ), 0, 3 );
        pane.add( purge, 1, 3 );
        pane.add( new Label( LOGGER.text( "properties.processor.collapse" ) ), 0, 4 );
        pane.add( collapse, 1, 4 );
        pane.add( new Label( LOGGER.text( "properties.processor.delay.ns" ) ), 2, 5 );
        pane.add( new Label( LOGGER.text( "properties.processor.delay" ) ), 0, 5 );
        pane.add( delay, 1, 5 );
        pane.add( new Label( LOGGER.text( "properties.processor.inline" ) ), 0, 6 );
        pane.add( inline, 1, 6 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
        strategy.getItems().addAll( Стратегия.values() );
        strategy.setVisibleRowCount( Стратегия.values().length );
        strategy.setCellFactory( ( ListView<Стратегия> lv ) -> new ListCellСтратегия() );
        strategy.setButtonCell( new ListCellСтратегия() );
        pauseProperty.bind( Bindings.createLongBinding( () -> convertLong( pause ), pause.textProperty() ) );
        delayProperty.bind( Bindings.createLongBinding( () -> convertLong( delay ), delay.textProperty() ) );
        inlineProperty.bind( Bindings.createIntegerBinding( () -> convertInteger( inline ), inline.textProperty() ) );
    }
    
    Collection<AttributeAgent> getAgents()
    {
        return Arrays.asList( pauseAgent, restartAgent, strategyAgent, delayAgent, inlineAgent, purgeAgent, collapseAgent );
    }

    void reset( DbПроцессор процессор )
    {
        this.процессор = процессор;
    }
    
    private static Long convertLong( TextField field )
    {
        try
        {
            return Long.valueOf( field.getText() );
        }
        catch( NumberFormatException | NullPointerException ex )
        {
            return 0L;
        }
    }
    
    private static Integer convertInteger( TextField field )
    {
        try
        {
            return Integer.valueOf( field.getText() );
        }
        catch( NumberFormatException | NullPointerException ex )
        {
            return 0;
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
        volatile long значение;

        @Override
        public void fromScreen()
        {
            значение = pauseProperty.get();
        }
        
        @Override
        public void toScreen()
        {
            pause.setText( Long.toString( значение ) );
        }
        
        @Override
        public void fromStorage()
        {
            значение = процессор.пауза();
        }
        
        @Override
        public void toStorage()
        {
            процессор.пауза( значение );
        }

    }
    
    private class DelayAgent implements AttributeAgent
    {
        volatile long значение;

        @Override
        public void fromScreen()
        {
            значение = delayProperty.get();
        }
        
        @Override
        public void toScreen()
        {
            delay.setText( Long.toString( значение ) );
        }
        
        @Override
        public void fromStorage()
        {
            значение = процессор.задержка();
        }
        
        @Override
        public void toStorage()
        {
            процессор.задержка( значение );
        }

    }
    
    private class InlineAgent implements AttributeAgent
    {
        volatile int значение;

        @Override
        public void fromScreen()
        {
            значение = inlineProperty.get();
        }
        
        @Override
        public void toScreen()
        {
            inline.setText( Integer.toString( значение ) );
        }
        
        @Override
        public void fromStorage()
        {
            значение = процессор.накопление();
        }
        
        @Override
        public void toStorage()
        {
            процессор.накопление( значение );
        }

    }
    
    private class RestartAgent implements AttributeAgent
    {
        volatile boolean значение;

        @Override
        public void fromScreen()
        {
            значение = restart.selectedProperty().get();
        }
        
        @Override
        public void toScreen()
        {
            restart.setSelected(значение );
        }
        
        @Override
        public void fromStorage()
        {
            значение = процессор.рестарт();
        }
        
        @Override
        public void toStorage()
        {
            процессор.рестарт(значение );
        }

    }
    
    private class PurgeAgent implements AttributeAgent
    {
        volatile boolean значение;

        @Override
        public void fromScreen()
        {
            значение = purge.selectedProperty().get();
        }
        
        @Override
        public void toScreen()
        {
            purge.setSelected( значение );
        }
        
        @Override
        public void fromStorage()
        {
            значение = процессор.очистка();
        }
        
        @Override
        public void toStorage()
        {
            процессор.очистка( значение );
        }

    }
    
    private class CollapseAgent implements AttributeAgent
    {
        volatile boolean значение;

        @Override
        public void fromScreen()
        {
            значение = collapse.selectedProperty().get();
        }
        
        @Override
        public void toScreen()
        {
            collapse.setSelected( значение );
        }
        
        @Override
        public void fromStorage()
        {
            значение = процессор.сжатие();
        }
        
        @Override
        public void toStorage()
        {
            процессор.сжатие( значение );
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
