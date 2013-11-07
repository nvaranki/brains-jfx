package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.InverseBooleanBinding;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.*;
import java.util.concurrent.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Управляемый график динамического изменения значений по времени. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class TimeLinePane extends GridPane
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TimeLinePane.class );

    private final DrawAreaPane drawArea;
    private final TimeRulerPane timeRuler;
    private final ValueRulerPane valueRuler;
    private final ControlBarPane controlBar;
    private final TimeLineController controller;
    @Deprecated private final ContextMenu popup;
    
    TimeLinePane( TimeLineController timeLineController )
    {
        controller = timeLineController;
        timeRuler = new TimeRulerPane( controller.getTimeConvertor() );
        valueRuler = new ValueRulerPane( controller.getValueConvertor() );
        controlBar = new ControlBarPane( controller );
        drawArea = new DrawAreaPane( controller );

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
        
        getColumnConstraints().addAll( cc0, cc1 );
        getRowConstraints().addAll( rc0, rc1, rc2 );
        setPadding( new Insets( 5, 5, 5, 0 ) );
        
        add( valueRuler, 0, 0 );
        add( timeRuler, 1, 1 );
        add( drawArea, 1, 0 );
        add( controlBar, 0, 2, 2, 1 );
       
        MenuItem menuItemRemove = new MenuItem( LOGGER.text( "timeline.popup.remove" ) );
        menuItemRemove.setGraphic( JavaFX.icon( "icons16x16/remove.png" ) );
        menuItemRemove.setOnAction( new ActionRemove() );
        
        popup = new ContextMenu();
        popup.getItems().add( menuItemRemove );

        controller.widthProperty().bind( timeRuler.widthProperty() );
        controller.heightProperty().bind( valueRuler.heightProperty() );
        timeRuler.relativeProperty().bind( controller.dynamicProperty() );
        
        setOnMouseClicked( new ContextMenuRaiser( popup, TimeLinePane.this ) );
    }
    
    @Deprecated
    void appendToPopup( List<MenuItem> items ) 
    {
        if( items != null && !items.isEmpty() )
        {
            JavaFX.copyMenuItems( items, popup.getItems(), true );
            
            timeRuler.appendToPopup( popup.getItems() );
            
            valueRuler.appendToPopup( popup.getItems() );

            List<MenuItem> itemsDrawArea = new ArrayList<>();
            MenuItem menuItemStart = new MenuItem( LOGGER.text( "timeline.popup.start" ) );
            menuItemStart.setGraphic( JavaFX.icon( "icons16x16/start.png" ) );
            menuItemStart.setOnAction( controlBar.createActionStartAllFlows() );
            menuItemStart.disableProperty().bind( controller.dynamicProperty() );

            MenuItem menuItemStop = new MenuItem( LOGGER.text( "timeline.popup.stop" ) );
            menuItemStop.setGraphic( JavaFX.icon( "icons16x16/stop.png" ) );
            menuItemStop.setOnAction( controlBar.createActionStopAllFlows() );
            menuItemStop.disableProperty().bind( new InverseBooleanBinding( controller.dynamicProperty() ) );

            itemsDrawArea.add( menuItemStart );
            itemsDrawArea.add( menuItemStop );
            if( !popup.getItems().isEmpty() )
                itemsDrawArea.add( new SeparatorMenuItem() );
            itemsDrawArea.addAll( popup.getItems() );
            drawArea.appendToPopup( itemsDrawArea );
        }
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
                controller.getTimeConvertor(), controller.getValueConvertor(), 
                color, pattern, queue, 1000 );
        painter.writableImageProperty().bind( controller.writableImageProperty() );
        controlBar.addValueControl( name, painter, popup.getItems() );
        return queue;
    }
    
    /**
     * Действие по удалению графика с экрана.
     */
    private class ActionRemove implements EventHandler<ActionEvent>
    {
        @Override
        public void handle( ActionEvent _ )
        {
            Parent parent = TimeLinePane.this.getParent();
            if( parent instanceof Pane )
            {
                // остановить процессы
                controller.dynamicProperty().setValue( false );
                // убрать с экрана
                Pane pane = (Pane)parent;
                pane.getChildren().remove( TimeLinePane.this );
            }
            else
            {
                LOGGER.log( "001001001W" );
            }
        }
    }
    
}
