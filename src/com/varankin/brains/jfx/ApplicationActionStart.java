package com.varankin.brains.jfx;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.Мыслитель;
import com.varankin.brains.appl.УстановитьСостояние;
import com.varankin.util.Текст;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для запуска мыслительного процесса.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionStart extends Action
{
    private final ApplicationView.Context context;
    private final Действие<com.varankin.brains.Контекст> действие;

    ApplicationActionStart( ApplicationView.Context context )
    {
        super( context.jfx, Текст.ПАКЕТЫ.словарь( ApplicationActionStart.class, 
                context.jfx.контекст.специфика ) );
        this.context = context;
        this.действие = new УстановитьСостояние( Мыслитель.Процесс.Состояние.РАБОТА );
        setEnabled( true );
    }

    @Override
    public void handle( ActionEvent event )
    {
        context.actions.getStart().setEnabled( false );
        context.actions.getPause().setEnabled( true  );
        context.actions.getStop() .setEnabled( true  );
        
        new ApplicationActionWorker( действие, context.jfx ).execute();
    }

}
