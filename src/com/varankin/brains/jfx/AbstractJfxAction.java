package com.varankin.brains.jfx;

import com.varankin.util.Текст;

/**
 * Контейнер свойств визуально доступного действия, которое автоматически
 * загружает локализованные метки, акселераторы, иконки и т.п.
 *
 * @author &copy; 2012 Николай Варанкин
 */
abstract class AbstractJfxAction<КОНТЕКСТ> extends com.varankin.util.jfx.AbstractJfxAction
{
    protected final КОНТЕКСТ контекст;
    protected final Текст словарь;

    AbstractJfxAction( КОНТЕКСТ контекст, Текст словарь )
    {
        super( словарь );
        this.контекст = контекст;
        this.словарь = словарь;
    }
    
}
