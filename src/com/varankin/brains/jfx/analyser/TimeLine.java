package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import java.util.Queue;
import java.util.concurrent.*;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * График динамического отображения изменения значений по времени. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class TimeLine extends VBox
{
    private final DrawArea drawArea;
    private final TimeRuler timeRuler;
    private final ValueRuler valueRuler;
    private final ControlBar controlBar;
    
    TimeLine( final JavaFX jfx )
    {
        // DEBUG START
        float vMin = -1f;
        float vMax = +1f;
        long tMax = System.currentTimeMillis() + 1*1000L;
        long tMin = tMax - 60*1000L;
        // DEBUG END
        
        timeRuler = new TimeRuler( tMin, tMax );
        valueRuler = new ValueRuler( vMin, vMax );
        drawArea = new DrawArea( timeRuler, valueRuler );

        setPadding( new Insets( 5, 0, 5, 0 ) );
        setSpacing( 5 );
        getChildren().add( buildGraphGrid( drawArea, valueRuler, timeRuler ) );
        getChildren().add( controlBar = new ControlBar( jfx, drawArea.newRefreshServiceInstance() ) );
        
        // DEBUG START
        Runnable observerService = new Runnable()
        {
            Queue<Dot> queue1 = TimeLine.this.createValueQueue( "Value 1", Color.RED,  DrawAreaPainter.CROSS );
            Queue<Dot> queue2 = TimeLine.this.createValueQueue( "Value 2", Color.BLUE, DrawAreaPainter.CROSS45 );
            Queue<Dot> queue3 = TimeLine.this.createValueQueue( "Value 3", Color.GREEN, DrawAreaPainter.BOX );
            
            @Override
            public void run()
            {
                queue1.add( new Dot( (float)Math.random() * 2f - 1f, System.currentTimeMillis()) );
                queue2.add( new Dot( (float)Math.random() * 2f - 1f, System.currentTimeMillis()) );
                queue3.add( new Dot( (float)Math.random() * 2f - 1f, System.currentTimeMillis()) );
            }
        };
        jfx.getScheduledExecutorService().scheduleAtFixedRate( observerService, 0L, 1000L, TimeUnit.MILLISECONDS );
        // DEBUG END
    }
    
    private static GridPane buildGraphGrid( Node graph, Node valueRuler, Node timeRuler )
    {
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

        GridPane grid = new GridPane();
        grid.getColumnConstraints().addAll( cc0, cc1 );
        grid.getRowConstraints().addAll( rc0, rc1 );
        grid.add( valueRuler, 0, 0 );
        grid.add( timeRuler, 1, 1 );
        grid.add( graph, 1, 0 );
        return grid;
    }
    
    /**
     * Добавляет отображаемое значение и создает очередь для отметок, рисуемых на графике.
     * 
     * @param name    название значения.
     * @param color   цвет рисования шаблона.
     * @param pattern шаблон отметки на графике.
     * @return очередь точек для рисования отметок на графике.
     */
    Queue<Dot> createValueQueue( String name, Color color, int[][] pattern )
    {
        BlockingQueue<Dot> queue = new LinkedBlockingQueue<>();
        DrawAreaPainter painter = new DrawAreaPainter( drawArea, timeRuler, valueRuler, 
                color, pattern, queue );
        controlBar.addValueControl( name, painter );
        return queue;
    }
    
}
