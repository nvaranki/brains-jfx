package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import java.util.Queue;
import java.util.concurrent.*;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * График динамического отображения изменения значений по времени. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class TimeLine extends GridPane
{
    private final DrawArea drawArea;
    private final TimeRuler timeRuler;
    private final ValueRuler valueRuler;
    private final ControlBar controlBar;
    
    TimeLine( JavaFX jfx, float vMin, float vMax, int tDuration, int tExcess )
    {
        timeRuler = new TimeRuler( tDuration, tExcess, TimeUnit.SECONDS );
        valueRuler = new ValueRuler( vMin, vMax );
        drawArea = new DrawArea( timeRuler, valueRuler );
        controlBar = new ControlBar( jfx, drawArea.newRefreshServiceInstance() );
        timeRuler.relativeProperty().bind( controlBar.dynamicProperty() );

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
        DrawAreaPainter painter = new BufferedDrawAreaPainter( drawArea, timeRuler, valueRuler, 
                color, pattern, queue, 1000 );
        controlBar.addValueControl( name, painter );
        return queue;
    }
    
}
