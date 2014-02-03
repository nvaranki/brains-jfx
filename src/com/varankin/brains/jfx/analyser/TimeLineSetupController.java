package com.varankin.brains.jfx.analyser;

import com.varankin.util.LoggerX;
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
    

    @Override
    public Parent build()
    {
        HBox buttonBar = new HBox();
        buttonBar.getChildren().addAll( new Button("OK"), new Button("Cancel") );

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

    boolean isApproved()
    {
        return true; //TODO
    }
    
}
