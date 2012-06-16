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
    private Очистить действие;

    ApplicationActionClean( ApplicationView.Context context )
    {
        super( context.jfx, Текст.ПАКЕТЫ.словарь( ApplicationActionClean.class, 
                context.jfx.контекст.специфика ) );
        actions = context.actions;
        действие = new Очистить();
    }

    @Override
    public void handle( ActionEvent _ )
    {
        new ApplicationActionWorker( действие, jfx.контекст, jfx, 
                actions.getLoad(), actions.getClean() ).execute();
    }

}
