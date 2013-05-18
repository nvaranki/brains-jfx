package com.varankin.brains.jfx;

import javafx.application.Platform;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для завершения приложения.
 *
 * @author &copy; 2013 Николай Варанкин
 */
class ApplicationActionExit extends AbstractContextJfxAction<JavaFX>
{

    ApplicationActionExit( JavaFX jfx )
    {
        super( jfx, jfx.словарь( ApplicationActionExit.class ) );
    }

    @Override
    public void handle( ActionEvent event )
    {
        //TODO try to stop appl.
        Platform.exit();
    }

}
