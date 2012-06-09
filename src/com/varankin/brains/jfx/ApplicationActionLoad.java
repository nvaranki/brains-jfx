package com.varankin.brains.jfx;

import com.varankin.brains.appl.Загрузить;
import com.varankin.brains.artificial.io.*;
import com.varankin.brains.artificial.io.marked.*;
import com.varankin.brains.artificial.io.pure.*;
import com.varankin.brains.Контекст;
import com.varankin.util.Текст;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для загрузки мыслительной структуры объектов.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionLoad extends Action
{
    private final ApplicationView.Context context;
    private final Загрузить действие;

    ApplicationActionLoad( ApplicationView.Context context )
    {
        super( context.jfx, Текст.ПАКЕТЫ.словарь( ApplicationActionLoad.class, 
                context.jfx.контекст.специфика ) );
        this.context = context;
        setEnabled( false );
        Контекст контекст = context.jfx.контекст;
        НаборФабрик набор = new НаборМаркированныхФабрик( 
                new НаборБазовыхФабрик( контекст ), контекст.мыслитель.сервис() );
        действие = new Загрузить(  
                набор.фабрикаПоля(), набор.фабрикаСенсора(), 
                набор.фабрикаПроцессора(), набор.фабрикаФункции(), 
                набор.фабрикаЗначимого() );
    }

    @Override
    public void handle( ActionEvent event )
    {
        context.actions.getLoad() .setEnabled( false );
        context.actions.getClean().setEnabled( false );
        
        new ApplicationActionWorker( действие, context.jfx )
        {
            @Override
            protected void finished()
            {
                context.actions.getLoad() .setEnabled( true );
                context.actions.getClean().setEnabled( true );
            }
        }.execute();
    }
    
}
