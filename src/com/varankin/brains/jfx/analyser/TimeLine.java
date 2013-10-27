package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import java.util.Queue;
import java.util.concurrent.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
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
    private final FlowPane labelsValue;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;
    
    private long refreshRate;
    private TimeUnit refreshRateUnit;
    
    TimeLine( final JavaFX jfx )
    {
        //TODO appl param
        refreshRate = 100L; // ms
        refreshRateUnit = TimeUnit.MILLISECONDS;
        
        executorService = jfx.getExecutorService();
        scheduledExecutorService = jfx.getScheduledExecutorService();
        
        // DEBUG START
        float vMin = -1f;
        float vMax = +1f;
        long tMax = System.currentTimeMillis() + 1*1000L;
        long tMin = tMax - 60*1000L;
        int daWidth = 600; 
        int daHeight = 100;
        // DEBUG END
        
        drawArea = new DrawArea( daWidth, daHeight, vMin, vMax, tMin, tMax );
        ImageView graph = new ImageView();
        graph.setLayoutY( daHeight - 1 );
        graph.setScaleY( -1 );
        graph.setPreserveRatio( true );
        graph.setImage( drawArea );

        timeRuler = new TimeRuler( daWidth, tMin, tMax, drawArea );
        valueRuler = new ValueRuler( daHeight, vMin, vMax, drawArea );

        CheckBox labelTime = new CheckBox( "Time" );
        labelTime.setSelected( true );
        labelTime.setOnAction( new Holder( labelTime, scheduledExecutorService.scheduleAtFixedRate( 
                new RefreshService(), 0L, refreshRate, refreshRateUnit ) ) );
        
        labelsValue = new FlowPane();
        labelsValue.setHgap( 30 );
        labelsValue.setPadding( new Insets( 0, 5, 0, 5 ) );
        labelsValue.setAlignment( Pos.CENTER );
        labelsValue.setMinHeight( labelTime.getMinHeight() );
        labelsValue.setPrefHeight( labelTime.getPrefHeight() );

        setPadding( new Insets( 5, 0, 5, 0 ) );
        setSpacing( 5 );
        getChildren().add( buildGraphGrid( daWidth, daHeight, graph, valueRuler, timeRuler ) );
        getChildren().add( buildLabelGrid( daWidth, labelsValue, labelTime ) );
        
        // DEBUG START
        Runnable observerService = new Runnable()
        {
            Queue<Dot> queue1 = TimeLine.this.addValue( "Value 1", Color.RED,  DrawAreaPainter.CROSS );
            Queue<Dot> queue2 = TimeLine.this.addValue( "Value 2", Color.BLUE, DrawAreaPainter.CROSS45 );
            Queue<Dot> queue3 = TimeLine.this.addValue( "Value 3", Color.GREEN, DrawAreaPainter.BOX );
            
            @Override
            public void run()
            {
                queue1.add( new Dot( (float)Math.random() * 2f - 1f, System.currentTimeMillis()) );
                queue2.add( new Dot( (float)Math.random() * 2f - 1f, System.currentTimeMillis()) );
                queue3.add( new Dot( (float)Math.random() * 2f - 1f, System.currentTimeMillis()) );
            }
        };
        scheduledExecutorService.scheduleAtFixedRate( observerService, 0L, 1000L, TimeUnit.MILLISECONDS );
        // DEBUG END
    }
    
    private static GridPane buildGraphGrid( int daWidth, int daHeight, 
            Node graph, Node valueRuler, Node timeRuler )
    {
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setMinWidth( 45d );
        cc0.setHgrow( Priority.NEVER );
        cc0.setHalignment( HPos.RIGHT );
        
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setPrefWidth( daWidth );
        cc1.setMinWidth( daWidth );
        cc1.setHgrow( Priority.ALWAYS );
        cc1.setHalignment( HPos.LEFT );
        
        RowConstraints rc0 = new RowConstraints();
        rc0.setPrefHeight( daHeight );
        rc0.setMinHeight( daHeight );
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
    
    private static GridPane buildLabelGrid( int daWidth, Node labelsValue, Node labelTime )
    {
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setHgrow( Priority.ALWAYS );
        cc0.setHalignment( HPos.CENTER );
        
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow( Priority.NEVER );
        cc1.setHalignment( HPos.RIGHT );
        
        GridPane grid = new GridPane();
        grid.setPrefWidth( daWidth + 45 );
        grid.setMaxWidth( daWidth + 45 );
        grid.getColumnConstraints().addAll( cc0, cc1 );
        grid.add( labelsValue, 0, 0 );
        grid.add( labelTime, 1, 0 );
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
    Queue<Dot> addValue( String name, Color color, int[][] pattern )
    {
        BlockingQueue<Dot> queue = new LinkedBlockingQueue<>();
        DrawAreaPainter painter = new DrawAreaPainter( drawArea, color, pattern, queue );
        
        WritableImage sample = new WritableImage( 16, 16 );
        Color outlineColor = Color.LIGHTGRAY;
        for( int i = 1; i < 15; i ++ )
        {
            sample.getPixelWriter().setColor( i,  0, outlineColor );
            sample.getPixelWriter().setColor( i, 15, outlineColor );
            sample.getPixelWriter().setColor(  0, i, outlineColor );
            sample.getPixelWriter().setColor( 15, i, outlineColor );
        }
        painter.paint( 7, 7, sample );

        CheckBox label = new CheckBox( name );
        label.setGraphic( new ImageView( sample ) );
        label.setGraphicTextGap( 3 );
        label.setSelected( true );
        label.setOnAction( new Selector( label, painter, executorService.submit( painter ) ) );
        
        labelsValue.getChildren().add( label );
        
        return queue;
    }
    
    /**
     * Контроллер движения временной шкалы.
     */
    private class Holder implements EventHandler<ActionEvent>
    {
        private final CheckBox cb;
        private Future<?> process;

        Holder( CheckBox cb, Future<?> process )
        {
            this.cb = cb;
            this.process = process;
        }
        
        @Override
        public void handle( ActionEvent t )
        {
            if( cb.selectedProperty().get() )
                process = scheduledExecutorService.scheduleAtFixedRate( 
                        new RefreshService(), 0L, refreshRate, refreshRateUnit );
            else
                process.cancel( true );
        }
        
    }
    
    /**
     * Контроллер видимости значений.
     */
    private class Selector implements EventHandler<ActionEvent>
    {
        private final CheckBox cb;
        private final DrawAreaPainter painter;
        private Future<?> process;

        Selector( CheckBox cb, DrawAreaPainter painter, Future<?> process ) 
        {
            this.cb = cb;
            this.painter = painter;
            this.process = process;
        }
        
        @Override
        public void handle( ActionEvent _ ) 
        {
            if( cb.selectedProperty().get() )
                process = executorService.submit( painter );
            else
                process.cancel( true );
        }
    }

    /**
     * Сервис движения временной шкалы.
     */
    private class RefreshService implements Runnable
    {
        @Override
        public void run()
        {
            Platform.runLater( new Adopter( System.currentTimeMillis() ) ); 
        }
    }
    
    /**
     * Контроллер сдвига временной шкалы.
     */
    private class Adopter implements Runnable
    {
        private final long moment;

        Adopter( long moment )
        {
            this.moment = moment;
        }
        
        @Override
        public void run()
        {
            drawArea.adopt( moment );
            //timeRuler.adopt( moment );
        }
        
    }
    
}
