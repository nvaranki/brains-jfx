package com.varankin.brains.jfx;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.Мыслитель;
import com.varankin.brains.appl.УстановитьСостояние;
import com.varankin.util.Текст;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для приостановки мыслительного процесса.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionPause extends Action
{
    private final ApplicationView.Context context;
    private final Действие<com.varankin.brains.Контекст> действие;


    ApplicationActionPause( ApplicationView.Context context )
    {
        super( context.jfx, Текст.ПАКЕТЫ.словарь( ApplicationActionPause.class, 
                context.jfx.контекст.специфика ) );
        this.context = context;
        this.действие = new УстановитьСостояние( Мыслитель.Процесс.Состояние.ПАУЗА );
        setEnabled( false );
    }

    @Override
    public void handle( ActionEvent event )
    {
        context.actions.getStart().setEnabled( true  );
        context.actions.getPause().setEnabled( false );
        context.actions.getStop() .setEnabled( true  );
        
        new ApplicationActionWorker( действие, context.jfx ).execute();
    }

}
