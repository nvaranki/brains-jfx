package com.varankin.brains.jfx.analyser;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Процесс динамического рисования отметок в графической зоне. 
 * Отметки рисуются блоками, по мере поступления в очередь.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class DrawAreaPainter implements Runnable
{
    private static final Logger LOGGER = Logger.getLogger( DrawAreaPainter.class.getName() );
    public static final int[][] DOT     = new int[][]{{0,0}};
    public static final int[][] BOX     = new int[][]{{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0},{-1,0},{-1,1}};
    public static final int[][] CROSS   = new int[][]{{0,0},{0,1},{1,0},{0,-1},{-1,0}};
    public static final int[][] CROSS45 = new int[][]{{0,0},{1,1},{1,-1},{-1,-1},{-1,1}};
    private static long id = 0L;

    private final WritableImage image;
    private final TimeConvertor timeConvertor;
    private final ValueConvertor valueConvertor;
    private final BlockingQueue<Dot> очередь;
    private final int fragmentSize;
    private final long fragmentTimeout;
    private final TimeUnit fragmentUnits;
    private final Color color;
    private final int[][] pattern;

    /**
     * @param image    графическая зона.
     * @param tc       функция X-координаты отметки от времени.
     * @param vc       функция Y-координаты отметки от значения.
     * @param color    цвет рисования шаблона.
     * @param pattern  шаблон для рисования как массив точек (x,y).
     * @param очередь  очередь отметок для прорисовки.
     */
    DrawAreaPainter( WritableImage image, TimeConvertor tc, ValueConvertor vc, 
            Color color, int[][] pattern, BlockingQueue<Dot> очередь )
    {
        this.image = image;
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

    @Override
    public void run()
    {
        LOGGER.log( Level.FINE, "DrawAreaPainter started: pool={0}, timeout={1} {2}", 
                new Object[]{ fragmentSize, fragmentTimeout, fragmentUnits.name() } );
        try
        {
            Thread.currentThread().setName( getClass().getSimpleName() + id++ );
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
    
    private void paint( Dot dot )
    {
        int x = timeConvertor.timeToImage( dot.t );
        int y = valueConvertor.valueToImage( dot.v );
        paint( x, y, image );
    }

    void paint( int x, int y, WritableImage image )
    {
        int width  = (int)Math.round( image.getWidth() );
        int height = (int)Math.round( image.getHeight() );
        PixelWriter writer = image.getPixelWriter();
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
    private class MultiDotTask extends Task<Void>
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
                DrawAreaPainter.this.paint( блок[i] );
            return null;
        }
        
    }
    
}
