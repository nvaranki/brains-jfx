package com.varankin.brains.jfx.analyser;

import java.nio.IntBuffer;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * Графическая зона для рисования отметок. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
class DrawArea extends ImageView
{
    private final DoubleProperty widthProperty, heightProperty;
    private final TimeConvertor timeConvertor;
    private final ValueConvertor valueConvertor;
    private final WritablePixelFormat<IntBuffer> pixelFormat;
    private final int blank[][];
    private WritableImage writableImage;
    private Color timeAxisColor, valueAxisColor, zeroValueAxisColor;
    @Deprecated private int xAdopted;

    DrawArea( TimeRuler timeRuler, ValueRuler valueRuler )
    {
        setPreserveRatio( true );
        //DEBUG START
        timeAxisColor = Color.BLACK;
        valueAxisColor = Color.BLACK;
        zeroValueAxisColor = Color.GRAY;
        //DEBUG END
        timeConvertor = timeRuler;
        valueConvertor = valueRuler;
        pixelFormat = PixelFormat.getIntArgbInstance();
        blank = new int[2][];

        widthProperty = new SimpleDoubleProperty();
        widthProperty.bind( timeRuler.widthProperty() );
        widthProperty.addListener( new ChangeListener<Number>() 
        {
            @Override
            public void changed( ObservableValue<? extends Number> ov, Number oldValue, Number newValue )
            {
                resize( newValue.intValue(), heightProperty.intValue() );
            }
        } );
        heightProperty = new SimpleDoubleProperty();
        heightProperty.bind( valueRuler.heightProperty() );
        heightProperty.addListener( new ChangeListener<Number>() 
        {
            @Override
            public void changed( ObservableValue<? extends Number> ov, Number oldValue, Number newValue )
            {
                resize( widthProperty.intValue(), newValue.intValue() );
            }
        } );
    }
    
    private void resize( int width, int height )
    {
        if( width > 0 && height > 0 )
        {
            //TODO copy drawn area
            setImage( writableImage = new WritableImage( width, height ) );
            getTransforms().clear();
            getTransforms().add( new Translate( 0, height ) );
            getTransforms().add( new Scale( 1, -1 ) );
            xAdopted = width - 10;
            axes( width, height );
        }
    }
    
    final void axes( int width, int height )
    {
        PixelWriter writer = writableImage.getPixelWriter();
        for( int i = 0; i < width; i++ ) 
            writer.setColor( i, 0, getTimeAxisColor() ); // timeline
        for( int i = 0; i < height; i++ ) 
            writer.setColor( 0, i, getValueAxisColor() ); // value
        int zero = valueConvertor.valueToImage( 0.0F );
        if( 0 < zero && zero < heightProperty.intValue() )
        {
            for( int i = 2; i < width; i += 2 ) 
                writer.setColor( i, zero, getZeroValueAxisColor() ); // zero value
        }
        PixelReader reader = writableImage.getPixelReader();
        reader.getPixels( 2, 0, 1, height, pixelFormat, blank[0] = new int[height], 0, 1 );
        reader.getPixels( 1, 0, 1, height, pixelFormat, blank[1] = new int[height], 0, 1 );
    }

    final WritableImage getWritableImage()
    {
        return writableImage;
    }

    final Runnable newRefreshServiceInstance()
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
            int shift = timeConvertor.timeToImage( moment ) - xAdopted;
            if( shift > 0 )
            {
                // подправить расчет
                long offset = timeConvertor.getOffset() - shift;
                timeConvertor.setOffset( offset );
                // смещение всей зоны; +-1 для сохранения оси значений
                PixelWriter writer = writableImage.getPixelWriter();
                int pixelWidth = widthProperty.intValue();
                int pixelHeight = heightProperty.intValue();
                int width = pixelWidth - shift - 1;
                if( width > 0 )
                    writer.setPixels( 1, 0, width, pixelHeight, writableImage.getPixelReader(), shift + 1, 0 );
                // очистить справа от скопированной зоны
                for( int i = Math.max( 1, pixelWidth - shift ); i < pixelWidth; i++ )
                    writer.setPixels( i, 0, 1, pixelHeight, pixelFormat, blank[(int)(-offset%2)^(i%2)], 0, 1 );
            }
        }
    }
    
}
