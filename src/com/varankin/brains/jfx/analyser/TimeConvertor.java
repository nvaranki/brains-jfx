package com.varankin.brains.jfx.analyser;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Николай
 */
final class TimeConvertor
{
    static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
    
    private long t0, tSize, tExcess;
    private double tx;
    private long tEntry = -1L;

    TimeConvertor( long duration, long excess, int width )
    {
        tSize   = duration;
        tExcess = excess;
        tx = Double.valueOf( width - 1 ) / tSize;
    }
    
    int timeToImage( long t )
    {
        return (int)Math.round( ( t - t0 ) * tx );
    }

    int setEntry( long t )
    {
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

}
