package com.varankin.brains.jfx;

import com.varankin.biz.action.СогласованноеДействие;
import com.varankin.util.HistoryList;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для повтора.
 * При успешном выполнении действия устанавливает или продвигает элемент
 * истории на приоритетную позицию.
 * 
 * @see com.varankin.brains.appl.HistoricProvider
 *
 * @author &copy; 2013 Николай Варанкин
 * 
 * @param <КОНТЕКСТ> класс контекста действия.
 * @param <ЭЛЕМЕНТ>  класс элемента истории.
 */
class ApplicationActionHistory<КОНТЕКСТ,ЭЛЕМЕНТ> 
    extends CoordinatedAction<КОНТЕКСТ>
    implements InvalidationListener
{
    private final HistoryList<ЭЛЕМЕНТ> история;
    private final int позиция;

    /**
     * @param действие согласованное действие для выполнения.
     * @param контекст контекст действия, должен постоянно соответствовать элементу истории.
     * @param jfx      контекст JavaFX.
     * @param история  менеджер истории.
     * @param позиция  индекс назначенного элемента истории.
     */
    ApplicationActionHistory( 
            СогласованноеДействие<КОНТЕКСТ> действие, КОНТЕКСТ контекст, 
            JavaFX jfx, HistoryList<ЭЛЕМЕНТ> история, int позиция )
    {
        super( действие, контекст, jfx, ApplicationActionHistory.class.getSimpleName() );
        this.история = история;
        this.позиция = позиция;
        refreshProperties();
    }

    @Override
    public void handle( ActionEvent _ )
    {
        final ЭЛЕМЕНТ элемент = история.get( позиция );
        new ApplicationActionWorker<КОНТЕКСТ>( действие, контекст ) // новый, т.к. одноразовый
        {
            @Override
            protected void succeeded()
            {
                super.succeeded();
                история.advance( элемент );
            }            
        }.execute( jfx );
    }

    private void refreshProperties() 
    {
        ЭЛЕМЕНТ элемент = история.get( позиция );
        disableProperty().setValue( элемент == null || !действие.isEnabled() );
        String название = элемент != null ? элемент.toString() : "";
        textProperty().setValue( Integer.toString( позиция ) + ' ' + название );
    }
    
    @Override
    public void invalidated( Observable _ ) 
    {
        refreshProperties();
    }
    
}
