/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.varankin.brains.jfx;

import com.varankin.brains.appl.Отображаемый;
import com.varankin.io.container.Provider;
import com.varankin.util.Текст;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.scene.web.WebEngine;

/**
 *
 * @author Николай
 */
public class WebViewLoaderTask extends Task<String>
{
    private static final Logger LOGGER = Logger.getLogger( WebViewLoaderTask.class.getName() );
    
    private final Provider<String> svg;
    private final WebEngine engine;
    private final String название;
    private final Текст словарь;

    public WebViewLoaderTask( Provider<String> svg, WebEngine engine, String название, Текст словарь )
    {
        this.svg = svg;
        this.engine = engine;
        this.название = название;
        this.словарь = словарь;
    }

    @Override
    protected String call() throws Exception
    {
        return svg.newInstance(); //TODO Отображаемый.MIME_SVG
    }

    @Override
    protected void succeeded()
    {
        engine.loadContent( getValue(), Отображаемый.MIME_SVG );
    }

    @Override
    protected void failed()
    {
        String msg = словарь.текст( "failure", название );
        Throwable exception = this.getException();
        engine.loadContent( HtmlGenerator.toHtml( msg, exception ), Отображаемый.MIME_TEXT );
        LOGGER.log( Level.SEVERE, msg, exception );
    }
}
