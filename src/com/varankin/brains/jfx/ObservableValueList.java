package com.varankin.brains.jfx;

import com.varankin.brains.db.Коллекция;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import javafx.application.Platform;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Николай
 * 
 * @param <T> 
 */
class ObservableValueList<T> extends ObservableValueBase<ObservableList<T>> 
{
    private final ObservableList<T> LIST;

    ObservableValueList( Коллекция<T> элементы ) 
    {
        LIST = FXCollections.<T>observableArrayList();
        элементы.listeners().add( new PropertyChangeListenerImpl( элементы ) ); //TODO .removeProperty...()
    }

    @Override
    public final ObservableList<T> getValue() 
    {
        return LIST;
    }
    
    //<editor-fold defaultstate="collapsed" desc="классы">
    
    private class PropertyChangeAction implements Runnable
    {
        final List<T> update;
        final Object action;
        
        PropertyChangeAction( Collection<? extends T> update, Object action ) 
        {
            this.update = new ArrayList<>( update ); // snapshot
            this.action = action;
        }
        
        @Override
        public void run() 
        {
            //TODO sort?!
            if( Коллекция.PROPERTY_ADDED.equals( action ) )
                LIST.addAll( update );
            else if( Коллекция.PROPERTY_REMOVED.equals( action ) )
                LIST.removeAll( update );
            else if( Коллекция.PROPERTY_UPDATED.equals( action ) ) 
            {
                LIST.retainAll( update );
                for( T t : update )
                    if( !LIST.contains( t ) )
                        LIST.add( t );
            }
            ObservableValueList.this.fireValueChangedEvent();
        }
    }
    
    private class PropertyChangeListenerImpl implements PropertyChangeListener 
    {
        final Collection<T> ЭЛЕМЕНТЫ;
        
        public PropertyChangeListenerImpl( Collection<T> элементы ) 
        {
            ЭЛЕМЕНТЫ = элементы;
        }
        
        @Override
        public void propertyChange(PropertyChangeEvent evt) 
        {
            // database part got changed
            switch (evt.getPropertyName()) 
            {
                case Коллекция.PROPERTY_ADDED:
                    Platform.runLater( new PropertyChangeAction( 
                            Arrays.<T>asList( (T)evt.getNewValue() ), Коллекция.PROPERTY_ADDED ) );
                    break;
                    
                case Коллекция.PROPERTY_REMOVED:
                    Platform.runLater( new PropertyChangeAction(
                            Arrays.<T>asList( (T)evt.getOldValue() ), Коллекция.PROPERTY_REMOVED ) );
                    break;
                    
                case Коллекция.PROPERTY_UPDATED:
                    Platform.runLater( new PropertyChangeAction( ЭЛЕМЕНТЫ, Коллекция.PROPERTY_UPDATED ) );
                    break;
            }
        }
    }
    
    //</editor-fold>

}

