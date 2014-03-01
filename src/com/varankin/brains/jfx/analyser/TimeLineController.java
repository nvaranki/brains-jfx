package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.property.PropertyMonitor;
import com.varankin.util.LoggerX;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
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

    private BooleanProperty dynamicProperty;

    @FXML private Pane timeRuler;
    @FXML private TimeRulerController timeRulerController;
    @FXML private Pane valueRuler;
    @FXML private ValueRulerController valueRulerController;
    @FXML private Pane drawArea;
    @FXML private GraphPaneController graphPaneController;
    @FXML private Pane controlBar;
    @FXML private LegendPaneController legendPaneController;
    /*@FXML*/ private ContextMenu popup;
    @FXML private GridPane pane;

    public TimeLineController(  )
    {
        MenuItem menuItemRemove = new MenuItem( LOGGER.text( "timeline.popup.remove" ) );
        menuItemRemove.setGraphic( JavaFX.icon( "icons16x16/remove.png" ) );
        menuItemRemove.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionRemove( event );
            }
        } );
        
        popup = new ContextMenu();
        popup.getItems().add( menuItemRemove );
        popup.setAutoHide( true );
        
        lifeCycleListener = new LifeCycleListener();
    }

    /**
     * Создает график динамического изменения значений по времени. 
     * Применяется в конфигурации без FXML.
     */
    @Override
    public GridPane build()
    {
        legendPaneController = new LegendPaneController();
        graphPaneController = new GraphPaneController();
        timeRulerController = new TimeRulerController();
        valueRulerController = new ValueRulerController();

        timeRuler = timeRulerController.build();
        valueRuler = valueRulerController.build();
        drawArea = graphPaneController.build();
        controlBar = legendPaneController.build();

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
        pane.add( drawArea, 1, 0 );
        pane.add( controlBar, 0, 2, 2, 1 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
        dynamicProperty = graphPaneController.dynamicProperty();

        pane.setOnContextMenuRequested( new EventHandler<ContextMenuEvent>() 
        {
            @Override
            public void handle( ContextMenuEvent e )
            {
                popup.show( pane, e.getScreenX(), e.getScreenY() );
                e.consume();
            }
        } );
        pane.parentProperty().addListener( new WeakChangeListener<>( lifeCycleListener ) );
    }

    /**
     * Действие по удалению графика с экрана.
     */
    @FXML
    void onActionRemove( ActionEvent _ )
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
    
    BooleanProperty dynamicProperty()
    {
        return dynamicProperty;
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
        graphPaneController.reset( controller );
    }
    
    /**
     * Добавляет значение, отображаемое на графике.
     * 
     * @param pm        источник значений.
     * @param title     название значения для отображения на графике.
     */ 
    void addProperty( PropertyMonitor pm, String title )
    {
        // Drad'n'drop here?
        //addProperty( pm, ..., title );
    }
    
    /**
     * Добавляет значение, отображаемое на графике.
     * 
     * @param pm        источник значений.
     * @param property  название значения как атрибута в источнике значений.
     * @param convertor преобразователь значения в тип {@link Float}.
     * @param pattern   шаблон отметки на графике.
     * @param color     цвет рисования шаблона отметки на графике.
     * @param title     название значения для отображения на графике.
     */
    void addMonitor( PropertyMonitor pm, String property, Dot.Convertor<Float> convertor,
            int[][] pattern, Color color, String title )
    {
        DotPainter painter = new BufferedDotPainter( new LinkedBlockingQueue<Dot>(), 1000 );
        painter.valueConvertorProperty().bind( valueRulerController.convertorProperty() );
        painter.timeConvertorProperty().bind( timeRulerController.convertorProperty() );
        painter.writableImageProperty().bind( graphPaneController.writableImageProperty() );
        Value value = new Value();
        value.монитор = pm;
        value.property = property;
        value.convertor = convertor;
        value.painter = painter;
        value.title = title;
        value.color = color;
        value.pattern = pattern;
        legendPaneController.valuesProperty().getValue().add( value );
    }

    void extendPopupMenu( List<? extends MenuItem> parentPopupMenu )
    {
        List<MenuItem> popupItems = popup.getItems();
        //TODO DEBUG START
        MenuItem menuItemSimulate = new MenuItem("Simulate");
        menuItemSimulate.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Deprecated private int id;
            
            @Override
            public void handle( ActionEvent _ )
            {
                simulate( "Value A"+id++, "Value B"+id++, "Value C"+id++ );
            }
        } );
        popup.getItems().add( menuItemSimulate);
        //TODO DEBUG END
        JavaFX.copyMenuItems( parentPopupMenu, popupItems, true );
        graphPaneController.extendPopupMenu( popupItems );
        valueRulerController.extendPopupMenu( popupItems );
        timeRulerController.extendPopupMenu( popupItems );
        legendPaneController.extendPopupMenu( popupItems );
    }
        
    @Deprecated // DEBUG
    void simulate( String... values )
    {
        JavaFX jfx = JavaFX.getInstance();
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN };
        int[][][] patterns = { DotPainter.CROSS, DotPainter.CROSS45, DotPainter.BOX };
        int i = 0;
        final List<PropertyMonitorImpl> monitors = new ArrayList<>();
        for( String value : values )
        {
            PropertyMonitorImpl monitor = new PropertyMonitorImpl();
            monitors.add( monitor );
            addMonitor( monitor, PropertyMonitorImpl.PROPERTY, monitor.CONVERTOR, 
                    patterns[i%patterns.length], colors[i%colors.length], value );
            i++;
        }
        
        Runnable observerService = new Runnable()
        {
            @Override
            public void run()
            {
                for( PropertyMonitorImpl monitor : monitors )
                    monitor.fire();
            }
        };
        jfx.getScheduledExecutorService().scheduleAtFixedRate( observerService, 0L, 1000L, TimeUnit.MILLISECONDS );
    }
    
    @Deprecated // DEBUG
    private final static class PropertyMonitorImpl implements PropertyMonitor
    {
        static final String PROPERTY = "value";
        final Dot.Convertor<Float> CONVERTOR = new Dot.Convertor<Float>() 
        {
            @Override
            public Dot toDot( Float value, long timestamp )
            {
                return new Dot( value, timestamp );
            }
        };
        final Collection<PropertyChangeListener> listeners = new ArrayList<>();

        @Override
        public Collection<PropertyChangeListener> наблюдатели()
        {
            return listeners;
        }

        void fire()
        {
            for( PropertyChangeListener listener : listeners )
                listener.propertyChange( new PropertyChangeEvent( PropertyMonitorImpl.this, PROPERTY, 
                        null, (float)Math.random() * 2f - 1f ) );
        }
    }
    
    private class LifeCycleListener implements ChangeListener<Parent>
    {
        @Override
        public void changed( ObservableValue<? extends Parent> _, Parent oldValue, Parent newValue )
        {
            if( newValue != null )
            {
                legendPaneController.dynamicProperty().bindBidirectional( dynamicProperty );
                timeRulerController.relativeProperty().bindBidirectional( dynamicProperty );
                graphPaneController.widthProperty().bind( timeRuler.widthProperty() );
                graphPaneController.heightProperty().bind( valueRuler.heightProperty() );
                graphPaneController.timeConvertorProperty().bind( timeRulerController.convertorProperty() );
                graphPaneController.valueConvertorProperty().bind( valueRulerController.convertorProperty() );
            }
            else if( oldValue != null )
            {
                legendPaneController.dynamicProperty().unbindBidirectional( dynamicProperty );
                timeRulerController.relativeProperty().unbindBidirectional( dynamicProperty );
                graphPaneController.widthProperty().unbind();
                graphPaneController.heightProperty().unbind();
                graphPaneController.timeConvertorProperty().unbind();
                graphPaneController.valueConvertorProperty().unbind();

                legendPaneController.valuesProperty().clear();
            }
        }
    }
    
}
