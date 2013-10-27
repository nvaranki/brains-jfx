package com.varankin.brains.jfx.analyser;

import java.nio.IntBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;

/**
 * Графическая зона для рисования отметок. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class DrawArea extends WritableImage implements TimeConvertor, ValueConvertor
{
    private final int[] buffer;
    private double t0, tx, v0, vx;
    private int xAdopted, offset;
    private int pixelWidth, pixelHeight;
    private Color timeAxisColor, valueAxisColor, zeroValueAxisColor;

    DrawArea( int width, int height, float vMin, float vMax, long tMin, long tMax )
    {
        super( width, height );
        pixelWidth = width;
        pixelHeight = height;
        buffer = new int[pixelWidth*pixelHeight];
        timeAxisColor = Color.BLACK;
        valueAxisColor = Color.BLACK;
        zeroValueAxisColor = Color.GRAY;
        xAdopted = width - 10;
        t0 = tMin;
        tx = Double.valueOf( width )/(tMax - tMin);
        v0 = vMax;
        vx = Double.valueOf( height )/(vMin - vMax);
        axes( width, height, valueToImage( 0.0F ) );
    }

    final void axes( int width, int height, int zero )
    {
        PixelWriter writer = getPixelWriter();
        for( int i = 0; i < width; i++ ) 
            writer.setColor( i, 0, getTimeAxisColor() ); // timeline
        for( int i = 0; i < height; i++ ) 
            writer.setColor( 0, i, getValueAxisColor() ); // value
        if( 0 < zero && zero < pixelHeight )
        {
            for( int i = 1; i < width; i += 2 ) 
                writer.setColor( i, zero, getZeroValueAxisColor() ); // zero value
        }
    }

    @Override
    public int timeToImage( long t )
    {
        return offset + (int)Math.round( ( t - t0 ) * tx );
    }

    @Override
    public int valueToImage( float v )
    {
        return (int)Math.round( ( v - v0 ) * vx );
    }
    
    void mark( float v, long t, Color color, int[][] pattern )
    {
        int x = timeToImage( t );
        int y = valueToImage( v );
        double width = getWidth();
        double height = getHeight();
        PixelWriter writer = getPixelWriter();
        for( int[] offsets : pattern )
        {
            int xo = x + offsets[0];
            int yo = y + offsets[1];
            if( 0 <= xo && xo < width && 0 <= yo && yo < height )
                writer.setColor( xo, yo, color );
        }
    }

    void adopt( long now )
    {
        int shift = timeToImage( now ) - xAdopted;
        shift -= shift %2; // ensures dotted axis copied well  TODO verify approach;
        if( shift > 0 )
        {
            WritablePixelFormat<IntBuffer> pf = PixelFormat.getIntArgbInstance();
            getPixelReader().getPixels( shift + 1, 0, pixelWidth - shift - 1, pixelHeight, pf, buffer, 0, pixelWidth );
            getPixelWriter().setPixels( 1, 0, pixelWidth - shift - 1, pixelHeight, pf, buffer, 0, pixelWidth );
            offset -= shift;
        }
    }

    public Color getTimeAxisColor()
    {
        return timeAxisColor;
    }

    public void setTimeAxisPaint( Color color )
    {
        timeAxisColor = color;
    }

    public Color getValueAxisColor()
    {
        return valueAxisColor;
    }

    public void setValueAxisPaint( Color color )
    {
        valueAxisColor = color;
    }

    public Color getZeroValueAxisColor()
    {
        return zeroValueAxisColor;
    }

    public void setZeroValueAxisPaint( Color color )
    {
        zeroValueAxisColor = color;
    }

}
