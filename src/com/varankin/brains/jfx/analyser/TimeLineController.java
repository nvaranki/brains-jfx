package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
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
    
    private final SimpleBooleanProperty dynamicProperty;
    private final ContextMenu popup;

    @FXML private Pane timeRuler;
    @FXML private TimeRulerController timeRulerController;
    @FXML private Pane valueRuler;
    @FXML private ValueRulerController valueRulerController;
    @FXML private Pane drawArea;
    @FXML private GraphPaneController graphPaneController;
    @FXML private Pane controlBar;
    @FXML private LegendPaneController legendPaneController;
    //@FXML private ContextMenu popup;
    @FXML private GridPane pane;

    public TimeLineController(  )
    {
        dynamicProperty = new SimpleBooleanProperty();

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
        dynamicProperty.bindBidirectional( legendPaneController.dynamicProperty() );
        dynamicProperty.bindBidirectional( graphPaneController.dynamicProperty() );
        dynamicProperty.bindBidirectional( timeRulerController.relativeProperty() );
        
        graphPaneController.widthProperty().bind( timeRuler.widthProperty() );
        graphPaneController.heightProperty().bind( valueRuler.heightProperty() );
        graphPaneController.timeConvertorProperty().bind( timeRulerController.convertorProperty() );
        graphPaneController.valueConvertorProperty().bind( valueRulerController.convertorProperty() );
        
        pane.setOnContextMenuRequested( new EventHandler<ContextMenuEvent>() {

            @Override
            public void handle( ContextMenuEvent e )
            {
                popup.show( pane, e.getScreenX(), e.getScreenY() );
                e.consume();
            }
        } );
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
            // остановить процессы
            dynamicProperty.setValue( false );
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

    void reset( TimeLineSetupController controller )
    {
        //throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Добавляет отображаемое значение и создает очередь для отметок, рисуемых на графике.
     * 
     * @param name    название значения.
     * @param color   цвет рисования шаблона.
     * @param pattern шаблон отметки на графике.
     * @return очередь точек для рисования отметок на графике.
     */
    Queue<Dot> createQueue( String name, Color color, int[][] pattern )
    {
        BlockingQueue<Dot> queue = new LinkedBlockingQueue<>();
        DotPainter painter = new BufferedDotPainter(
                timeRulerController.convertorProperty().get(),
                valueRulerController.convertorProperty().get(),
                queue, 1000 );
        painter.writableImageProperty().bind( graphPaneController.writableImageProperty() );
        painter.colorProperty().setValue( color );
        painter.patternProperty().setValue( pattern );
        legendPaneController.addValueControl( name, painter );
        return queue;
    }
    
    @Deprecated
    void appendToPopup( List<MenuItem> items ) 
    {
        if( items != null && !items.isEmpty() )
        {
            JavaFX.copyMenuItems( items, popup.getItems(), true );
            timeRulerController.appendToPopup( popup.getItems() );
            valueRulerController.appendToPopup( popup.getItems() );

            List<MenuItem> itemsDrawArea = new ArrayList<>();
            MenuItem menuItemStart = new MenuItem( LOGGER.text( "timeline.popup.start" ) );
            menuItemStart.setGraphic( JavaFX.icon( "icons16x16/start.png" ) );
//            menuItemStart.setOnAction( legendPaneController.createActionStartAllFlows() );
            menuItemStart.disableProperty().bind( dynamicProperty() );

            MenuItem menuItemStop = new MenuItem( LOGGER.text( "timeline.popup.stop" ) );
            menuItemStop.setGraphic( JavaFX.icon( "icons16x16/stop.png" ) );
//            menuItemStop.setOnAction( legendPaneController.createActionStopAllFlows() );
            menuItemStop.disableProperty().bind( Bindings.not( dynamicProperty() ) );

            itemsDrawArea.add( menuItemStart );
            itemsDrawArea.add( menuItemStop );
            if( !popup.getItems().isEmpty() )
                itemsDrawArea.add( new SeparatorMenuItem() );
            itemsDrawArea.addAll( popup.getItems() );
//            drawArea.appendToPopup( itemsDrawArea );
            
        }
    }
    
    void setParentPopupMenu( List<? extends MenuItem> parentPopupMenu )
    {
        List<MenuItem> popupItems = popup.getItems();
        JavaFX.copyMenuItems( parentPopupMenu, popupItems, true );
        graphPaneController.setParentPopupMenu( popupItems );
        valueRulerController.setParentPopupMenu( popupItems );
        timeRulerController.setParentPopupMenu( popupItems );
        legendPaneController.setParentPopupMenu( popupItems );
    }
        
}
