package com.varankin.brains.jfx;

import com.varankin.biz.action.*;
import com.varankin.brains.appl.ResultLogger;
import javafx.concurrent.Task;

/**
 * Действие для выполнения в среде JavaFX.
 *
 * @author &copy; 2012 Николай Варанкин
 */
public class ApplicationActionWorker<КОНТЕКСТ> extends Task<Результат>
{
    private final Действие<КОНТЕКСТ> действие;
    private final КОНТЕКСТ контекст;
    private volatile ResultLogger репортер;

    public ApplicationActionWorker( Действие<КОНТЕКСТ> действие, КОНТЕКСТ контекст )
    {
        this.действие = действие;
        this.контекст = контекст;
    }

    protected final КОНТЕКСТ контекст()
    {
        return контекст;
    }
    
    @Override
    protected Результат call() throws Exception
    {
        return действие.выполнить( контекст );
    }
    
    @Override
    protected void succeeded()
    {
        super.succeeded();
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
     * 
     * @param координатор сервис блокировки действий на время выполнения.
     */
    public void execute( JavaFX jfx ) 
    {
        репортер = jfx.контекст.репортер;
        jfx.getExecutorService().execute( this );
    }

}
