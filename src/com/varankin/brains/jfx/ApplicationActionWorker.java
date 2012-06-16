package com.varankin.brains.jfx;

import com.varankin.biz.action.*;
import com.varankin.brains.appl.ResultLogger;

import javafx.concurrent.Task;

/**
 * Действие для выполнения в среде JavaFX.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionWorker<КОНТЕКСТ> extends Task<Результат>
{
    private final Действие<КОНТЕКСТ> действие;
    private final КОНТЕКСТ контекст;
    private final JavaFX jfx;
    private Action[] участники;

    ApplicationActionWorker( Действие<КОНТЕКСТ> действие, КОНТЕКСТ контекст, JavaFX jfx, Action... участники )
    {
        this.действие = действие;
        this.контекст = контекст;
        this.jfx = jfx;
        this.участники = участники;
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
        setDependentActionsEnabled( true );
    }

    /**
     * Convenience method simulating SwingWorker.execute().
     * 
     * @param участники действия, которые следует заблокировать на время выполнения.
     */
    void execute( Action... участники )
    {
        if( участники.length > 0 ) this.участники = участники;
        setDependentActionsEnabled( false );
        jfx.getExecutorService().execute( this );
    }

    private void setDependentActionsEnabled( boolean статус )
    {
        for( Action участник : участники )
            участник.setEnabled( статус );
    }

}
