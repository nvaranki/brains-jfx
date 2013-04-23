package com.varankin.brains.jfx;

import com.varankin.biz.action.Действие;
import com.varankin.util.Текст;
import javafx.event.ActionEvent;

/**
 * Простое действие в среде JavaFX.
 *
 * @author &copy; 2013 Николай Варанкин
 */
class SimpleAction<КОНТЕКСТ> extends com.varankin.brains.jfx.AbstractJfxAction
{
    private final Действие<КОНТЕКСТ> действие;
    private final КОНТЕКСТ контекст;
    private final JavaFX jfx;

    SimpleAction( Действие<КОНТЕКСТ> действие, КОНТЕКСТ контекст, 
            JavaFX jfx, String префикс )
    {
        super( Текст.ПАКЕТЫ.словарь( SimpleAction.class.getPackage(), 
                префикс, jfx.контекст.специфика ) );
        this.jfx = jfx;
        this.действие = действие;
        this.контекст = контекст;
    }
    
    @Override
    public void handle( ActionEvent _ )
    {
        new ApplicationActionWorker<>( действие, контекст ).execute( jfx );
    }
    
}
