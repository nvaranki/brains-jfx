package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.property.PropertyMonitor;
import com.varankin.util.LoggerX;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;
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
import javafx.scene.paint.Color;
import javafx.util.Builder;

/**
 * FXML-контроллер графика динамического изменения значений по времени.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class TimeLineController implements Builder<Pane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TimeLineController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/TimeLine.css";
    private static final String CSS_CLASS = "time-line";
    
    static final String RESOURCE_FXML  = "/fxml/analyser/TimeLine.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();
    
    private final ChangeListener<Parent> lifeCycleListener;

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
        lifeCycleListener = new LifeCycleListener<>();
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
    }
    
    @FXML
    protected void onDragOver( DragEvent event )
    {
        PropertyMonitor m = monitor( event );
        TransferMode[] modes = m != null ? new TransferMode[]{ TransferMode.LINK } : TransferMode.NONE;
        event.acceptTransferModes( modes );
        event.consume();
    }

    @FXML
    protected void onDragDropped( DragEvent event )
    {
        PropertyMonitor m = monitor( event );
        event.setDropCompleted( m != null && addPropertyMonitor( m ) );
        event.consume();
    }
    
    private static PropertyMonitor monitor( DragEvent event )
    {
        Object gs = event.getGestureSource();
        if( gs instanceof Node )
        {
            Object userData = ((Node)gs).getUserData();
            if( userData instanceof PropertyMonitor )
                return (PropertyMonitor)userData;
        }
        return null;
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
    
    void reset( ValueRulerPropertiesPaneController controller )
    {
        valueRulerController.reset( controller );
    }

    void reset( TimeRulerPropertiesPaneController controller )
    {
        timeRulerController.reset( controller );
    }

    void reset( GraphPropertiesPaneController controller )
    {
        graphController.reset( controller );
    }
    
    /**
     * Добавляет значение, отображаемое на графике.
     * 
     * @param monitor        источник значений.
     */ 
    boolean addPropertyMonitor( PropertyMonitor monitor )
    {
        boolean confirmed = true; //TODO
        if( confirmed )
        {
            ObservableList<Value> observables = legendController.valuesProperty().getValue();
            //TODO DEBUG START
            @Deprecated int i = observables.size();
            // first tab
            int buffer = 1000;
            String name = monitor.getClass().getSimpleName() + i;
            String property = "DEBUG"; 
            Value.Convertor<Float> convertor = (Float value, long timestamp) -> new Dot( value, timestamp );
            // next tab
            @Deprecated Color[] colors = {Color.RED, Color.BLUE, Color.GREEN };
            @Deprecated int[][][] patterns = { DotPainter.CROSS, DotPainter.CROSS45, DotPainter.BOX };
            int[][] pattern = patterns[i%patterns.length];
            Color color = colors[i%colors.length];
            //TODO DEBUG END
            DotPainter painter = new BufferedDotPainter( new LinkedBlockingQueue<>(), buffer );
            painter.valueConvertorProperty().bind( valueRulerController.convertorProperty() );
            painter.timeConvertorProperty().bind( timeRulerController.convertorProperty() );
            painter.writableImageProperty().bind( graphController.writableImageProperty() );
            observables.add( new Value( monitor, property, convertor, painter, pattern, color, name ) );
            return true;
        }
        else
        {
            return false;
        }
    }
    
    void extendPopupMenu( List<? extends MenuItem> parentPopupMenu )
    {
        List<MenuItem> popupItems = popup.getItems();
        //TODO DEBUG START
        popup.getItems().add( new MenuItemSimulatePropertyMonitor( 
                legendController.valuesProperty().getValue(), 
                (Void спецификация) ->
                {
                    DotPainter painter = new BufferedDotPainter( new LinkedBlockingQueue<>(), 1000 );
                    painter.valueConvertorProperty().bind( valueRulerController.convertorProperty() );
                    painter.timeConvertorProperty().bind( timeRulerController.convertorProperty() );
                    painter.writableImageProperty().bind( graphController.writableImageProperty() );
                    return painter;
                }) );
        //TODO DEBUG END
        JavaFX.copyMenuItems( parentPopupMenu, popupItems, true );
        graphController.extendPopupMenu( popupItems );
        valueRulerController.extendPopupMenu( popupItems );
        timeRulerController.extendPopupMenu( popupItems );
        legendController.extendPopupMenu( popupItems );
    }
    
    private class LifeCycleListener<T> implements ChangeListener<T>
    {
        @Override
        public void changed( ObservableValue<? extends T> __, T oldValue, T newValue )
        {
            BooleanProperty dynamicProperty = graphController.dynamicProperty();
            
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
            else if( oldValue != null )
            {
                legendController.unitProperty().unbind();
                legendController.dynamicProperty().unbindBidirectional( dynamicProperty );
                timeRulerController.relativeProperty().unbindBidirectional( dynamicProperty );
                graphController.widthProperty().unbind();
                graphController.heightProperty().unbind();
                graphController.timeConvertorProperty().unbind();
                graphController.valueConvertorProperty().unbind();

                legendController.valuesProperty().clear();
            }
        }
    }
    
}
