package com.varankin.brains.jfx.analyser;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Николай
 */
final class TimeConvertor
{
    private long t0, tSize, tExcess;
    private double tx;
    private long tEntry = -1L;

    TimeConvertor( long duration, long excess, TimeUnit unit )
    {
        tSize   = TimeUnit.MILLISECONDS.convert( duration, unit );
        tExcess = TimeUnit.MILLISECONDS.convert( excess, unit );
    }
    
    int timeToImage( long t )
    {
        return (int)Math.round( ( t - t0 ) * tx );
    }

    void reset( long duration, long excess, TimeUnit unit )
    {
        tSize   = TimeUnit.MILLISECONDS.convert( duration, unit );
        tExcess = TimeUnit.MILLISECONDS.convert( excess, unit );
    }
    
    int reset( double width, long t )
    {
        tx = width / tSize;
        
        int shift;
        if( tEntry < 0L )
        {
            shift = 0;
            tEntry = t;
        }
        else 
        {
            shift = (int)Math.round(  tx * ( t - tEntry ) );
            if( shift > 0 )
                tEntry += Math.round( shift / tx );
        }
        
        t0 = tEntry + tExcess - tSize;
        
        return shift;
    }
    
    long getEntry()
    {
        return tEntry;
    }

    long getSize()
    {
        return tSize;
    }

    long getExcess()
    {
        return tExcess;
    }

}
