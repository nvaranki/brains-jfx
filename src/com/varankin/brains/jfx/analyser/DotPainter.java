package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.Контекст;
import java.util.concurrent.*;
import java.util.logging.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.concurrent.Task;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

/**
 * Менеджер рисования отметок в графической зоне. 
 * Отметки рисуются блоками, по мере поступления в очередь.
 * 
 * @author &copy; 2019 Николай Варанкин
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
    }
    
    /**
     * @return свойство "графическая зона рисования".
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
     * @return свойство "рисование отметок".
     */
    final BooleanProperty enabledProperty()
    {
        return enabledProperty;
    }
    
    /**
     * @return свойство "масштаб значений".
     */
    final Property<ValueConvertor> valueConvertorProperty()
    {
        return valueConvertorProperty;
    }

    /**
     * @return свойство "масштаб времени".
     */
    final Property<TimeConvertor> timeConvertorProperty()
    {
        return timeConvertorProperty;
    }
    
    /**
     * @param значение отметка для прорисовки.
     * @param момент   момент времени возникновения отметки.
     * @return {@code true} если отметка поставлена в очередь, иначе {@code false}.
     */
    boolean offer( float значение, long момент )
    {
        return enabledProperty.get() && очередь.offer( new Dot( значение, момент ) );
    }
    
    /**
     * Рисует отметку в графической зоне.  
     * 
     * @param dot отметка.
     */
    protected void paint( Dot dot )
    {
        if( Float.isNaN( dot.v )) return;
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
        public void changed( ObservableValue<? extends WritableImage> __, 
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
        Future<?> process;
        
        @Override
        public void changed( ObservableValue<? extends Boolean> __, 
                            Boolean oldValue, Boolean newValue )
        {
            if( newValue != null && newValue )
            {
                // запустить прорисовку отметок
                process = JavaFX.getInstance().getExecutorService().submit( new DotStreamCutter() );
            }
            else if( process != null )
            {
                // остановить прорисовку отметок
                process.cancel( true );
            }
        }
    }
    
    /**
     * Экстрактор фрагментов потока отметок для прорисовки единым блоком.
     */
    private class DotStreamCutter implements Runnable
    {
        final int количество;
        final long пауза;
        final TimeUnit единица;

        DotStreamCutter()
        {
            Контекст контекст = JavaFX.getInstance().контекст;
            количество = Integer.valueOf( контекст.параметр( Контекст.Параметры.PAINTER_QUEUE ) );
            пауза = Long.valueOf( контекст.параметр( Контекст.Параметры.PAINTER_TIMEOUT ) );
            единица = TimeUnit.valueOf( контекст.параметр( Контекст.Параметры.PAINTER_UNIT ) );
        }
        
        /**
         * @param количество максимальное количество отметок в блоке на прорисовку.
         * @param пауза      время ожидания пустой очереди отметок.
         * @param единица    единица времени ожидания.
         */
        DotStreamCutter( int количество, long пауза, TimeUnit единица )
        {
            this.количество = количество;
            this.пауза = пауза;
            this.единица = единица;
        }

        @Override
        public final void run()
        {
            LOGGER.log( Level.FINE, "DrawAreaPainter started: pool={0}, timeout={1} {2}", 
                    new Object[]{ количество, пауза, единица.name() } );
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
                    Dot[] блок = new Dot[ Math.max( 1, количество ) ];
                    блок[0] = очередь.take();
                    int i = 1;
                    for( ; i < блок.length; i++ )
                    {
                        Dot dot = очередь.poll( пауза, единица );
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
