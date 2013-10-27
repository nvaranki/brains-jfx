package com.varankin.brains.jfx.analyser;

import java.util.Date;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * Линейка по горизонтальной оси времени. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
class TimeRuler extends AbstractRuler
{

    TimeRuler( int daWidth, long tMin, long tMax, TimeConvertor convertor )
    {
        //TODO appl. param.
        double factor = 2d; // 1, 2, 5
        int daStepMin = 10; // min 10 pixels in between ticks
        long step = (long)roundToFactor( ( tMax - tMin ) / ( daWidth / daStepMin ), factor );
        generate( daWidth, tMin, tMax, step, convertor );
    }

    private void generate( int daWidth, long tMin, long tMax, long step, TimeConvertor convertor )
    {
        long size = tMax - tMin;
        long offset = tMin % step;
        int stepCount = (int)Math.ceil( size / step );
        for( int i = 0; i <= stepCount; i++ )
        {
            long t = tMin - offset + step * i;
            int x = convertor.timeToImage( t );
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
    
}
