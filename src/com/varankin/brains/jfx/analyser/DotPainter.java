package com.varankin.brains.jfx.analyser;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Процесс динамического рисования отметок в графической зоне. 
 * Отметки рисуются блоками, по мере поступления в очередь.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
class DotPainter implements Runnable
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
    private final TimeConvertor timeConvertor;
    private final ValueConvertor valueConvertor;
    private final BlockingQueue<Dot> очередь;
    private final int fragmentSize;
    private final long fragmentTimeout;
    private final TimeUnit fragmentUnits;
    private Color color;
    private int[][] pattern;

    /**
     * @param tc       функция X-координаты отметки от времени.
     * @param vc       функция Y-координаты отметки от значения.
     * @param color    цвет рисования шаблона.
     * @param pattern  шаблон для рисования как массив точек (x,y).
     * @param очередь  очередь отметок для прорисовки.
     */
    DotPainter( TimeConvertor tc, ValueConvertor vc, 
            Color color, int[][] pattern, BlockingQueue<Dot> очередь )
    {
        this.writableImage = new SimpleObjectProperty<>();
        this.timeConvertor = tc;
        this.valueConvertor = vc;
        this.color = color;
        this.pattern = pattern;
        this.очередь = очередь;
        //TODO appl. setup
        fragmentUnits = TimeUnit.MILLISECONDS;
        fragmentTimeout = 20L;
        fragmentSize = 50;
    }

    ObjectProperty<WritableImage> writableImageProperty()
    {
        return writableImage;
    }
    
    final Color getColor()
    {
        return color;
    }
    
    final void setColor( Color color )
    {
        this.color = color;
    }

    final int[][] getPattern()
    {
        return pattern;
    }
    
    final void setPattern( int[][] pattern )
    {
        this.pattern = pattern;
    }

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
    
    protected void paint( Dot dot )
    {
        int x = timeConvertor.timeToImage( dot.t );
        int y = valueConvertor.valueToImage( dot.v );
        paint( x, y, writableImage.get(), color, pattern );
    }

    static void paint( int x, int y, WritableImage image, Color color, int[][] pattern )
    {
        int width  = image.widthProperty().intValue();
        int height = image.heightProperty().intValue();
        PixelWriter writer = image.getPixelWriter();
        for( int[] offsets : pattern )
        {
            int xo = x + offsets[0];
            int yo = y + offsets[1];
            if( 0 <= xo && xo < width && 0 <= yo && yo < height )
                writer.setColor( xo, yo, color );
        }
    }
    
    Image sample()
    {
        return sample( color, pattern );
    }
    
    static Image sample( Color color, int[][] pattern )
    {
        Color outlineColor = Color.LIGHTGRAY;
        
        WritableImage sample = new WritableImage( 16, 16 );
        for( int i = 1; i < 15; i ++ )
        {
            sample.getPixelWriter().setColor( i,  0, outlineColor );
            sample.getPixelWriter().setColor( i, 15, outlineColor );
            sample.getPixelWriter().setColor(  0, i, outlineColor );
            sample.getPixelWriter().setColor( 15, i, outlineColor );
        }
        paint( 7, 7, sample, color, pattern );

        return sample;        
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
    
}