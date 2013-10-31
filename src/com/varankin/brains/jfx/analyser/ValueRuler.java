package com.varankin.brains.jfx.analyser;

import static com.varankin.brains.jfx.analyser.AbstractRuler.roundToFactor;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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
final class ValueRuler extends AbstractRuler implements ValueConvertor
{
    private final double tickShift;
    private final double factor = 1d; // 1, 2, 5
    private final int pixelStepMin = 5; // min 5 pixels in between ticks
    private double v0, vx;

    ValueRuler( final float vMin, final float vMax )
    {
        //TODO appl. param.
        tickShift = 35d;
        
        setMinHeight( 100d );
        heightProperty().addListener( new InvalidationListener() 
        {
            @Override
            public void invalidated( Observable o )
            {
                int height = (int)Math.round( heightProperty().get() );
                if( height > 0 )
                {
                    resetConvertor( vMin, vMax );
                    getChildren().clear();
                    generate( vMin, vMax );
                }
            }
        } );
    }
    
    private void resetConvertor( float vMin, float vMax )
    {
        v0 = vMax;
        vx = Double.valueOf( getHeight() )/( vMin - vMax );
    }

    private void generate( float vMin, float vMax )
    {
        float step = (float)roundToFactor( ( vMax - vMin ) / ( getHeight() / pixelStepMin ), factor );
        float size = vMax - vMin;
        float vStart = vMin - vMin % step;
        int stepCount = (int)Math.ceil( size / step );
        for( int i = 0; i <= stepCount; i++ )
        {
            float v = vStart + step * i;
            int y = valueToImage( v );
            long f = Math.round( (double)v / step );
            if( 0 <= y && y < getHeight() )
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
    
    @Override
    public int valueToImage( float v )
    {
        return (int)Math.round( ( v - v0 ) * vx );
    }
    
}
