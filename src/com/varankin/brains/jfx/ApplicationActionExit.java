package com.varankin.brains.jfx;

import javafx.application.Platform;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для завершения приложения.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionExit extends AbstractContextJfxAction<ApplicationView.Context>
{

    ApplicationActionExit( ApplicationView.Context context )
    {
        super( context, context.jfx.словарь( ApplicationActionExit.class ) );
    }

    @Override
    public void handle( ActionEvent event )
    {
        //TODO try to stop appl.
        Platform.exit();
    }

}
