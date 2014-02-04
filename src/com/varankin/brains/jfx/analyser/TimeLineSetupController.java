package com.varankin.brains.jfx.analyser;

import com.varankin.util.LoggerX;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

    @Override
    public Parent build()
    {
        buttonOK = new Button( LOGGER.text( "button.apply" ) );
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
        tabValueRuler.setContent( new ValueRulerPropertiesPaneController().build() );
        tabValueRuler.setText( LOGGER.text( "timeline.setup.value.title" ) );
        tabValueRuler.setClosable( false );
        
        Tab tabTimeRuler = new Tab();
        tabTimeRuler.setContent( new TimeRulerPropertiesPaneController().build() );
        tabTimeRuler.setText( LOGGER.text( "timeline.setup.time.title" ) );
        tabTimeRuler.setClosable( false );
        
        Tab tabGraph = new Tab();
        tabGraph.setContent( new GraphPropertiesPaneController().build() );
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
