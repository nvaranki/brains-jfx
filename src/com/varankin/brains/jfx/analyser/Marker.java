package com.varankin.brains.jfx.analyser;

/**
 *
 * @author Николай
 */
 enum Marker
{
    DOT( DotPainter.DOT ), 
    BOX( DotPainter.BOX ), 
    SPOT( DotPainter.SPOT ), 
    CROSS( DotPainter.CROSS ), 
    CROSS45( DotPainter.CROSS45 ), 
    TEE2H( DotPainter.TEE2H ), 
    TEE2V( DotPainter.TEE2V );
    
    public int[][] pattern;

    private Marker( int[][] pattern )
    {
        this.pattern = pattern;
    }

    @Override
    public String toString()
    {
        return name();
    }
    
}
