package com.varankin.brains.jfx.archive;

import com.varankin.brains.artificial.ПроцессорРасчета.Стратегия;
import com.varankin.brains.jfx.db.FxПроцессор;
import com.varankin.util.LoggerX;

import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;
import javafx.util.StringConverter;

/**
 * FXML-контроллер панели выбора и установки параметров процессора.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
public final class TabProcessorController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabProcessorController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabProcessor.css";
    private static final String CSS_CLASS = "properties-tab-processor";
    private static final StringConverter<Long> CONVERTER_LONG 
            = new ToStringConverter<>( s -> Long.valueOf( s ) );
    private static final StringConverter<Integer> CONVERTER_INTEGER 
            = new ToStringConverter<>( s -> Integer.valueOf( s ) );

    static final String RESOURCE_FXML = "/fxml/archive/TabProcessor.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxПроцессор процессор;
    private IndeterminateHelper restartHelper, purgeHelper, collapseHelper;
    
    @FXML private TextField pause;
    @FXML private TextField delay;
    @FXML private TextField inline;
    @FXML private CheckBox restart;
    @FXML private CheckBox purge;
    @FXML private CheckBox collapse;
    @FXML private ChoiceBox<Стратегия> strategy;

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
        restart.setAllowIndeterminate( true );
        
        purge = new CheckBox();
        purge.setFocusTraversable( true );
        purge.setAllowIndeterminate( true );
        
        collapse = new CheckBox();
        collapse.setFocusTraversable( true );
        collapse.setAllowIndeterminate( true );
        
        strategy = new ChoiceBox<>();
        strategy.setFocusTraversable( true );
        
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
        strategy.getItems().add( 0, null );
        strategy.setConverter( new StringToEnumConverter<>( 
                Стратегия.values(), "properties.tab.processor.strategy." ) );
    }
    
    void set( FxПроцессор процессор )
    {
        if( this.процессор != null )
        {
            pause.textProperty().unbindBidirectional( this.процессор.пауза() );
            delay.textProperty().unbindBidirectional( this.процессор.задержка());
            inline.textProperty().unbindBidirectional( this.процессор.накопление() );
            restart.selectedProperty().unbindBidirectional( this.процессор.рестарт() );
            purge.selectedProperty().unbindBidirectional( this.процессор.очистка() );
            collapse.selectedProperty().unbindBidirectional( this.процессор.сжатие() );
            strategy.valueProperty().unbindBidirectional( this.процессор.стратегия() );
            restartHelper.removeListeners();
            purgeHelper.removeListeners();
            collapseHelper.removeListeners();
        }
        if( процессор != null )
        {
            pause.textProperty().bindBidirectional( процессор.пауза(), CONVERTER_LONG );
            delay.textProperty().bindBidirectional( процессор.задержка(), CONVERTER_LONG );
            inline.textProperty().bindBidirectional( процессор.накопление(), CONVERTER_INTEGER );
            restart.selectedProperty().bindBidirectional( процессор.рестарт() );
            purge.selectedProperty().bindBidirectional( процессор.очистка() );
            collapse.selectedProperty().bindBidirectional( процессор.сжатие() );
            strategy.valueProperty().bindBidirectional( процессор.стратегия() );
            restartHelper = new IndeterminateHelper( restart.indeterminateProperty(), процессор.рестарт() );
            purgeHelper = new IndeterminateHelper( purge.indeterminateProperty(), процессор.очистка() );
            collapseHelper = new IndeterminateHelper( collapse.indeterminateProperty(), процессор.сжатие() );
        }
        this.процессор = процессор;
    }
    
}
