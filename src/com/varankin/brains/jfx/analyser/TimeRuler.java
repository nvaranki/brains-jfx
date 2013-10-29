package com.varankin.brains.jfx.analyser;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * Линейка по горизонтальной оси времени. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
class TimeRuler extends AbstractRuler implements TimeConvertor
{
    private final double factor = 1d; // 1, 2, 5
    private final int pixelStepMin = 10; // min 10 pixels in between ticks
    @Deprecated private long offset;
    private double t0, tx;

    TimeRuler( final long tMin, final long tMax )
    {
        setMinWidth( 100d );
        widthProperty().addListener( new InvalidationListener() 
        {
            @Override
            public void invalidated( Observable o )
            {
                int width  = (int)Math.round( widthProperty().get() );
                if( width > 0 )
                {
                    resetConvertor( tMin, tMax );
                    getChildren().clear();
                    generate( tMin, tMax );
                }
            }
    } );
    }

    private void resetConvertor( long tMin, long tMax )
    {
        t0 = tMin;
        tx = Double.valueOf( getWidth() )/( tMax - tMin );
    }

    private void generate( long tMin, long tMax )
    {
        long step = (long)roundToFactor( ( tMax - tMin ) / ( getWidth() / pixelStepMin ), factor );
        long size = tMax - tMin;
        long tStart = tMin - tMin % step;
        int stepCount = (int)Math.ceil( size / step );
        for( int i = 0; i <= stepCount; i++ )
        {
            long t = tStart + step * i;
            int x = timeToImage( t );
            if( 0 <= x && x < getWidth() )
                generate( x, Long.toString( (t%60000L)/1L/*new Date( t ).getSeconds()*/ ), t / step );
        }
    }

    private void generate( int x, String text, long s )
    {
        int length = s % 10 == 0 ? getTickSizeLarge() : 
                s % 5 == 0 ? getTickSizeMedium() : getTickSizeSmall();
        Line tick = new Line( 0, 0, 0, length );
        tick.setStroke( getTickPaint() );
        tick.relocate( x, 0d );
        getChildren().add( tick );
        if( s % 10 == 0 || s % 5 == 0 )
        {
            Text value = new Text( text );
            value.relocate( x, 10d );
            value.setFill( getValuePaint() );
            if( x + value.boundsInLocalProperty().get().getMaxX() < getWidth() )
                getChildren().add( value );
        }
    }
    
    @Override
    public int timeToImage( long t )
    {
        return (int)( offset + Math.round( ( t - t0 ) * tx ) );
    }

    @Override
    public void setOffset( long value )
    {
        offset = value;
    }

    @Override
    public long getOffset()
    {
        return offset;
    }

}
