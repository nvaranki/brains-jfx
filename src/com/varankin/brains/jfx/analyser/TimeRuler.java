package com.varankin.brains.jfx.analyser;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * Линейка по горизонтальной оси времени. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class TimeRuler extends AbstractRuler implements TimeConvertor
{
    private final double factor = 1d; // 1, 2, 5
    private final int pixelStepMin = 10; // min 10 pixels in between ticks
    private double t0, tx;
    private long tSize, tEntry, tExcess;
    private final SimpleBooleanProperty relativeProperty;

    TimeRuler( long duration, long excess, TimeUnit unit )
    {
        setMinWidth( 100d );
        tSize = TimeUnit.MILLISECONDS.convert( duration, unit );
        tExcess = TimeUnit.MILLISECONDS.convert( excess, unit );
        relativeProperty = new SimpleBooleanProperty();
        relativeProperty.addListener( new ChangeListener<Boolean>() 
        {
            @Override
            public void changed( ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue )
            {
                double width = widthProperty().doubleValue();
                if( width > 0d ) 
                {
                    getChildren().clear();
                    generateRuler( width );
                }
            }
        } );
        widthProperty().addListener( new InvalidationListener() 
        {
            @Override
            public void invalidated( Observable o )
            {
                int width  = widthProperty().intValue();
                if( width > 0 )
                {
                    resetConvertor( width, relativeProperty.get() ? System.currentTimeMillis() : tEntry );
                    getChildren().clear();
                    generateRuler( width );
                }
            }
    } );
    }
    
    int evaluateImageShiftTo( long moment )
    {
        return (int)Math.round(  tx * ( moment - tEntry ) );
    }

    void resetConvertor( double width, long moment )
    {
        tEntry = moment;
        t0 = tEntry + tExcess - tSize;
        tx = width / tSize;
    }

    private void generateRuler( double width )
    {
        boolean relative = relativeProperty.get();
        DateFormat formatter = DateFormat.getTimeInstance();
        long step = (long)roundToFactor( tSize / ( width / pixelStepMin ), factor );
        for( int i = 1, count = (int)Math.ceil( tExcess / step ); i <= count; i++ )
        {
            long t = step * i;
            int x = timeToImage( tEntry + t );
            if( 0 <= x && x < getWidth() )
                generateTickAndText( x, relative ?
                        Long.toString( t/1000L/*new Date( t ).getSeconds()*/ ) :
                        formatter.format( new Date( tEntry + t ) ), i, relative );
        }
        for( int i = 0, count = (int)Math.ceil( ( tSize - tExcess ) / step ); i <= count; i++ )
        {
            long t = - step * i;
            int x = timeToImage( tEntry + t );
            if( 0 <= x && x < getWidth() )
                generateTickAndText( x, relative ?
                        Long.toString( t/1000L/*new Date( t ).getSeconds()*/ ) :
                        formatter.format( new Date( tEntry + t ) ), i, relative );
        }
    }

    private void generateTickAndText( int x, String text, long s, boolean relative )
    {
        int length = s % 10 == 0 ? getTickSizeLarge() : 
                s % 5 == 0 ? getTickSizeMedium() : getTickSizeSmall();
        Line tick = new Line( 0, 0, 0, length );
        tick.setStroke( getTickPaint() );
        tick.relocate( x, 0d );
        getChildren().add( tick );
        if( s % 10 == 0 || ( s % 5 == 0 && relative ) )
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
        return (int)Math.round( ( t - t0 ) * tx );
    }

    BooleanProperty relativeProperty()
    {
        return relativeProperty;
    }
    
}
