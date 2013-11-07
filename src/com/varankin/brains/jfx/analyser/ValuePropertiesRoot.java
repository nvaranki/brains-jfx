package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.InverseBooleanBinding;
import com.varankin.util.LoggerX;
import java.util.*;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;

/**
 * Панель диалога для выбора и установки параметров прорисовки отметок.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class ValuePropertiesRoot extends BorderPane
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValuePropertiesRoot.class );
    private static final String RESOURCE_CSS  = "/fxml/analyzer/ValuePropertiesRoot.css";
    private static final String RESOURCE_FXML = "/fxml/analyzer/ValuePropertiesRoot.fxml";
    private static final String CSS_CLASS = "value-properties-root";

    @FXML private ValuePropertiesPane properties;
    @FXML private Button buttonApply;
    
    private DotPainter painter;

    public ValuePropertiesRoot()
    {
        //<editor-fold defaultstate="collapsed" desc="API Loader">
/*       
        properties = new ValuePropertiesPane();
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
        buttonBar.getChildren().addAll( buttonOK, buttonApply, buttonCancel );

        setCenter( properties );
        setBottom( buttonBar );

        getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );
        initialize();
 */       
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="FXML Loader">
/**/
        java.net.URL location = getClass().getResource( RESOURCE_FXML );
        ResourceBundle resources = LOGGER.getLogger().getResourceBundle();
        javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader( location, resources );
        fxmlLoader.setRoot( ValuePropertiesRoot.this );
        fxmlLoader.setController( ValuePropertiesRoot.this );

        try 
        {
            fxmlLoader.load();
        } 
        catch( java.io.IOException exception ) 
        {
            throw new RuntimeException( exception );
        }
        
        //</editor-fold>
    }
    
    @FXML
    protected void initialize()
    {
        getStyleClass().add( CSS_CLASS );

        buttonApply.disableProperty().bind( 
                new InverseBooleanBinding( properties.changedProperty() ) );
    }
    
    final void setPainter( DotPainter painter )
    {
        this.painter = painter;
    }

    @FXML
    void onActionOK( ActionEvent event )
    {
        applyChanges();
        getScene().getWindow().hide();
    }
    
    @FXML
    void onActionApply( ActionEvent event )
    {
        applyChanges();
        properties.changedProperty().setValue( Boolean.FALSE );
    }
    
    @FXML
    void onActionCancel( ActionEvent event )
    {
        getScene().getWindow().hide();
    }
    
    private void applyChanges()
    {
        Color oldColor = painter.getColor();
        Color newColor = properties.getColor();
        if( newColor != null && !oldColor.equals( newColor ) )
            painter.setColor( newColor );
        
        int[][] oldPattern = painter.getPattern();
        int[][] newPattern = properties.getMarker().pattern;
        if( newPattern != null && !Arrays.deepEquals( oldPattern, newPattern ) )
            painter.setPattern( newPattern );
    }

    @FXML
    void onShowing( WindowEvent event )
    {
        properties.setColor( painter.getColor() );
        properties.setScale( 3 );
        int[][] painterPattern = painter.getPattern();
        for( Marker m : Marker.values() )
            if( Arrays.deepEquals( painterPattern, m.pattern ) )
            {
                properties.setMarker( m );
                break;
            }
        properties.changedProperty().setValue( Boolean.FALSE );
    }
    
}
