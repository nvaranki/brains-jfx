package com.varankin.brains.jfx.analyser;

import java.util.Date;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * Линейка по горизонтальной оси времени. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
class TimeRuler extends AbstractRuler implements TimeConvertor
{
    private double t0, tx;
    private long offset;

    TimeRuler( int width, long tMin, long tMax )
    {
        //TODO appl. param.
        double factor = 2d; // 1, 2, 5
        int daStepMin = 10; // min 10 pixels in between ticks
        
        t0 = tMin;
        tx = Double.valueOf( width )/(tMax - tMin);
        long step = (long)roundToFactor( ( tMax - tMin ) / ( width / daStepMin ), factor );
        generate( width, tMin, tMax, step );
    }

    private void generate( int daWidth, long tMin, long tMax, long step )
    {
        long size = tMax - tMin;
        long offset = tMin % step;
        int stepCount = (int)Math.ceil( size / step );
        for( int i = 0; i <= stepCount; i++ )
        {
            long t = tMin - offset + step * i;
            int x = timeToImage( t );
            if( 0 <= x && x < daWidth )
                generate( x, Integer.toString( new Date( t ).getSeconds() ), t / step );
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
