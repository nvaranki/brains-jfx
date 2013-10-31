package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 * @author Николай
 */
public class AnalyserView extends VBox
{

    public AnalyserView( JavaFX jfx )
    {
        setFillWidth( true );
        setSpacing( 10 );
        setPadding( new Insets( 5, 5, 5, 5 ) );
        getChildren().add( simulate( jfx, new TimeLine( jfx, -1.0F, +1.0F, 60, 2 ), "Value 1", "Value 2", "Value 3" ) );
        getChildren().add( new Separator( Orientation.HORIZONTAL ) );
        getChildren().add( simulate( jfx, new TimeLine( jfx, -1.0F, +1.0F, 60, 2 ), "Value 4", "Value 5", "Value 6" ) );
        getChildren().add( new Separator( Orientation.HORIZONTAL ) );
        getChildren().add( simulate( jfx, new TimeLine( jfx, -1.0F, +1.0F, 60, 2 ), "Value 7", "Value 8", "Value 9" ) );
        getChildren().add( new Separator( Orientation.HORIZONTAL ) );
        getChildren().add( simulate( jfx, new TimeLine( jfx, -1.0F, +1.0F, 60, 2 ), "Value A", "Value B", "Value C" ) );
        getChildren().add( new Separator( Orientation.HORIZONTAL ) );
        getChildren().add( simulate( jfx, new TimeLine( jfx, -1.0F, +1.0F, 60, 2 ), "Value D", "Value E", "Value F" ) );
        getChildren().add( new Separator( Orientation.HORIZONTAL ) );
    }
    
    @Deprecated // DEBUG
    private TimeLine simulate( JavaFX jfx, TimeLine tl, String... values )
    {
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN };
        int[][][] patterns = { DrawAreaPainter.CROSS, DrawAreaPainter.CROSS45, DrawAreaPainter.BOX };
        int i = 0;
        final List<Queue<Dot>> queues = new ArrayList<>();
        for( String value : values )
        {
            queues.add( tl.createValueQueue( value, colors[i%colors.length], patterns[i%patterns.length] ) );
            i++;
        }
//        Queue<Dot> queue2 = TimeLine.this.createValueQueue( "Value 2", Color.BLUE, DrawAreaPainter.CROSS45 );
//        Queue<Dot> queue3 = TimeLine.this.createValueQueue( "Value 3", Color.GREEN, DrawAreaPainter.BOX );
        
        Runnable observerService = new Runnable()
        {
            @Override
            public void run()
            {
                for( Queue<Dot> queue : queues )
                    queue.add( new Dot( (float)Math.random() * 2f - 1f, System.currentTimeMillis()) );
            }
        };
        jfx.getScheduledExecutorService().scheduleAtFixedRate( observerService, 0L, 1000L, TimeUnit.MILLISECONDS );
        return tl;
    }
    
}
