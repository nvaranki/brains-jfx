package com.varankin.brains.jfx;

import com.varankin.brains.appl.Очистить;
import com.varankin.util.Текст;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для очистки мыслительной структуры объектов.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionClean extends Action
{
    private final ActionFactory actions;

    ApplicationActionClean( ApplicationView.Context context )
    {
        super( context.jfx, Текст.ПАКЕТЫ.словарь( ApplicationActionClean.class, 
                context.jfx.контекст.специфика ) );
        actions = context.actions;
    }

    @Override
    public void handle( ActionEvent _ )
    {
        actions.getLoad() .setEnabled( false );
        actions.getClean().setEnabled( false );
        
        new ApplicationActionWorker( new Очистить(), jfx )
        {
            @Override
            protected void finished()
            {
                actions.getLoad() .setEnabled( true );
                actions.getClean().setEnabled( true );
            }
        }.execute();
    }

}
