package com.varankin.brains.jfx.analyser;

/**
 *
 * @author Николай
 */
interface TimeConvertor
{

    int timeToImage( long t );
    
    void setOffset( long value );
    
    long getOffset();
    
}
