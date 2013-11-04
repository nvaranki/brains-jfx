package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import java.nio.IntBuffer;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;

/**
 *
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class TimeLineController
{
    private final DoubleProperty widthProperty, heightProperty;
    private final ReadOnlyObjectWrapper<WritableImage> writableImageProperty;
    private final SimpleBooleanProperty dynamicProperty;
    private final ScheduledExecutorService scheduledExecutorService;
    private final ExecutorService executorService;
    private Runnable refreshService;
    private long refreshRate;
    private TimeUnit refreshRateUnit;
    private final TimeConvertor timeConvertor;
    private final ValueConvertor valueConvertor;
    private int timeAxisOffset;
    private Color timeAxisColor, valueAxisColor, zeroValueAxisColor;
    private final WritablePixelFormat<IntBuffer> pixelFormat;
    private final int blank[][];
    private final Map<Object,Boolean> flowState;
    private final Map<Object,Future<?>> flowProcesses;

    public TimeLineController( JavaFX jfx, TimeConvertor timeRuler, ValueConvertor valueRuler )
    {
        writableImageProperty = new ReadOnlyObjectWrapper<>();
        widthProperty = new SimpleDoubleProperty();
        widthProperty.addListener( new ChangeListener<Number>() 
        {
            @Override
            public void changed( ObservableValue<? extends Number> ov, Number oldValue, Number newValue )
            {
                timeConvertor.reset( newValue.doubleValue(), dynamicProperty.get() ? 
                            System.currentTimeMillis() : timeConvertor.getEntry() );
                replaceImage( newValue.intValue(), heightProperty.intValue() );
            }
        } );
        heightProperty = new SimpleDoubleProperty();
        heightProperty.addListener( new ChangeListener<Number>() 
        {
            @Override
            public void changed( ObservableValue<? extends Number> ov, Number oldValue, Number newValue )
            {
                valueConvertor.reset( newValue.intValue() );
                replaceImage( widthProperty.intValue(), newValue.intValue() );
            }
        } );
        dynamicProperty = new SimpleBooleanProperty();
        dynamicProperty.addListener( new ChangeListener<Boolean>() 
        {
            private Future<?> process;

            @Override
            public void changed( ObservableValue<? extends Boolean> observable, 
                                Boolean oldValue, Boolean newValue ) 
            {
                if( newValue != null )
                    if( newValue )
                    {
                        // возобновить движение графической зоны
                        process = scheduledExecutorService.scheduleAtFixedRate( 
                            refreshService, 0L, refreshRate, refreshRateUnit );
                    }
                    else
                    {
                    // остановить движение графической зоны
                    if( process != null )
                        process.cancel( true );
                    }
            }
        } );
        scheduledExecutorService = jfx.getScheduledExecutorService();
        executorService = jfx.getExecutorService();
        //TODO appl param
        refreshRate = 100L; // ms
        refreshRateUnit = TimeUnit.MILLISECONDS;
        refreshService = new RefreshService();
        //DEBUG START
        timeAxisColor = Color.BLACK;
        valueAxisColor = Color.BLACK;
        zeroValueAxisColor = Color.GRAY;
        //DEBUG END
        pixelFormat = PixelFormat.getIntArgbInstance();
        blank = new int[2][];
        this.timeConvertor = timeRuler;
        this.valueConvertor = valueRuler;
        flowState = new IdentityHashMap<>(); //TODO DEBUG Identity
        flowProcesses = new IdentityHashMap<>(); //TODO DEBUG Identity        
    }

    ReadOnlyObjectProperty<WritableImage> writableImageProperty()
    {
        return writableImageProperty.getReadOnlyProperty();
    }
    
    DoubleProperty widthProperty()
    {
        return widthProperty;
    }
    
    DoubleProperty heightProperty()
    {
        return heightProperty;
    }
    
    BooleanProperty dynamicProperty()
    {
        return dynamicProperty;
    }

    TimeConvertor getTimeConvertor()
    {
        return timeConvertor;
    }

    ValueConvertor getValueConvertor()
    {
        return valueConvertor;
    }
    
    private void replaceImage( int width, int height )
    {
        if( width > 0 && height > 0 )
        {
            // заменить, т.к. другие размеры
            WritableImage newWritableImage = new WritableImage( width, height );
            // нарисовать оси 
            int zero = valueConvertor.valueToImage( 0.0F );
            drawAxes( newWritableImage, width, height, zero );
            snapshotBlankPatterns( newWritableImage, height, zero, 2, 1 );
            //setImage( null );
//            getTransforms().clear();
//            getChildren().clear();
//            view.setImage( writableImage = newWritableImage );
//            view.setPreserveRatio( true );
//            getTransforms().add( new Translate( 0, height ) );
//            getTransforms().add( new Scale( 1, -1 ) );
////            setImage( writableImage = newWritableImage );
//            getChildren().add( view );
            writableImageProperty.setValue( newWritableImage );
        }
    }
    
    private void drawAxes( WritableImage writableImage, int width, int height, int zero )
    {
        PixelWriter writer = writableImage.getPixelWriter();
        //TODO option BORDER t,r,b,l
//        for( int i = 0; i < width; i++ ) 
//            writer.setColor( i, 0, getTimeAxisColor() ); // timeline
//        for( int i = 0; i < height; i++ ) 
//            writer.setColor( 0, i, getValueAxisColor() ); // value
        if( 0 < zero && zero < heightProperty.intValue() )
        {
            for( int i = 2; i < width; i += 2 ) 
                writer.setColor( i, zero, getZeroValueAxisColor() ); // zero value
        }
    }
    
    private void snapshotBlankPatterns( WritableImage writableImage, int height, int zero, int... ys )
    {
        PixelReader reader = writableImage.getPixelReader();
        for( int i = 0; i < blank.length && i < ys.length; i++ )
            reader.getPixels( ys[i], 0, 1, height, pixelFormat, blank[i] = new int[height], 0, 1 );
    }
    
    boolean getFlowState( Object o )
    {
        Boolean flows = flowState.get( o );
        return flows != null ? flows : true;
    }

    void setFlowState( Object o, boolean flows )
    {
        flowState.put( o, flows );
    }

    Future<?> startFlow( Object o, DotPainter painter )
    {
        Future<?> process = executorService.submit( painter );
        flowProcesses.put( o, process );
        return process;
    }
    
    boolean stopFlow( Object o )
    {
        Future<?> process = flowProcesses.get( o );
        return process == null || process.cancel( true );
    }
    
    void removeFlow( Object o )
    {
        flowProcesses.remove( o );
    }
    
    long getRefreshRate()
    {
        return refreshRate;
    }

    void setRefreshRate( long rate )
    {
        refreshRate = rate;
    }

    TimeUnit getRefreshRateUnit()
    {
        return refreshRateUnit;
    }

    void setRefreshRateUnit( TimeUnit unit )
    {
        refreshRateUnit = unit;
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
            int shift = timeConvertor.reset( widthProperty().doubleValue(), moment );//timeConvertor.timeToImageShift( moment );
            if( shift > 0 )
            {
//                System.out.println( "time shift=" +(moment -timeConvertor.getEntry()));
//                System.out.println( "area shift=" +(shift));
//                System.out.println( "time shift adj=" +(timeConvertor.imageToTimeShift(shift)));
                // подправить расчет
                //timeConvertor.reset( widthProperty().doubleValue(), moment );
                // смещение всей зоны; +-1 для сохранения оси значений
                shiftImage( shift );
            }
        }
        
        private void shiftImage( int shift )
        {
            PixelWriter writer = writableImageProperty.get().getPixelWriter();
            int pixelWidth = widthProperty.intValue();
            int pixelHeight = heightProperty.intValue();
            int width = pixelWidth - shift - 1;
            if( width > 0 )
                writer.setPixels( 1, 0, width, pixelHeight, writableImageProperty.get().getPixelReader(), shift + 1, 0 );
            // очистить справа от скопированной зоны
            timeAxisOffset += shift;
            timeAxisOffset %= 2;
            for( int i = Math.max( 1, pixelWidth - shift ); i < pixelWidth; i++ )
                writer.setPixels( i, 0, 1, pixelHeight, pixelFormat, blank[timeAxisOffset^(i%2)], 0, 1 );
        }

    }
    
}
