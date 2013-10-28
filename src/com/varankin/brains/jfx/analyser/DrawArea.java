package com.varankin.brains.jfx.analyser;

import java.nio.IntBuffer;
import javafx.application.Platform;
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
final class DrawArea extends WritableImage
{
    private final int blank[][];
    private final TimeConvertor timeConvertor;
    private final ValueConvertor valueConvertor;
    private int xAdopted;
    private int pixelWidth, pixelHeight;//, zero;
    private Color timeAxisColor, valueAxisColor, zeroValueAxisColor;
    private final WritablePixelFormat<IntBuffer> pixelFormat;

    DrawArea( int width, int height, TimeConvertor tc, ValueConvertor vc )
    {
        super( width, height );
        timeConvertor = tc;
        valueConvertor = vc;
        pixelFormat = PixelFormat.getIntArgbInstance();
        pixelWidth = width;
        pixelHeight = height;
        timeAxisColor = Color.BLACK;
        valueAxisColor = Color.BLACK;
        zeroValueAxisColor = Color.GRAY;
        xAdopted = width - 10;

        axes( width, height );

        blank = new int[][]{ new int[pixelHeight], new int[pixelHeight] };
        getPixelReader().getPixels( 2, 0, 1, pixelHeight, pixelFormat, blank[0], 0, 1 );
        getPixelReader().getPixels( 1, 0, 1, pixelHeight, pixelFormat, blank[1], 0, 1 );
    }

    final void axes( int width, int height )
    {
        PixelWriter writer = getPixelWriter();
        for( int i = 0; i < width; i++ ) 
            writer.setColor( i, 0, getTimeAxisColor() ); // timeline
        for( int i = 0; i < height; i++ ) 
            writer.setColor( 0, i, getValueAxisColor() ); // value
        int zero = valueConvertor.valueToImage( 0.0F );
        if( 0 < zero && zero < pixelHeight )
        {
            for( int i = 2; i < width; i += 2 ) 
                writer.setColor( i, zero, getZeroValueAxisColor() ); // zero value
        }
    }

    private void adopt( long now )
    {
        int shift = timeConvertor.timeToImage( now ) - xAdopted;
        if( shift > 0 )
        {
            // подправить расчет
            long offset = timeConvertor.getOffset() - shift;
            timeConvertor.setOffset( offset );
            // смещение всей зоны; +-1 для сохранения оси значений
            PixelWriter writer = getPixelWriter();
            int width = pixelWidth - shift - 1;
            if( width > 0 )
                writer.setPixels( 1, 0, width, pixelHeight, getPixelReader(), shift + 1, 0 );
            // очистить справа от скопированной зоны
            for( int i = Math.max( 1, pixelWidth - shift ); i < pixelWidth; i++ )
                writer.setPixels( i, 0, 1, pixelHeight, pixelFormat, blank[(int)(-offset%2)^(i%2)], 0, 1 );
        }
    }

    Runnable newRefreshServiceInstance()
    {
        return new RefreshService();
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

    /**
     * Сервис движения временной шкалы.
     */
    private class RefreshService implements Runnable
    {
        @Override
        public void run()
        {
            Platform.runLater( new Adopter( System.currentTimeMillis() ) ); 
        }
    }
    
    /**
     * Контроллер сдвига временной шкалы.
     */
    private class Adopter implements Runnable
    {
        private final long moment;

        Adopter( long moment )
        {
            this.moment = moment;
        }
        
        @Override
        public void run()
        {
            adopt( moment );
            //timeRuler.adopt( moment );
        }
        
    }
    
}
