package com.varankin.brains.jfx.analyser;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * Процесс динамического рисования отметок в графической зоне. 
 * Отметки рисуются блоками, по мере поступления в очередь.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class DrawAreaPainter implements Runnable
{
    private static final Logger LOGGER = Logger.getLogger( DrawAreaPainter.class.getName() );
    private static long id = 0L;

    private final DrawArea drawArea;
    private final BlockingQueue<Dot> очередь;
    private final int fragmentSize;
    private final long fragmentTimeout;
    private final TimeUnit fragmentUnits;
    private volatile Thread thread;

    /**
     * @param drawArea графическая зона.
     * @param очередь  очередь отметок для прорисовки.
     */
    DrawAreaPainter( DrawArea drawArea, BlockingQueue<Dot> очередь )
    {
        this.drawArea = drawArea;
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
        thread = Thread.currentThread();
        try
        {
            thread.setName( getClass().getSimpleName() + id++ );
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
    
    void interrupt()
    {
        if( thread != null ) thread.interrupt();
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
            {
                Dot dot = блок[i];
                DrawAreaPainter.this.drawArea.mark( dot.v, dot.t, dot.color, dot.pattern );
            }
            return null;
        }
        
    }
    
}
