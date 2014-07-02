package com.varankin.brains.jfx.archive;

import com.varankin.brains.appl.Отображаемый;
import com.varankin.brains.artificial.io.svg.*;
import com.varankin.brains.db.*;
import com.varankin.brains.jfx.HtmlGenerator;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.io.container.Provider;
import com.varankin.util.Текст;

import java.util.logging.*;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.web.WebEngine;

/**
 * Загрузчик изображения элемента в формате SVG в Интернет навигатор.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
class WebViewLoaderTask extends Task<String>
{
    private static final Logger LOGGER = Logger.getLogger( WebViewLoaderTask.class.getName() );
    
    private final Элемент элемент;
    private final StringProperty заголовок;
    private final WebEngine engine;
    
    private volatile String название;
    
    WebViewLoaderTask( Элемент элемент, StringProperty заголовок, WebEngine engine )
    {
        this.элемент = элемент;
        this.заголовок = заголовок;
        this.engine = engine;
    }

    @Override
    protected String call() throws Exception
    {
        try( Транзакция т = элемент.транзакция() )
        {
            название = элемент.название();
            Сборка сборка = new Сборка( элемент );
            SvgService<Элемент> service = ( Элемент э ) -> генератор( э, сборка );
            String code = service.генератор( элемент ).newInstance(); //TODO Отображаемый.MIME_SVG
            т.завершить( true );
            return code;
        }
    }

    @Override
    protected void succeeded()
    {
        заголовок.setValue( название );
        engine.loadContent( getValue(), Отображаемый.MIME_SVG );
    }

    @Override
    protected void failed()
    {
        Текст словарь = JavaFX.getInstance().словарь( WebViewLoaderTask.class );
        String msg = словарь.текст( "failure", название );
        Throwable exception = this.getException();
        engine.loadContent( HtmlGenerator.toHtml( msg, exception ), Отображаемый.MIME_TEXT );
        LOGGER.log( Level.SEVERE, msg, exception );
    }

    @SuppressWarnings("Confusing indentation")
    private static Provider<String> генератор( Атрибутный элемент, Сборка сборка )
    {
        Provider<String> p;
        if( элемент instanceof Библиотека ) p = new SvgБиблиотека( (Библиотека)элемент, сборка ); else
        if( элемент instanceof Заметка    ) p = new SvgЗаметка( (Заметка)элемент ); else
        if( элемент instanceof Контакт    ) p = new SvgКонтакт( (Контакт)элемент, сборка ); else
        if( элемент instanceof Модуль     ) p = new SvgМодуль( (Модуль)элемент, сборка ); else
        if( элемент instanceof Поле       ) p = new SvgПоле( (Поле)элемент, сборка ); else
        if( элемент instanceof Проект     ) p = new SvgПроект( (Проект)элемент, сборка ); else
        if( элемент instanceof Процессор  ) p = new SvgПроцессор( (Процессор)элемент, сборка ); else
        if( элемент instanceof Расчет     ) p = new SvgРасчет( (Расчет)элемент, сборка ); else
        if( элемент instanceof Сигнал     ) p = new SvgСигнал( (Сигнал)элемент, сборка ); else
        if( элемент instanceof Соединение ) p = new SvgСоединение( (Соединение)элемент, сборка ); else
        if( элемент instanceof Точка      ) p = new SvgТочка( (Точка)элемент, сборка ); else
        if( элемент instanceof Фрагмент   ) p = new SvgФрагмент( (Фрагмент)элемент, сборка ); else
        if( элемент instanceof Библиотека ) p = new SvgБиблиотека( (Библиотека)элемент, сборка ); else
        p = new SvgНеизвестный( элемент );
        return p;
    }
    
}
