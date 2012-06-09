package com.varankin.brains.jfx;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.Мыслитель;
import com.varankin.brains.appl.УстановитьСостояние;
import com.varankin.util.Текст;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для остановки мыслительного процесса.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionStop extends Action
{
    private final ApplicationView.Context context;
    private final Действие<com.varankin.brains.Контекст> действие;

    ApplicationActionStop( ApplicationView.Context context )
    {
        super( context.jfx, Текст.ПАКЕТЫ.словарь( ApplicationActionStop.class, 
                context.jfx.контекст.специфика ) );
        this.context = context;
        this.действие = new УстановитьСостояние( Мыслитель.Процесс.Состояние.ОСТАНОВ );
        setEnabled( false );
    }

    @Override
    public void handle( ActionEvent event )
    {
        context.actions.getStart().setEnabled( true  );
        context.actions.getPause().setEnabled( false );
        context.actions.getStop() .setEnabled( false );
        
        new ApplicationActionWorker( действие, context.jfx ).execute();
    }

}
