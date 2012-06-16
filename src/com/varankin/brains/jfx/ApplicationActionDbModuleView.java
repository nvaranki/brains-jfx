package com.varankin.brains.jfx;

import com.varankin.util.Текст;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для удаления модуля из архива.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionDbModuleView extends Action
{
    private final ApplicationView.Context context;
    //private final Действие<Контекст> действие;

    ApplicationActionDbModuleView( ApplicationView.Context context )
    {
        super( context.jfx, Текст.ПАКЕТЫ.словарь( ApplicationActionDbModuleView.class, 
                context.jfx.контекст.специфика ) );
        //this.действие = new УдалитьАрхивныйМодуль();
        this.context = context;
    }

    @Override
    public void handle( ActionEvent _ )
    {
//        new ApplicationActionWorker( действие, context.jfx, 
//                this //TODO concept context.actions.getDbRemoveModule() 
//                ).execute();
    }

}
