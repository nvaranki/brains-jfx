package com.varankin.brains.jfx.analyser;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;

/**
 * Процесс динамического рисования отметок в графической зоне. 
 * Отметки рисуются блоками, по мере поступления в очередь.
 * Прорисованные отметки сохраняются в буфере. При замене графической 
 * зоны отметки из буфера рисуются заново.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
class BufferedDotPainter extends DotPainter
{
    /** закольцованный буфер отметок */
    private final Dot[] буфер;
    /** указатель для сохранения очередной отметки */
    private int указатель;

    /**
     * @param tc       функция X-координаты отметки от времени.
     * @param vc       функция Y-координаты отметки от значения.
     * @param очередь  очередь отметок для прорисовки.
     * @param size     размер буфера.
     */
    BufferedDotPainter( BlockingQueue<Dot> очередь, int size )
    {
        super( очередь );
        буфер = new Dot[ Math.max( 1, size ) ];
        writableImageProperty().addListener( new ChangeListener<Image>() 
        {
            @Override
            public void changed( ObservableValue<? extends Image> ov, Image oldImage, Image newImage )
            {
                List<Dot> dots = new LinkedList<>();
                synchronized( буфер )
                {
                    for( Dot dot : буфер )
                        if( dot != null )
                            dots.add( dot );
                }
                if( !dots.isEmpty() )
                    Platform.runLater( new MultiDotTask( dots.toArray( new Dot[dots.size()] ), dots.size() ) );
            }
        } );
    }
    
    @Override
    protected final void paint( Dot dot )
    {
        synchronized( буфер )
        {
            буфер[ указатель++ ] = dot;
            указатель %= буфер.length;
        }
        super.paint( dot );
    }
    
}
