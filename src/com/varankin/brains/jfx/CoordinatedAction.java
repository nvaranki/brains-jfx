package com.varankin.brains.jfx;

import com.varankin.biz.action.СогласованноеДействие;
import com.varankin.util.Текст;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javafx.event.ActionEvent;

/**
 * Согласованное действие в среде JavaFX.
 *
 * @author &copy; 2013 Николай Варанкин
 */
class CoordinatedAction<КОНТЕКСТ> extends AbstractJfxAction
{
    protected final СогласованноеДействие<КОНТЕКСТ> действие;
    protected       КОНТЕКСТ контекст;
    protected final JavaFX jfx;

    CoordinatedAction( СогласованноеДействие<КОНТЕКСТ> действие, КОНТЕКСТ контекст, 
            JavaFX jfx, String префикс )
    {
        super( Текст.ПАКЕТЫ.словарь( CoordinatedAction.class.getPackage(), 
                префикс, jfx.контекст.специфика ) );
        this.jfx = jfx;
        this.действие = действие;
        this.контекст = контекст;
        действие.addPropertyChangeListener( new PropertyChangeListenerImpl() );
        disableProperty().setValue( !действие.isEnabled() );
    }

    @Override
    public void handle( ActionEvent _ )
    {
        new ApplicationActionWorker<>( действие, контекст ).execute( jfx );
    }
    
    private class PropertyChangeListenerImpl implements PropertyChangeListener
    {
        @Override
        public void propertyChange( PropertyChangeEvent event ) 
        {
            Object newValue = event.getNewValue();
            if( СогласованноеДействие.PROPERTY_ENABLED.equals( event.getPropertyName() ) 
                    && newValue != null )
            {
                boolean enabled = (Boolean)newValue;
                CoordinatedAction.this.disableProperty().setValue( !enabled ); //TODO thread!
            }
        }
    }

}
