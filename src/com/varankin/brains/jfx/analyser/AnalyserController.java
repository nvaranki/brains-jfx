package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.*;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.WeakListChangeListener;
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
    
    private final ListChangeListener<Node> doubleSeparatorRemover;
    private final ChangeListener<Boolean> lifeCycleListener;
    
    private TimeLineSetupStage setup;

    @FXML private ScrollPane pane;
    @FXML private Pane buttonPanel;
    @FXML private Button buttonRemoveAll;
    @FXML private VBox box;
    @FXML private ContextMenu popup;
    @FXML private MenuItem menuItemAdd;

    public AnalyserController()
    {
        lifeCycleListener = new LifeCycleListener();
        doubleSeparatorRemover = new DoubleSeparatorRemover();
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

        pane = new ScrollPane();
        pane.setContent( box );
        pane.setContextMenu( popup );
        pane.setFitToWidth( true );
        pane.setHbarPolicy( ScrollPane.ScrollBarPolicy.NEVER );
        pane.setVbarPolicy( ScrollPane.ScrollBarPolicy.ALWAYS );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
        box.getChildren().addListener( new WeakListChangeListener<>( doubleSeparatorRemover ) );
        buttonRemoveAll.disableProperty().bind( Bindings.lessThan( 
                Bindings.size( box.getChildren() ), 2 ) );
        pane.visibleProperty().addListener( new WeakChangeListener<>( lifeCycleListener ) ); 
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
            List<Node> children = box.getChildren();
            int pos = Math.max( 0, children.indexOf( buttonPanel ) );
            children.addAll( pos, Arrays.<Node>asList( builder.getNode(), new Separator( Orientation.HORIZONTAL ) ) );
        }
    }
    
    @FXML
    private void onActionRemoveAllTimeLines( ActionEvent _ )
    {
        boolean confirmed = true;//TODO popup and ask again
        if( confirmed ) removeAllTimeLines();
    }

    private void removeAllTimeLines()
    {
        List<Node> graphs = new ArrayList<>( box.getChildren() );
        graphs.remove( buttonPanel );
        box.getChildren().removeAll( graphs );
    }
    
    private class DoubleSeparatorRemover implements ListChangeListener<Node>
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
    }
    
    private class LifeCycleListener implements ChangeListener<Boolean>
    {
        @Override
        public void changed( ObservableValue<? extends Boolean> _, 
                            Boolean oldValue, Boolean newValue )
        {
            if( newValue != null && newValue )
            {
                // всё уже настроено
            }
            else if( oldValue != null && oldValue )
            {
                buttonRemoveAll.disableProperty().unbind();
                // удалить все графики, чтобы они отключили мониторинг
                removeAllTimeLines();
            }
        }
    }
    
}
