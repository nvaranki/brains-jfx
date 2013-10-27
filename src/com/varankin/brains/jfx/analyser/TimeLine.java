package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 *
 * @author Николай
 */
public class TimeLine extends Pane
{
    private final BlockingQueue<Dot> queue;
    private final DrawArea drawArea;
    private final TimeRuler timeRuler;
    private final ValueRuler valueRuler;
    
    private ScheduledFuture refreshService;

    public TimeLine( final JavaFX jfx )
    {
        //TODO appl param
        long refreshRate = 200L; // ms
        TimeUnit refreshRateUnit = TimeUnit.MILLISECONDS;
//        setPrefHeight( 100 );
//        setPrefWidth( 600 );
        
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

        Label label = new Label("Value label");
        label.setLayoutY( daHeight );
        label.rotateProperty().setValue( -90 );
        
        Label labelTime = new Label("Time axis");
        //labelTime.setAlignment( Pos.BASELINE_RIGHT );
        Label labelValue = new Label("Value axis");
        //labelValue.setLayoutY( 100 );
        labelValue.getTransforms().add( new Translate( 0, 100 ) );//setRotate( -90 );
        labelValue.getTransforms().add( new Rotate( -90 ) );//setRotate( -90 );
        labelValue.autosize();
        
        GridPane grid = new GridPane();
        GridPane.setValignment( labelValue, VPos.TOP );
        GridPane.setHalignment( labelTime, HPos.RIGHT );
        grid.add( label, 0, 0, 1, 3 );
        grid.add( labelValue, 1, 0 );
        grid.add( valueRuler, 2, 0 );
        grid.add( timeRuler, 3, 1 );
        grid.add( labelTime, 3, 2 );
        grid.add( graph, 3, 0 );
//        grid.setGridLinesVisible( true );
//        ColumnConstraints cc1 = new ColumnConstraints();//Builder.create().prefWidth( 25d );
//        cc1.setPrefWidth( 25d );
//        cc1.setHgrow( Priority.NEVER );
//        ColumnConstraints cc0 = new ColumnConstraints();//Builder.create().prefWidth( 25d );
//        cc0.setPrefWidth( 35d );
//        cc0.setHgrow( Priority.NEVER );
//        grid.getColumnConstraints().addAll( cc0, cc1 );
        
        getChildren().add( grid );
        
        queue = new LinkedBlockingQueue<>();
        jfx.getExecutorService().submit( new DrawAreaPainter( drawArea, queue ) );
        
        // DEBUG START
        Runnable observerService = new Runnable()
        {
            @Override
            public void run()
            {
                queue.add( new Dot( (float)Math.random() * 2f - 1f, System.currentTimeMillis(), 
                        Color.RED, Dot.CROSS ) );
                queue.add( new Dot( (float)Math.random() * 2f - 1f, System.currentTimeMillis(), 
                        Color.BLUE, Dot.CROSS45 ) );
            }
        };
        jfx.getScheduledExecutorService()
                .scheduleAtFixedRate( observerService, 0L, 1000L, TimeUnit.MILLISECONDS );
//        for( int i = 1; i < 60; i++ )
//            queue.add( new Dot( (float)Math.random() * 2f - 1f, tMin + i * 1000L, Color.RED, Dot.CROSS ) );
//        for( int i = 1; i < 60; i++ ) 
//            queue.add( new Dot( (float)Math.random() * 2f - 1f, tMin + i * 1000L, Color.BLUE, Dot.CROSS45 ) );
//        queue.add( new Dot( 0f, tMin, Color.GREEN, Dot.DOT ) );
        // DEBUG END
        
        refreshService = jfx.getScheduledExecutorService().scheduleAtFixedRate( 
                new RefreshService(), 0L, refreshRate, refreshRateUnit );
    }
    
    public final Queue<Dot> getQueue()
    {
        return queue;
    }
    
    private class RefreshService implements Runnable
    {
        @Override
        public void run()
        {
            Platform.runLater( new Adopter( System.currentTimeMillis() ) ); 
        }
    }
    
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
