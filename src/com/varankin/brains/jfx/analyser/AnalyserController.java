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
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Builder;

/**
 * FXML-контроллер панели графиков. 
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class AnalyserController implements Builder<Node>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( AnalyserController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/Analyser.css";
    private static final String CSS_CLASS = "analyser";
    
    @Deprecated private int id; //DEBUG

    @FXML private VBox box;
    @FXML private ContextMenu popup;
    @FXML private MenuItem menuItemAdd;

    public AnalyserController()
    {
    }
    
    /**
     * Создает панель графиков. 
     * Применяется в конфигурации без FXML.
     */
    @Override
    public ScrollPane build()
    {
        menuItemAdd = new MenuItem( LOGGER.text( "analyser.popup.add" ) );
        menuItemAdd.setId( "menuItemAdd" );
        menuItemAdd.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent e )
            {
                onActionAddTimeLine( e );
            }
        } );
        
        popup = new ContextMenu();
        popup.getItems().add( menuItemAdd );

        box = new VBox();
        box.setId( "box" );
        box.setFillWidth( true );
        box.setFocusTraversable( true );

        ScrollPane pane = new ScrollPane();
        pane.setId( "analyser" );
        pane.setContent( box );
        pane.setContextMenu( popup );
        pane.setFitToWidth( true );
        pane.setHbarPolicy( ScrollPane.ScrollBarPolicy.NEVER );
        pane.setVbarPolicy( ScrollPane.ScrollBarPolicy.ALWAYS );
        pane.setStyle("-fx-background-color:transparent;"); //TODO DEBUG
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
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
    }
    
    @FXML
    private void onActionAddTimeLine( ActionEvent _ )
    {
        TimeLineController controller = new TimeLineController();
        TimeLinePane timeLine = controller.build();
        timeLine.appendToPopup( popup.getItems() );
        timeLine = simulate( timeLine, "Value A"+id++, "Value B"+id++, "Value C"+id++ ); //DEBUG
        box.getChildren().addAll( timeLine, new Separator( Orientation.HORIZONTAL ) );
        controller.dynamicProperty().set( true );
    }
    
    @Deprecated // DEBUG
    private TimeLinePane simulate( TimeLinePane tl, String... values )
    {
        JavaFX jfx = JavaFX.getInstance();
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
