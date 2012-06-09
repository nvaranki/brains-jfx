package com.varankin.brains.jfx;

import com.varankin.biz.action.*;
import com.varankin.brains.appl.ResultLogger;
import com.varankin.brains.Контекст;
import javafx.concurrent.Task;

/**
 * Действие для выполнения в среде JavaFX.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionWorker extends Task<Результат>
{
    private final Действие<Контекст> действие;
    private final JavaFX jfx;

    ApplicationActionWorker( Действие<Контекст> действие, JavaFX jfx )
    {
        this.действие = действие;
        this.jfx = jfx;
    }
    
    @Override
    protected Результат call() throws Exception
    {
        return действие.выполнить( jfx.контекст );
    }
    
    @Override
    protected void succeeded()
    {
        super.succeeded();
        ResultLogger репортер = jfx.контекст.репортер;
        Результат результат = getValue();
        if( результат != null ) 
            репортер.log( результат );
        else
            репортер.log( действие, null );
        finished();
    }

    @Override
    protected void cancelled()
    {
        super.succeeded();
        finished();
    }

    @Override
    protected void failed()
    {
        super.failed();
        ResultLogger репортер = jfx.контекст.репортер;
        репортер.log( действие, getException() );
        Результат результат = getValue();
        if( результат != null ) ;//TODO Platform.runLater( new ... ); display form with error details
        finished();
    }
    
    protected void finished()
    {
        // "abstract" method
    }

    /**
     * Convenience method simulating SwingWorker.execute().
     */
    void execute()
    {
        jfx.getExecutorService().execute( this );
    }

}
