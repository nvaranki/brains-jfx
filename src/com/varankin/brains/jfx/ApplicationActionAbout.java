package com.varankin.brains.jfx;

import javafx.event.ActionEvent;

/**
 * Действие JavaFX для показа диалога с визиткой программы.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionAbout extends AbstractJfxAction<JavaFX>
{
    private Открытка открытка;

    ApplicationActionAbout( ApplicationView.Context context )
    {
        super( context.jfx, context.jfx.словарь( ApplicationActionAbout.class ) );
    }

    @Override
    public void handle( ActionEvent event )
    {
        if( открытка == null )
            открытка = new Открытка( контекст, false );
        открытка.show();
    }

}
