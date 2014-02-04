package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
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
    private TimeLineSetupStage setup;

    @FXML private Pane buttonPanel;
    @FXML private Button buttonRemoveAll;
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
        Button buttonAdd = new Button( LOGGER.text( "analyser.popup.add" ) );
        buttonAdd.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent e )
            {
                onActionAddTimeLine( e );
            }
        } );
        
        buttonRemoveAll = new Button( LOGGER.text( "analyser.popup.clean" ) );
        buttonRemoveAll.setId( "buttonRemoveAll" );
        buttonRemoveAll.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent e )
            {
                onActionRemoveAllTimeLines(e );
            }
        } );
        
        buttonPanel = new HBox();
        buttonPanel.setId( "buttonPanel" );
        buttonPanel.getChildren().addAll( buttonAdd, buttonRemoveAll );
        
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
        box.getChildren().add( buttonPanel );
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
        buttonRemoveAll.disableProperty().bind( Bindings.lessThan( 
                Bindings.size( box.getChildren() ), 2 ) );
    }
    
    @FXML
    private void onActionAddTimeLine( ActionEvent _ )
    {
        if( setup == null )
        {
            setup = new TimeLineSetupStage();
            setup.initModality( Modality.APPLICATION_MODAL );
            setup.initOwner( JavaFX.getInstance().платформа );
            setup.setTitle( LOGGER.text( "analyser.popup.add" ) );
            setup.setOnShowing( new EventHandler<WindowEvent>() 
            {
                @Override
                public void handle( WindowEvent _ )
                {
                    setup.getController().setApproved( false );
                }
            } );
        }
        setup.showAndWait();
        if( setup.getController().isApproved() )
        {
            TimeLineController controller = new TimeLineController();
            TimeLinePane timeLine = controller.build();
            timeLine.appendToPopup( popup.getItems() );
            timeLine = simulate( timeLine, "Value A"+id++, "Value B"+id++, "Value C"+id++ ); //DEBUG
            List<Node> children = box.getChildren();
            int pos = Math.max( 0, children.indexOf( buttonPanel ) );
            children.addAll( pos, Arrays.<Node>asList( timeLine, new Separator( Orientation.HORIZONTAL ) ) );
            controller.dynamicProperty().set( true );
        }
    }
    
    @FXML
    private void onActionRemoveAllTimeLines( ActionEvent _ )
    {
        boolean confirmed = true;//TODO popup and ask again
        if( confirmed )
        {
            List<Node> graphs = new ArrayList<>( box.getChildren() );
            graphs.remove( buttonPanel );
            box.getChildren().removeAll( graphs );
        }
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
