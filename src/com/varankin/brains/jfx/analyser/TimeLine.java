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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
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
public class TimeLine extends VBox
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

        
        Label labelTime = new Label("Time");
        labelTime.setAlignment( Pos.BASELINE_RIGHT );
        
        FlowPane labelsValue = new FlowPane();
        labelsValue.setHgap( 5 );
        labelsValue.setPadding( new Insets( 0, 5, 0, 5 ) );
        labelsValue.setAlignment( Pos.CENTER );
        labelsValue.setMinHeight( labelTime.getMinHeight() );
        labelsValue.setPrefHeight( labelTime.getPrefHeight() );

        // DEBUG START
        Label labelValue1 = new Label("Value 1");
        Label labelValue2 = new Label("Value 2");
        Label labelValue3 = new Label("Value 3");
        Label labelValue4 = new Label("Value 4");
        labelsValue.getChildren().addAll( labelValue1, labelValue2, labelValue3, labelValue4 );
        // DEBUG END
        
        setPadding( new Insets( 5, 0, 5, 0 ) );
        setSpacing( 5 );
        getChildren().add( buildGraphGrid( daWidth, daHeight, graph, valueRuler, timeRuler ) );
        getChildren().addAll( buildLabelGrid( daWidth, labelsValue, labelTime ) );
        
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
