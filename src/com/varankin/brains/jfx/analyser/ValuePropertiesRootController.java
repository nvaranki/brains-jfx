package com.varankin.brains.jfx.analyser;

import com.varankin.util.LoggerX;
import java.util.Arrays;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Builder;

/**
/**
 * FXML-контроллер панели диалога для выбора и установки параметров прорисовки отметок.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class ValuePropertiesRootController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValuePropertiesRootController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ValuePropertiesRoot.css";
    private static final String CSS_CLASS = "value-properties-root";
    
    private DotPainter painter;
    
    @FXML private Pane properties;
    @FXML private Button buttonApply;
    @FXML private ValuePropertiesPaneController propertiesController;
    
    /**
     * Создает панель диалога для выбора и установки параметров прорисовки отметок.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public BorderPane build()
    {
        propertiesController = new ValuePropertiesPaneController();
        
        properties = propertiesController.build();
        properties.setId( "properties" );

        Button buttonOK = new Button( LOGGER.text( "button.ok" ) );
        buttonOK.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionOK( event );
            }
        } );
        buttonOK.setDefaultButton( true );

        buttonApply = new Button( LOGGER.text( "button.apply" ) );
        buttonApply.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionApply( event );
            }
        } );
        buttonApply.setId( "buttonApply" );

        Button buttonCancel = new Button( LOGGER.text( "button.cancel" ) );
        buttonCancel.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionCancel( event );
            }
        } );
        buttonCancel.setCancelButton( true );

        HBox buttonBar = new HBox();
        buttonBar.getChildren().addAll( buttonOK, buttonCancel, buttonApply );

        BorderPane pane = new BorderPane();
        pane.setCenter( properties );
        pane.setBottom( buttonBar );

        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );
        pane.getStyleClass().add( CSS_CLASS );
        
        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
        buttonApply.disableProperty().bind( Bindings.not( propertiesController.changedProperty() ) );
    }
    
    @FXML
    void onActionOK( ActionEvent event )
    {
        applyChanges();
        buttonApply.getScene().getWindow().hide();
    }
    
    @FXML
    void onActionApply( ActionEvent event )
    {
        applyChanges();
        propertiesController.changedProperty().setValue( Boolean.FALSE );
    }
    
    @FXML
    void onActionCancel( ActionEvent event )
    {
        buttonApply.getScene().getWindow().hide();
    }
    
    private void applyChanges()
    {
        Color oldColor = painter.colorProperty().getValue();
        Color newColor = propertiesController.getColor();
        if( newColor != null && !newColor.equals( oldColor ) )
            painter.colorProperty().setValue( newColor );
        
        int[][] oldPattern = painter.patternProperty().getValue();
        int[][] newPattern = propertiesController.getMarker().pattern;
        if( newPattern != null && !Arrays.deepEquals( oldPattern, newPattern ) )
            painter.patternProperty().setValue( newPattern );
    }

    final void setPainter( DotPainter painter )
    {
        this.painter = painter;
        propertiesController.setColor( painter.colorProperty().getValue() );
        propertiesController.setScale( 3 );
        int[][] painterPattern = painter.patternProperty().getValue();
        for( Marker m : Marker.values() )
            if( Arrays.deepEquals( painterPattern, m.pattern ) )
            {
                propertiesController.setMarker( m );
                break;
            }
        propertiesController.changedProperty().setValue( Boolean.FALSE );
    }
    
}
