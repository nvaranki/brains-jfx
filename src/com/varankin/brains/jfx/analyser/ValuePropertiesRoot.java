package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.InverseBooleanBinding;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.Arrays;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;

/**
 * Панель диалога для выбора и установки параметров прорисовки отметок.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
class ValuePropertiesRoot extends BorderPane
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValuePropertiesRoot.class );

    @FXML private final ValuePropertiesPane properties;
    private DotPainter painter;

    ValuePropertiesRoot()
    {
        properties = new ValuePropertiesPane();
        properties.setId( "properties" );

        double gap = JavaFX.getInstance().getDefaultGap();

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

        Button buttonApply = new Button( LOGGER.text( "button.apply" ) );
        buttonApply.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionApply( event );
            }
        } );
        buttonApply.disableProperty().bind( new InverseBooleanBinding( properties.changedProperty() ) );

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
        buttonBar.setAlignment( Pos.BASELINE_RIGHT );
        buttonBar.setPadding( new Insets( gap ) );
        buttonBar.setSpacing( gap );

        setCenter( properties );
        setBottom( buttonBar );
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
        properties.setScale( 1 );
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
