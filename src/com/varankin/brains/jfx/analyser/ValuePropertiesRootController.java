package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.InverseBooleanBinding;
import java.util.Arrays;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
/**
 * Контроллер панели диалога для выбора и установки параметров прорисовки отметок.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public class ValuePropertiesRootController
{
    @FXML protected Pane properties;
    @FXML protected Button buttonApply;
    @FXML protected ValuePropertiesPaneController propertiesController;
    
    private DotPainter painter;
    
    @FXML
    protected void initialize()
    {
        buttonApply.disableProperty().bind( 
                new InverseBooleanBinding( propertiesController.changedProperty() ) );
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
        Color oldColor = painter.getColor();
        Color newColor = propertiesController.getColor();
        if( newColor != null && !oldColor.equals( newColor ) )
            painter.setColor( newColor );
        
        int[][] oldPattern = painter.getPattern();
        int[][] newPattern = propertiesController.getMarker().pattern;
        if( newPattern != null && !Arrays.deepEquals( oldPattern, newPattern ) )
            painter.setPattern( newPattern );
    }

    final void setPainter( DotPainter painter )
    {
        this.painter = painter;
        propertiesController.setColor( painter.getColor() );
        propertiesController.setScale( 3 );
        int[][] painterPattern = painter.getPattern();
        for( Marker m : Marker.values() )
            if( Arrays.deepEquals( painterPattern, m.pattern ) )
            {
                propertiesController.setMarker( m );
                break;
            }
        propertiesController.changedProperty().setValue( Boolean.FALSE );
    }

}
