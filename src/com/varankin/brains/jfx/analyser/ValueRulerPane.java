package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * Линейка по вертикальной оси значений. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class ValueRulerPane extends AbstractRulerPane
{
    private final double tickShift;
    private final double factor = 1d; // 1, 2, 5
    private final int pixelStepMin = 5; // min 5 pixels in between ticks
    private final ValueConvertor convertor;
    @Deprecated private final ContextMenu popup;

    ValueRulerPane( ValueConvertor convertor )
    {
        this.convertor = convertor;
        //TODO appl. param.
        tickShift = 35d;
        
        popup = new ContextMenu();
        popup.getItems().addAll( new MenuItem("ValueRuler action") );
        
        setMinHeight( 100d );
        setOnMouseClicked( new ContextMenuRaiser( popup, ValueRulerPane.this ) );
        heightProperty().addListener( new SizeChangeListener() );
    }
    
    @Deprecated
    void appendToPopup( List<MenuItem> items ) 
    {
        if( items != null && !items.isEmpty() )
        {
            popup.getItems().add( new SeparatorMenuItem() );
            JavaFX.copyMenuItems( items, popup.getItems() );
        }
    }
    
    @Override
    protected void generateRuler()
    {
        getChildren().clear();
        
        float size = convertor.getSize();
        float step = (float)roundToFactor( size / ( getHeight() / pixelStepMin ), factor );
        float vStart = convertor.getMin();
        float offset = vStart % step;
        if( offset < 0 ) offset += step; // float!!! step = 0.1 => offset = -0.099999999
        vStart -= offset;
        int stepCount = (int)Math.ceil( size / step );
        for( int i = 0; i <= stepCount; i++ )
        {
            float v = vStart + step * i;
            int y = convertor.valueToImage( v );
            long f = Math.round( (double)v / step );
            if( 0 <= y && y < getHeight() )
                generateTickAndText( y, Float.toString( f * step ), f );
        }
    }

    private void generateTickAndText( int y, String text, long s )
    {
        int length = s % 10 == 0 ? getTickSizeLarge() : 
                s % 5 == 0 ? getTickSizeMedium() : getTickSizeSmall();
        Line tick = new Line( 0, 0, length, 0 );
        tick.getTransforms().add( new Translate( getTickSizeLarge(), 0 ) );
        tick.getTransforms().add( new Scale( -1, 1 ) );
        tick.relocate( tickShift, y );
        tick.setStroke( getTickPaint() );
        getChildren().add( tick );
        if( s % 10 == 0 || s % 5 == 0 )
        {
            Text value = new Text( text );
            value.relocate( shiftToRightAlign( value ), y );
            value.setFill( getValuePaint() );
            if( y + value.boundsInLocalProperty().get().getMaxY() < getHeight() )
                getChildren().add( value );
        }
    }

    private double shiftToRightAlign( Text value )
    {
        Bounds tickBounds = getBoundsInLocal();
        double tickRight = tickBounds.getMaxX();
        Bounds valueBounds = value.getBoundsInLocal();
        double valueRight = valueBounds.getMaxX();
        double blank = valueBounds.getMaxY(); //TODO verify approach
        double shift = tickRight - 10d - valueRight - blank;
        return shift;
    }
    
}
