package com.varankin.brains.jfx.analyser;

import java.util.concurrent.BlockingQueue;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Процесс динамического рисования отметок в графической зоне. 
 * Отметки рисуются блоками, по мере поступления в очередь.
 * Прорисованные отметки сохраняются в буфере. При замене графической 
 * зоны отметки из буфера рисуются заново.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
class BufferedDrawAreaPainter extends DrawAreaPainter
{
    /** закольцованный буфер отметок */
    private final Dot[] буфер;
    /** указатель для сохранения очередной отметки */
    private int указатель;

    /**
     * @param drawArea графическая зона.
     * @param tc       функция X-координаты отметки от времени.
     * @param vc       функция Y-координаты отметки от значения.
     * @param color    цвет рисования шаблона.
     * @param pattern  шаблон для рисования как массив точек (x,y).
     * @param очередь  очередь отметок для прорисовки.
     * @param size     размер буфера.
     */
    BufferedDrawAreaPainter( DrawArea drawArea, TimeConvertor tc, ValueConvertor vc, 
            Color color, int[][] pattern, BlockingQueue<Dot> очередь, int size )
    {
        super( drawArea, tc, vc, color, pattern, очередь );
        буфер = new Dot[ Math.max( 1, size ) ];
        drawArea.imageProperty().addListener( new ChangeListener<Image>() 
        {
            @Override
            public void changed( ObservableValue<? extends Image> ov, Image oldImage, Image newImage )
            {
                for( Dot dot : буфер )
                    if( dot != null )
                        BufferedDrawAreaPainter.super.paint( dot );
            }
        } );
    }
    
    @Override
    protected final void paint( Dot dot )
    {
        буфер[ указатель++ ] = dot;
        указатель %= буфер.length;
        super.paint( dot );
    }
    
}
