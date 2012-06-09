package com.varankin.brains.jfx;

import com.varankin.util.Текст;
import javafx.application.Platform;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для завершения приложения.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionExit extends Action
{

    ApplicationActionExit( ApplicationView.Context context )
    {
        super( context.jfx, Текст.ПАКЕТЫ.словарь( ApplicationActionExit.class, 
                context.jfx.контекст.специфика ) );
    }

    @Override
    public void handle( ActionEvent event )
    {
        //TODO try to stop appl.
        Platform.exit();
    }

}
