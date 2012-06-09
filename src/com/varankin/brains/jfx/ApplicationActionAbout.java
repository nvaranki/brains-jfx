package com.varankin.brains.jfx;

import com.varankin.util.Текст;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для показа диалога с визиткой программы.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionAbout extends Action
{
    private Открытка открытка;

    ApplicationActionAbout( ApplicationView.Context context )
    {
        super( context.jfx, Текст.ПАКЕТЫ.словарь( ApplicationActionAbout.class, 
                context.jfx.контекст.специфика ) );
    }

    @Override
    public void handle( ActionEvent event )
    {
        if( открытка == null )
            открытка = new Открытка( jfx, false );
        открытка.show();
    }

}
