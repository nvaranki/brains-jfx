package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 * @author Николай
 */
public class AnalyserView extends ScrollPane
{
    private static final LoggerX LOGGER = LoggerX.getLogger( AnalyserView.class );

    private final ContextMenu popup;

    public AnalyserView( final JavaFX jfx )
    {
        final VBox box = new VBox();
        box.setSpacing( 10 );
        box.setFillWidth( true );
        box.setPadding( new Insets( 5, 5, 5, 5 ) );
        box.setFocusTraversable( true );
        box.getChildren().addListener( new ListChangeListener<Node>() 
        {
            @Override
            public void onChanged( ListChangeListener.Change<? extends Node> change )
            {
                boolean removed = false;
                while( change.next() )
                    removed = removed || !change.getRemoved().isEmpty();
                if( removed )
                    for( int i = 0; i < box.getChildren().size(); i++ )
                        if( i % 2 == 0 && box.getChildren().get( i ) instanceof Separator )
                            box.getChildren().remove( i-- );
            }
        } );
        
        MenuItem menuItemAdd = new MenuItem( LOGGER.text( "analyser.popup.add" ) );
        popup = new ContextMenu( menuItemAdd ); //no event consumption by setContextMenu( popup ); !!!
        menuItemAdd.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Deprecated private int id; //DEBUG
            
            @Override
            public void handle( ActionEvent t )
            {
                TimeConvertor tc = new TimeConvertor( 60, 2, TimeUnit.SECONDS ); 
                ValueConvertor vc = new ValueConvertor( -1.0F, +1.0F );
                TimeLineController controller = new TimeLineController( jfx, tc, vc );
                TimeLinePane timeLine = new TimeLinePane( controller );
                timeLine.appendToPopup( popup.getItems() );
                timeLine = simulate( jfx, timeLine, "Value A"+id++, "Value B"+id++, "Value C"+id++ ); //DEBUG
                box.getChildren().addAll( timeLine, new Separator( Orientation.HORIZONTAL ) );
                controller.dynamicProperty().set( true );
            }
        } );

        setHbarPolicy( ScrollPane.ScrollBarPolicy.NEVER );
        setVbarPolicy( ScrollPane.ScrollBarPolicy.ALWAYS );
        setFitToWidth( true );
        setStyle("-fx-background-color:transparent;");
        setContent( box );
        setOnMouseClicked( new ContextMenuRaiser( popup, AnalyserView.this ) );
    }
    
    @Deprecated // DEBUG
    private TimeLinePane simulate( JavaFX jfx, TimeLinePane tl, String... values )
    {
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN };
        int[][][] patterns = { DotPainter.CROSS, DotPainter.CROSS45, DotPainter.BOX };
        int i = 0;
        final List<Queue<Dot>> queues = new ArrayList<>();
        for( String value : values )
        {
            queues.add( tl.createQueue( value, colors[i%colors.length], patterns[i%patterns.length] ) );
            i++;
        }
        
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
