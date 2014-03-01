package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.*;
import javafx.concurrent.Task;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

/**
 * Процесс динамического рисования отметок в графической зоне. 
 * Отметки рисуются блоками, по мере поступления в очередь.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
class DotPainter
{
    private static final Logger LOGGER = Logger.getLogger( DotPainter.class.getName() );
    private static long idThread = 0L;
    
    public static final int[][] DOT     = new int[][]{{0,0}};
    public static final int[][] BOX     = new int[][]{{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0},{-1,1}};
    public static final int[][] SPOT    = new int[][]{{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0},{-1,1},{0,0}};
    public static final int[][] TEE2H   = new int[][]{{1,1},{1,0},{1,-1},{0,0},{-1,1},{-1,0},{-1,-1}};
    public static final int[][] TEE2V   = new int[][]{{-1,1},{0,1},{1,1},{0,0},{-1,-1},{0,-1},{1,-1}};
    public static final int[][] CROSS   = new int[][]{{0,0},{0,1},{1,0},{0,-1},{-1,0}};
    public static final int[][] CROSS45 = new int[][]{{0,0},{1,1},{1,-1},{-1,-1},{-1,1}};

    private final ObjectProperty<WritableImage> writableImage;
    private final ObjectProperty<ValueConvertor> valueConvertorProperty;
    private final ObjectProperty<TimeConvertor> timeConvertorProperty;
    private final BooleanProperty enabledProperty;
    private final ObjectProperty<Color> colorProperty;
    private final ObjectProperty<int[][]> patternProperty;
    private final ChangeListener<Boolean> enabledPropertyChangeListener;
    private final ChangeListener<WritableImage> imageChangeListener;
    private final BlockingQueue<Dot> очередь;
    private final int fragmentSize;
    private final long fragmentTimeout;
    private final TimeUnit fragmentUnits;

    private PixelWriter writer;
    private int width, height;

    /**
     * @param очередь  очередь отметок для прорисовки.
     */
    DotPainter( BlockingQueue<Dot> очередь )
    {
        writableImage = new SimpleObjectProperty<>();
        valueConvertorProperty = new SimpleObjectProperty<>();
        timeConvertorProperty = new SimpleObjectProperty<>();
        enabledProperty = new SimpleBooleanProperty();
        colorProperty = new SimpleObjectProperty<>();
        patternProperty = new SimpleObjectProperty<>();
        
        imageChangeListener = new ImageChangeListener();
        writableImage.addListener( new WeakChangeListener<>( imageChangeListener ) );
        enabledPropertyChangeListener = new EnabledPropertyChangeListener();
        enabledProperty.addListener( new WeakChangeListener<>( enabledPropertyChangeListener ) );
        
        this.очередь = очередь;
        //TODO appl. setup
        fragmentUnits = TimeUnit.MILLISECONDS;
        fragmentTimeout = 20L;
        fragmentSize = 50;
    }
    
    /**
     * @return свойство "область рисования".
     */
    final Property<WritableImage> writableImageProperty()
    {
        return writableImage;
    }
    
    /**
     * @return свойство "цвет рисования шаблона".
     */
    final Property<Color> colorProperty()
    {
        return colorProperty;
    }

    /**
     * @return свойство "шаблон для рисования как массив точек (x,y)".
     */
    final Property<int[][]> patternProperty()
    {
        return patternProperty;
    }
    
    /**
     * @return свойство "рисование точек".
     */
    final BooleanProperty enabledProperty()
    {
        return enabledProperty;
    }
    
    final Property<ValueConvertor> valueConvertorProperty()
    {
        return valueConvertorProperty;
    }

    final Property<TimeConvertor> timeConvertorProperty()
    {
        return timeConvertorProperty;
    }
    
    /**
     * @return очередь точек.
     */
    final Queue<Dot> queue()
    {
        return очередь;
    }
    
    /**
     * Рисует отметку в графической зоне.  
     * 
     * @param dot отметка.
     */
    protected void paint( Dot dot )
    {
        int x = timeConvertorProperty.get().timeToImage( dot.t );
        int y = valueConvertorProperty.get().valueToImage( dot.v );
        paint( x, y, colorProperty.get(), patternProperty.get(), writer, width, height );
    }

    /**
     * Рисует отметку в графической зоне.  
     * 
     * @param x       координата X центра отметки в графической зоне.
     * @param y       координата Y центра отметки в графической зоне.
     * @param color   цвет отметки.
     * @param pattern шаблон рисунка отметки.
     * @param writer  пиксельный рисовальщик графической зоны.
     * @param width   ширина графической зоны.
     * @param height  высота графической зоны.
     */
    static void paint( int x, int y, Color color, int[][] pattern, PixelWriter writer, int width, int height )
    {
        for( int[] offsets : pattern )
        {
            int xo = x + offsets[0];
            int yo = y + offsets[1];
            if( 0 <= xo && xo < width && 0 <= yo && yo < height )
                writer.setColor( xo, yo, color );
        }
    }

    /**
     * Рисовальщик блока отметок.
     */
    protected class MultiDotTask extends Task<Void>
    {
        private final Dot[] блок;
        private final int количество;

        /**
         * @param блок       последовательность отметок.
         * @param количество фактическое количество отметок.
         */
        MultiDotTask( Dot[] блок, int количество )
        {
            this.блок = блок;
            this.количество = количество;
        }

        @Override
        protected Void call() throws Exception
        {
            LOGGER.log( Level.FINEST, "Drawing {0} dots.", количество );
            for( int i = 0; i < количество; i++ )
                DotPainter.this.paint( блок[i] );
            return null;
        }
        
    }
    
    /**
     * Контроллер области рисования.
     */
    private class ImageChangeListener implements ChangeListener<WritableImage>
    {
        @Override
        public void changed( ObservableValue<? extends WritableImage> observable, 
                            WritableImage oldValue, WritableImage newValue )
        {
            width  = newValue.widthProperty().intValue();
            height = newValue.heightProperty().intValue();
            writer = newValue.getPixelWriter();
        }
    }

    /**
     * Старт-стоп контроллер прорисовки значений.
     */
    private class EnabledPropertyChangeListener implements ChangeListener<Boolean>
    {
        private Future<?> process;
        
        @Override
        public void changed( ObservableValue<? extends Boolean> observable, 
                            Boolean oldValue, Boolean newValue )
        {
            if( newValue != null && newValue )
            {
                // запустить прорисовку отметок
                process = JavaFX.getInstance().getExecutorService().submit( new ThreadedPainter() );
            }
            else if( process != null )
            {
                // остановить прорисовку отметок
                process.cancel( true );
            }
        }
    }
    
    private class ThreadedPainter implements Runnable
    {
        @Override
        public final void run()
        {
            LOGGER.log( Level.FINE, "DrawAreaPainter started: pool={0}, timeout={1} {2}", 
                    new Object[]{ fragmentSize, fragmentTimeout, fragmentUnits.name() } );
            try
            {
                Thread.currentThread().setName( getClass().getSimpleName() + idThread++ );
            }
            catch( SecurityException ex )
            {
                LOGGER.log( Level.FINE, "DrawAreaPainter failed to rename thread." );
            }

            try
            {                
                while( !Thread.interrupted() )
                {
                    Dot[] блок = new Dot[ Math.max( 1, fragmentSize ) ];
                    блок[0] = очередь.take();
                    int i = 1;
                    for( ; i < блок.length; i++ )
                    {
                        Dot dot = очередь.poll( fragmentTimeout, fragmentUnits );
                        if( dot != null )
                            блок[i] = dot;
                        else
                            break; 
                    }
                    Platform.runLater( new MultiDotTask( блок, i ) );
                }
                LOGGER.log( Level.FINE, "DrawAreaPainter finished." );
            } 
            catch( InterruptedException ex )
            {
                LOGGER.log( Level.FINE, "DrawAreaPainter interruped: {0}", ex.getMessage() );
            }
        }
    }
        
}
