package com.varankin.brains.jfx.analyser;

import com.varankin.util.LoggerX;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Builder;

/**
 *
 * @author Николай
 */
public class TimeLineSetupController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TimeLineSetupController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/TimeLineSetup.css";
    private static final String CSS_CLASS = "time-line-setup";
    
    private boolean approved;
    
    @FXML private Button buttonOK, buttonCancel;
    @FXML private Pane valueRulerPropertiesPane;
    @FXML private ValueRulerPropertiesPaneController valueRulerPropertiesPaneController;
    @FXML private Pane timeRulerPropertiesPane;
    @FXML private TimeRulerPropertiesPaneController timeRulerPropertiesPaneController;
    @FXML private Node graphPropertiesPane;
    @FXML private GraphPropertiesPaneController graphPropertiesPaneController;

    @Override
    public Parent build()
    {
        buttonOK = new Button( LOGGER.text( "button.ok" ) );
        buttonOK.setDefaultButton( true );
        buttonOK.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionOK( event );
            }
        } );
        
        buttonCancel = new Button( LOGGER.text( "button.cancel" ) );
        buttonCancel.setCancelButton( true );
        buttonCancel.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionCancel( event );
            }
        } );
        
        HBox buttonBar = new HBox();
        buttonBar.getChildren().addAll( buttonOK, buttonCancel );

        Tab tabValueRuler = new Tab();
        valueRulerPropertiesPaneController = new ValueRulerPropertiesPaneController();
        valueRulerPropertiesPane = valueRulerPropertiesPaneController.build();
        tabValueRuler.setContent( valueRulerPropertiesPane );
        tabValueRuler.setText( LOGGER.text( "timeline.setup.value.title" ) );
        tabValueRuler.setClosable( false );
        
        Tab tabTimeRuler = new Tab();
        timeRulerPropertiesPaneController = new TimeRulerPropertiesPaneController();
        timeRulerPropertiesPane = timeRulerPropertiesPaneController.build();
        tabTimeRuler.setContent( timeRulerPropertiesPane );
        tabTimeRuler.setText( LOGGER.text( "timeline.setup.time.title" ) );
        tabTimeRuler.setClosable( false );
        
        Tab tabGraph = new Tab();
        graphPropertiesPaneController = new GraphPropertiesPaneController();
        graphPropertiesPane = graphPropertiesPaneController.build();
        tabGraph.setContent( graphPropertiesPane );
        tabGraph.setText( LOGGER.text( "timeline.setup.graph.title" ) );
        tabGraph.setClosable( false );
        
        TabPane tabs = new TabPane();
        tabs.getTabs().addAll( tabValueRuler, tabTimeRuler, tabGraph );

        BorderPane pane = new BorderPane();
        pane.setCenter( tabs );
        pane.setBottom( buttonBar );

        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );
        pane.getStyleClass().add( CSS_CLASS );
        
        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
        BooleanBinding validProperty = 
            Bindings.and( 
                Bindings.and( 
                    timeRulerPropertiesPaneController.validProperty(),
                    valueRulerPropertiesPaneController.validProperty() ),
                graphPropertiesPaneController.validProperty() ) ;
        buttonOK.disableProperty().bind( Bindings.not( validProperty ) );
    }

    @FXML
    void onActionOK( ActionEvent event )
    {
        approved = true;
        buttonOK.getScene().getWindow().hide();
    }
    
    @FXML
    void onActionCancel( ActionEvent event )
    {
        buttonCancel.getScene().getWindow().hide();
    }

    boolean isApproved()
    {
        return approved;
    }
    
    void setApproved( boolean value )
    {
        approved = value;
    }
    
}
