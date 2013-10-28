package com.varankin.brains.jfx.analyser;

import javafx.geometry.Bounds;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * Линейка по вертикальной оси значений. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
class ValueRuler extends AbstractRuler implements ValueConvertor
{
    private final double tickShift;
    private double v0, vx;

    ValueRuler( int height, float vMin, float vMax )
    {
        //TODO appl. param.
        tickShift = 35d;
        double factor = 1d; // 1, 2, 5
        int daStepMin = 5; // min 5 pixels in between ticks
        
        v0 = vMax;
        vx = Double.valueOf( height )/(vMin - vMax);
        float step = (float)roundToFactor( ( vMax - vMin ) / ( height / daStepMin ), factor );
        generate( height, vMin, vMax, step );
    }

    private void generate( int daHeight, float vMin, float vMax, float step )
    {
        float size = vMax - vMin;
        float offset = vMin % step;
        int stepCount = (int)Math.ceil( size / step );
        for( int i = 0; i <= stepCount; i++ )
        {
            float v = vMin - offset + step * i;
            int y = valueToImage( v );
            long f = Math.round( (double)v / step );
            if( 0 <= y && y < daHeight )
                generate( y, Float.toString( f * step ), f );
        }
    }

    private void generate( int y, String text, long s )
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
    
    @Override
    public int valueToImage( float v )
    {
        return (int)Math.round( ( v - v0 ) * vx );
    }
    
}
