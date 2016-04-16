package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.browser.BrowserTreeCell;
import com.varankin.characteristic.Свойственный;
import com.varankin.util.LoggerX;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.WeakListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.StageStyle;
import javafx.util.Builder;

/**
 * FXML-контроллер графика динамического изменения значений по времени.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class TimeLineController implements Builder<Pane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TimeLineController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/TimeLine.css";
    private static final String CSS_CLASS = "time-line";
    
    static final String RESOURCE_FXML  = "/fxml/analyser/TimeLine.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();
    
    private final FlowController flowController;
    private final ChangeListener<Parent> lifeCycleListener;
    private final ListChangeListener<Value> legendValuesListener;

    private ObservableSetupStage properties;
    
    @FXML private Pane timeRuler;
    @FXML private TimeRulerController timeRulerController;
    @FXML private Pane valueRuler;
    @FXML private ValueRulerController valueRulerController;
    @FXML private Pane graph;
    @FXML private GraphPaneController graphController;
    @FXML private Pane legend;
    @FXML private LegendPaneController legendController;
    @FXML private ContextMenu popup;
    @FXML private GridPane pane;

    public TimeLineController(  )
    {
        flowController = new FlowController();
        lifeCycleListener = new LifeCycleListener<>();
        legendValuesListener = this::onValueAddRemove;
    }

    /**
     * Создает график динамического изменения значений по времени. 
     * Применяется в конфигурации без FXML.
     * 
     * @return график.
     */
    @Override
    public GridPane build()
    {
        legendController = new LegendPaneController();
        graphController = new GraphPaneController();
        timeRulerController = new TimeRulerController();
        valueRulerController = new ValueRulerController();

        timeRuler = timeRulerController.build();
        valueRuler = valueRulerController.build();
        graph = graphController.build();
        legend = legendController.build();

        graph.setOnDragOver( this::onDragOver );
        graph.setOnDragDropped( this::onDragDropped );

        legend.setOnDragOver( this::onDragOver );
        legend.setOnDragDropped( this::onDragDropped );

        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setMinWidth( 45d );
        cc0.setHgrow( Priority.NEVER );
        cc0.setHalignment( HPos.RIGHT );
        
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setMinWidth( 100 );
        cc1.setHgrow( Priority.ALWAYS );
        cc1.setHalignment( HPos.LEFT );
        
        RowConstraints rc0 = new RowConstraints();
        rc0.setMinHeight( 100 );
        rc0.setVgrow( Priority.ALWAYS );
        rc0.setValignment( VPos.BOTTOM );
        
        RowConstraints rc1 = new RowConstraints();
        rc1.setMinHeight( 25d );
        rc1.setVgrow( Priority.NEVER );
        rc1.setValignment( VPos.TOP );

        RowConstraints rc2 = new RowConstraints();
        rc2.setFillHeight( false );
        rc2.setVgrow( Priority.NEVER );
        
        pane = new GridPane();
        pane.getColumnConstraints().addAll( cc0, cc1 );
        pane.getRowConstraints().addAll( rc0, rc1, rc2 );
        pane.add( valueRuler, 0, 0 );
        pane.add( timeRuler, 1, 1 );
        pane.add( graph, 1, 0 );
        pane.add( legend, 0, 2, 2, 1 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        MenuItem menuItemRemove = new MenuItem( LOGGER.text( "timeline.popup.remove" ) );
        menuItemRemove.setGraphic( JavaFX.icon( "icons16x16/remove.png" ) );
        menuItemRemove.setOnAction( this::onActionRemove );
        
        popup = new ContextMenu();
        popup.getItems().add( menuItemRemove );
        
        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
        pane.setOnContextMenuRequested( (ContextMenuEvent e) -> 
        {
            popup.show( pane, e.getScreenX(), e.getScreenY() );
            e.consume();
        });
        pane.parentProperty().addListener( new WeakChangeListener<>( lifeCycleListener ) );
        graphController.labelProperty().bindBidirectional( legendController.labelProperty() );
        legendController.valuesProperty().addListener( new WeakListChangeListener<>( legendValuesListener ) );
    }
    
    @FXML
    protected void onDragOver( DragEvent event )
    {
        Object gs = event.getGestureSource();
        if( gs instanceof Node && ((Node)gs).getUserData() instanceof Свойственный )
            event.acceptTransferModes( TransferMode.LINK );
        else 
            event.acceptTransferModes( TransferMode.NONE );
        event.consume();
    }
    
    @FXML
    protected void onDragDropped( DragEvent event )
    {
        Object gs = event.getGestureSource();
        if( gs instanceof BrowserTreeCell )
        {
            BrowserTreeCell btc = (BrowserTreeCell)gs;
            Object userData = btc.getUserData();
            if( userData instanceof Свойственный )
                Platform.runLater( () -> showValueOptions( new Value( (Свойственный)userData, btc.getText() ) ) );
            event.setDropCompleted( true );
        }            
        else
        {
            event.setDropCompleted( false );
        }
        event.consume();
    }
    
   @FXML
    protected void onContextMenuRequested( ContextMenuEvent event )
    {
        popup.show( pane, event.getScreenX(), event.getScreenY() );
        event.consume();
    }
    
    /**
     * Действие по удалению графика с экрана.
     * @param e
     */
    @FXML
    protected void onActionRemove( ActionEvent e )
    {
        Parent parent = pane.getParent();
        if( parent instanceof Pane )
        {
            // убрать с экрана
            Pane pp = (Pane)parent;
            pp.getChildren().remove( pane );
        }
        else
        {
            LOGGER.log( "001001001W" );
        }
    }
    
    /**
     * Управляет связью отображаемого элемента с графиком и шкалами. 
     * 
     * @param c изменения списка элементов.
     */
    void onValueAddRemove( ListChangeListener.Change<? extends Value> c )
    {
        while( c.next() )
        {
            if( c.wasPermutated() )
            {
                LOGGER.getLogger().fine( "Permutated list is unsupported." );
            }
            else if( c.wasUpdated() )
            {
                LOGGER.getLogger().fine( "Updated list is unsupported." );
            }
            else
            {
                for( Value value : c.getRemoved() )
                {
                    DotPainter painter = value.painterProperty().getValue();
                    painter.valueConvertorProperty().unbind();
                    painter.timeConvertorProperty().unbind();
                    painter.writableImageProperty().unbind();
                }
                for( Value value : c.getAddedSubList() )
                {
                    DotPainter painter = value.painterProperty().getValue();
                    painter.valueConvertorProperty().bind( valueRulerController.convertorProperty() );
                    painter.timeConvertorProperty().bind( timeRulerController.convertorProperty() );
                    painter.writableImageProperty().bind( graphController.writableImageProperty() );
                }
            }
        }
    }
    
    void reset( TimeLineSetupController controller )
    {
        valueRulerController.applyProperties( controller.valueRulerController() );
        timeRulerController.applyProperties( controller.timeRulerController() );
        graphController.applyProperties( controller.graphAreaController() );
        legendController.applyProperties( controller.graphAreaController() );
    }

    void extendPopupMenu( List<? extends MenuItem> parentPopupMenu )
    {
        List<MenuItem> popupItems = popup.getItems();
        JavaFX.copyMenuItems( parentPopupMenu, popupItems, true );
        graphController.extendPopupMenu( popupItems );
        valueRulerController.extendPopupMenu( popupItems );
        timeRulerController.extendPopupMenu( popupItems );
        legendController.extendPopupMenu( popupItems );
    }
    
    void addFlowListenerTo( BooleanProperty dynamicProperty, boolean logic )
    {
        flowController.logic = logic;
        dynamicProperty.addListener( new WeakChangeListener<>( flowController ) );
    }
    
    private void showValueOptions( Value value )
    {
        if( properties == null )
        {
            properties = new ObservableSetupStage( legendController::addLegendItem );
            properties.initStyle( StageStyle.DECORATED );
            properties.initOwner( JavaFX.getInstance().платформа );
            properties.setTitle( LOGGER.text( "properties.observable.title" ) );
        }
        properties.getController().setValue( value );
        properties.show();
    }

    /**
     * Устанавливает зависимость {@link GraphPaneController#dynamicProperty() } 
     * от внешнего мажорного выключателя.
     */
    private class FlowController implements ChangeListener<Boolean>
    {
        boolean logic = true;
        boolean dynamic = true;

        @Override
        public void changed( ObservableValue<? extends Boolean> o, Boolean oldValue, Boolean newValue )
        {
            BooleanProperty dp = graphController.dynamicProperty();
            if( newValue != null )
            {
                boolean run = logic ? newValue : !newValue;
                if( run )
                {
                    // восстановить текущее или прежнее значение
                    dynamic |= dp.get();
                    dp.setValue( dynamic );
                }
                else
                {
                    // сохранить текущее значение и выключить
                    dynamic = dp.get();
                    dp.setValue( Boolean.FALSE );
                }
            }
        }

    }
    
    private class LifeCycleListener<T> implements ChangeListener<T>
    {
        @Override
        public void changed( ObservableValue<? extends T> __, T oldValue, T newValue )
        {
            BooleanProperty dynamicProperty = graphController.dynamicProperty();
            if( oldValue != null )
            {
                legendController.unitProperty().unbind();
                legendController.dynamicProperty().unbindBidirectional( dynamicProperty );
                timeRulerController.relativeProperty().unbindBidirectional( dynamicProperty );
                graphController.widthProperty().unbind();
                graphController.heightProperty().unbind();
                graphController.timeConvertorProperty().unbind();
                graphController.valueConvertorProperty().unbind();

                legendController.clear();
            }
            if( newValue != null )
            {
                legendController.unitProperty().bind( timeRulerController.unitProperty() );
                legendController.dynamicProperty().bindBidirectional( dynamicProperty );
                timeRulerController.relativeProperty().bindBidirectional( dynamicProperty );
                graphController.widthProperty().bind( timeRuler.widthProperty() );
                graphController.heightProperty().bind( valueRuler.heightProperty() );
                graphController.timeConvertorProperty().bind( timeRulerController.convertorProperty() );
                graphController.valueConvertorProperty().bind( valueRulerController.convertorProperty() );
            }
        }
    }

}
