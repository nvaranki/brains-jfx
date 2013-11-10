package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import java.util.concurrent.Future;
import javafx.beans.value.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.util.Builder;

/**
 * FXML-контроллер элемента управления прорисовкой отметок.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class LegendValueController implements Builder<Control>
{
    private static final String RESOURCE_CSS  = "/fxml/analyser/LegendValue.css";
    private static final String CSS_CLASS = "legend-value";
    
    private final ColorPropertyChangeListener colorPropertyChangeListener;
    private final PatternPropertyChangeListener patternPropertyChangeListener;
    private final SelectedPropertyChangeListener selectedPropertyChangeListener;

    private DotPainter painter;
    
    @FXML private CheckBox legend;

    public LegendValueController()
    {
        colorPropertyChangeListener = new ColorPropertyChangeListener();
        patternPropertyChangeListener = new PatternPropertyChangeListener();
        selectedPropertyChangeListener = new SelectedPropertyChangeListener();
    }

    /**
     * Создает элемент управления прорисовкой отметок.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public CheckBox build()
    {
        legend = new CheckBox();
        legend.setSelected( false );

        legend.getStyleClass().add( CSS_CLASS );
        legend.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return legend;
    }
    
    @FXML
    protected void initialize()
    { 
        legend.selectedProperty().addListener( new WeakChangeListener<>( selectedPropertyChangeListener ) );
    }

    private void resample( Color color, int[][] pattern )
    {
        if( color != null && pattern != null )
        {
            Color outlineColor = Color.LIGHTGRAY;
            WritableImage sample = new WritableImage( 16, 16 );
            PixelWriter pixelWriter = sample.getPixelWriter();
            for( int i = 1; i < 15; i ++ )
            {
                pixelWriter.setColor( i,  0, outlineColor );
                pixelWriter.setColor( i, 15, outlineColor );
                pixelWriter.setColor(  0, i, outlineColor );
                pixelWriter.setColor( 15, i, outlineColor );
            }
            DotPainter.paint( 7, 7, color, pattern, pixelWriter, 16, 16 );
            legend.setGraphic( new ImageView( sample ) );
        }
    }

    void setPainter( DotPainter painter )
    {
        this.painter = painter;
        painter.colorProperty().addListener( new WeakChangeListener<>( colorPropertyChangeListener ) );
        painter.patternProperty().addListener( new WeakChangeListener<>( patternPropertyChangeListener ) );
        resample( painter.colorProperty().getValue(), painter.patternProperty().getValue() );
    }
        
    private class ColorPropertyChangeListener implements ChangeListener<Color>
    {
        @Override
        public void changed( ObservableValue<? extends Color> observable, 
                            Color oldValue, Color newValue )
        {
            resample( newValue, painter.patternProperty().getValue() );
        }
    }

    private class PatternPropertyChangeListener implements ChangeListener<int[][]>
    {
        @Override
        public void changed( ObservableValue<? extends int[][]> observable, 
                            int[][] oldValue, int[][] newValue )
        {
            resample( painter.colorProperty().getValue(), newValue );
        }
    }
        
    private class SelectedPropertyChangeListener implements ChangeListener<Boolean>
    {
        private Future<?> process;
        
        @Override
        public void changed( ObservableValue<? extends Boolean> observable, 
                            Boolean oldValue, Boolean newValue )
        {
            if( newValue != null && newValue )
            {
                // запустить прорисовку отметок
                process = JavaFX.getInstance().getExecutorService().submit( painter );
            }
            else if( process != null )
            {
                // остановить прорисовку отметок
                process.cancel( true );
            }
        }
    }
        
}
