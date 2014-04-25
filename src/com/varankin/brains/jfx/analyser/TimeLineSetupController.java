package com.varankin.brains.jfx.analyser;

import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;

/**
 * FXML-контроллер выбора параметров рисования графика.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class TimeLineSetupController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TimeLineSetupController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/TimeLineSetup.css";
    private static final String CSS_CLASS = "time-line-setup";
    
    static final String RESOURCE_FXML  = "/fxml/analyser/TimeLineSetup.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();
    
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
        buttonOK = new Button( LOGGER.text( "button.create" ) );
        buttonOK.setDefaultButton( true );
        buttonOK.setOnAction( this::onActionOK );
        
        buttonCancel = new Button( LOGGER.text( "button.cancel" ) );
        buttonCancel.setCancelButton( true );
        buttonCancel.setOnAction( this::onActionCancel );
        
        HBox buttonBar = new HBox();
        buttonBar.getChildren().addAll( buttonOK, buttonCancel );

        valueRulerPropertiesPaneController = new ValueRulerPropertiesPaneController();
        valueRulerPropertiesPane = valueRulerPropertiesPaneController.build();
        Tab tabValueRuler = new Tab();
        tabValueRuler.setContent( valueRulerPropertiesPane );
        tabValueRuler.setText( LOGGER.text( "timeline.setup.value.title" ) );
        tabValueRuler.setClosable( false );
        
        timeRulerPropertiesPaneController = new TimeRulerPropertiesPaneController();
        timeRulerPropertiesPane = timeRulerPropertiesPaneController.build();
        Tab tabTimeRuler = new Tab();
        tabTimeRuler.setContent( timeRulerPropertiesPane );
        tabTimeRuler.setText( LOGGER.text( "timeline.setup.time.title" ) );
        tabTimeRuler.setClosable( false );
        
        graphPropertiesPaneController = new GraphPropertiesPaneController();
        graphPropertiesPane = graphPropertiesPaneController.build();
        Tab tabGraph = new Tab();
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
        timeRulerPropertiesPaneController.reset();
        valueRulerPropertiesPaneController.reset();
        graphPropertiesPaneController.reset();
        BooleanBinding valid = 
            Bindings.and( 
                Bindings.and( 
                    timeRulerPropertiesPaneController.validProperty(),
                    valueRulerPropertiesPaneController.validProperty() ),
                graphPropertiesPaneController.validProperty() ) ;
        buttonOK.disableProperty().bind( Bindings.not( valid ) );
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

    ValueRulerPropertiesPaneController getValueRulerPropertiesPaneController()
    {
        return valueRulerPropertiesPaneController;
    }

    TimeRulerPropertiesPaneController getTimeRulerPropertiesPaneController()
    {
        return timeRulerPropertiesPaneController;
    }
    
    GraphPropertiesPaneController getGraphPropertiesPaneController()
    {
        return graphPropertiesPaneController;
    }
    
}
