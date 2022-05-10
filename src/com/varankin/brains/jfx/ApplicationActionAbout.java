package com.varankin.brains.jfx;

import javafx.event.ActionEvent;

/**
 * Действие JavaFX для показа диалога с визиткой программы.
 *
 * @author &copy; 2013 Николай Варанкин
 */
class ApplicationActionAbout extends AbstractContextJfxAction<JavaFX>
{
    private Открытка открытка;

    ApplicationActionAbout( JavaFX jfx )
    {
        super( jfx, jfx.словарь( ApplicationActionAbout.class ) );
    }

    @Override
    public void handle( ActionEvent event )
    {
        if( открытка == null )
            открытка = new Открытка( контекст, false );
        открытка.show();
    }

}
