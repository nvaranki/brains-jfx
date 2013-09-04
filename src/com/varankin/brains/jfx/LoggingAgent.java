package com.varankin.brains.jfx;

import com.varankin.util.LoggingHandler;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.control.TextInputControl;

/**
 *
 *
 * @author &copy; 2011 Николай
 */
class LoggingAgent implements LoggingHandler.Appendable
{
    final TextInputControl табло;

    LoggingAgent( TextInputControl табло )
    {
        this.табло = табло;
    }

    @Override
    public void append( String текст, Level level )
    {
        Platform.runLater( new Appender( текст, level, табло ) );
    }
    
    static private class Appender implements Runnable
    {
        final TextInputControl табло;
        final String текст;
        final Level level;

        Appender( String текст, Level level, TextInputControl табло )
        {
            this.текст = текст;
            this.level = level;
            this.табло = табло;
        }

        @Override
        public void run()
        {
            табло.appendText( текст );
        }
    }

}
