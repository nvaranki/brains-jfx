package com.varankin.brains.jfx;

import com.varankin.util.Текст;

/**
 * Контейнер свойств визуально доступного действия, которое автоматически
 * загружает локализованные метки, акселераторы, иконки и т.п.
 *
 * @author &copy; 2013 Николай Варанкин
 */
public abstract class AbstractContextJfxAction<КОНТЕКСТ> 
        extends AbstractJfxAction
{
    protected final КОНТЕКСТ контекст;
    protected final Текст словарь;

    protected AbstractContextJfxAction( КОНТЕКСТ контекст, Текст словарь )
    {
        super( словарь );
        this.контекст = контекст;
        this.словарь = словарь;
    }
    
}
