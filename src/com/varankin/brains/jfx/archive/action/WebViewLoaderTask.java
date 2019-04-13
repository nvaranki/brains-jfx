package com.varankin.brains.jfx.archive.action;

import com.varankin.brains.appl.Отображаемый;
import com.varankin.brains.db.*;
import com.varankin.brains.db.export.svg.SvgФабрика;
import com.varankin.brains.jfx.HtmlGenerator;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.filter.*;
import com.varankin.util.Текст;

import java.util.logging.*;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.web.WebEngine;

import static com.varankin.brains.appl.ЭкспортироватьSvg.*;
import static com.varankin.filter.И.и;
import static com.varankin.filter.НЕ.не;

/**
 * Загрузчик изображения элемента в формате SVG в Интернет навигатор.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
class WebViewLoaderTask extends Task<String>
{
    private static final Logger LOGGER = Logger.getLogger( WebViewLoaderTask.class.getName() );
    
    private final DbЭлемент элемент;
    private final StringProperty заголовок;
    private final WebEngine engine;
    
    private volatile String название;
    
    WebViewLoaderTask( DbЭлемент элемент, StringProperty заголовок, WebEngine engine )
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
            т.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, элемент.архив() );
            название = элемент.название();
            Фильтр<DbЭлемент> сборка = и( new Сборка( элемент ), не( БИБЛИОТЕКА ) );
            String code = SvgФабрика.getInstance().providerOf( элемент, сборка ).newInstance(); //TODO Отображаемый.MIME_SVG
            т.завершить( code != null );
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
    
}
