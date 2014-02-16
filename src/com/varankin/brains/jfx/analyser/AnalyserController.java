package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.*;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
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
    
    static final String RESOURCE_FXML  = "/fxml/analyser/Analyser.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();
    
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
        }
        setup.showAndWait();
        TimeLineSetupController setupController = setup.getController();
        if( setupController.isApproved() )
        {
            BuilderFX<Pane,TimeLineController> builder = new BuilderFX<>();
            builder.init( TimeLineController.class, 
                    TimeLineController.RESOURCE_FXML, TimeLineController.RESOURCE_BUNDLE );
            TimeLineController controller = builder.getController();
            controller.reset( setupController.getValueRulerPropertiesPaneController() );
            controller.reset( setupController.getTimeRulerPropertiesPaneController() );
            controller.reset( setupController.getGraphPropertiesPaneController() );
            controller.extendPopupMenu( popup.getItems() );
            controller.simulate( "Value A"+id++, "Value B"+id++, "Value C"+id++ );
            List<Node> children = box.getChildren();
            int pos = Math.max( 0, children.indexOf( buttonPanel ) );
            children.addAll( pos, Arrays.<Node>asList( builder.getNode(), new Separator( Orientation.HORIZONTAL ) ) );
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
    
}
