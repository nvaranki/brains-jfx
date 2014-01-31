package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import javafx.beans.binding.Bindings;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Управляемый график динамического изменения значений по времени. 
 * 
 * @author &copy; 2014 Николай Варанкин
 */
final class TimeLinePane extends GridPane
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TimeLinePane.class );
    private static final String RESOURCE_FXML_LEGEND = "/fxml/analyser/LegendPane.fxml";
    private static final String RESOURCE_FXML_GRAPH = "/fxml/analyser/GraphPane.fxml";

    private final Node drawArea;
    private final Node controlBar;
    private final TimeLineController controller;
    @Deprecated private final ContextMenu popup;
    private final GraphPaneController graphPaneController;
    private final LegendPaneController legendPaneController;
    
    @FXML private TimeRulerController timeRulerController;
    @FXML private ValueRulerController valueRulerController;

    TimeLinePane( TimeLineController timeLineController )
    {
        controller = timeLineController;
        
        if( JavaFX.getInstance().useFxmlLoader() )
            try
            {
                java.net.URL location;
                FXMLLoader fxmlLoader;
                ResourceBundle resources = LOGGER.getLogger().getResourceBundle();
                
                location = getClass().getResource( RESOURCE_FXML_LEGEND );
                fxmlLoader = new FXMLLoader( location, resources );
                controlBar = (Node)fxmlLoader.load();
                legendPaneController = fxmlLoader.getController();

                location = getClass().getResource( RESOURCE_FXML_GRAPH );
                fxmlLoader = new FXMLLoader( location, resources );
                drawArea = (Node)fxmlLoader.load();
                graphPaneController = fxmlLoader.getController();
            }
            catch( IOException | ClassCastException ex )
            {
                throw new RuntimeException( ex );
            }
        else
        {
            legendPaneController = new LegendPaneController();
            graphPaneController = new GraphPaneController();

            controlBar = legendPaneController.build();
            drawArea = graphPaneController.build();
        }

        timeLineController.dynamicProperty().bindBidirectional( legendPaneController.dynamicProperty() );
        
        timeRulerController = new TimeRulerController();
        valueRulerController = new ValueRulerController();
        Pane timeRuler = timeRulerController.build();//new TimeRulerPane( controller.getTimeConvertor() );
        Pane valueRuler = valueRulerController.build();//new ValueRulerPane( controller.getValueConvertor() );

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

        controller.dynamicProperty().bindBidirectional( graphPaneController.dynamicProperty() );
        controller.dynamicProperty().bindBidirectional( timeRulerController.relativeProperty() );
        graphPaneController.widthProperty().bind( timeRuler.widthProperty() );
        graphPaneController.heightProperty().bind( valueRuler.heightProperty() );
        graphPaneController.timeConvertorProperty().bind( timeRulerController.convertorProperty() );
        graphPaneController.valueConvertorProperty().bind( valueRulerController.convertorProperty() );
        
        //TODO setOnMouseClicked( new ContextMenuRaiser( popup, TimeLinePane.this ) );
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
            menuItemStart.disableProperty().bind( controller.dynamicProperty() );

            MenuItem menuItemStop = new MenuItem( LOGGER.text( "timeline.popup.stop" ) );
            menuItemStop.setGraphic( JavaFX.icon( "icons16x16/stop.png" ) );
//            menuItemStop.setOnAction( legendPaneController.createActionStopAllFlows() );
            menuItemStop.disableProperty().bind( Bindings.not( controller.dynamicProperty() ) );

            itemsDrawArea.add( menuItemStart );
            itemsDrawArea.add( menuItemStop );
            if( !popup.getItems().isEmpty() )
                itemsDrawArea.add( new SeparatorMenuItem() );
            itemsDrawArea.addAll( popup.getItems() );
//            drawArea.appendToPopup( itemsDrawArea );
            
            graphPaneController.setParentPopupMenu( popup.getItems() );
            legendPaneController.setParentPopupMenu( popup.getItems() );
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
                timeRulerController.convertorProperty().get(),
                valueRulerController.convertorProperty().get(),
                queue, 1000 );
        painter.writableImageProperty().bind( graphPaneController.writableImageProperty() );
        painter.colorProperty().setValue( color );
        painter.patternProperty().setValue( pattern );
        legendPaneController.addValueControl( name, painter );
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
