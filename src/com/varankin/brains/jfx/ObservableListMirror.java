package com.varankin.brains.jfx;

import java.util.*;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.*;

/**
 * Реализация списка типа {@link ObservableList}, которая копирует исходный 
 * ObservableList. Копирование выполняется посредством назначаемого
 * {@link InvalidationListener}, который использует предоставленный агент 
 * копирования. Данный список является внешне неизменяемым объектом. 
 *
 * @author &copy; 2013 Николай Варанкин
 */
public final class ObservableListMirror<E> extends AbstractList<E> implements ObservableList<E>
{
    private final ObservableList<E> SOURCE;
    private final ObservableList<E> TARGET;
    private final InvalidationListener copier; // гарантирует сохранность WeakInvalidationListener

    public ObservableListMirror( ObservableList<E> source, Copier<E> агент )
    {
        SOURCE = source;
        TARGET = FXCollections.observableArrayList();
        copier = new InvalidationListenerImpl( агент );
        агент.copy( SOURCE, TARGET );
        source.addListener( new WeakInvalidationListener( copier ) );
    }
    
    //<editor-fold defaultstate="collapsed" desc="list control">
    
    @Override
    public boolean addAll( E... es )
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public E get( int index )
    {
        return TARGET.get( index );
    }
    
    @Override
    public void remove( int iS, int iE )
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAll( E... es )
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean retainAll( E... es )
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean setAll( E... es )
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean setAll( Collection<? extends E> clctn )
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int size()
    {
        return TARGET.size();
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="listener control">
    
    @Override
    public final void addListener( ListChangeListener<? super E> listener )
    {
        TARGET.addListener( listener );
    }
    
    @Override
    public final void addListener( InvalidationListener listener )
    {
        TARGET.addListener( listener );
    }
    
    @Override
    public final void removeListener( ListChangeListener<? super E> listener )
    {
        TARGET.removeListener( listener );
    }
    
    @Override
    public final void removeListener( InvalidationListener listener )
    {
        TARGET.removeListener( listener );
    }
    
    //</editor-fold>
   
    public interface Copier<E>
    {
        void copy( List<E> source, List<E> target );
    }
    
    private class InvalidationListenerImpl implements InvalidationListener
    {
        private final Copier<E> АГЕНТ;

        InvalidationListenerImpl( Copier<E> агент )
        {
            АГЕНТ = агент;
        }
        
        @Override
        public void invalidated( javafx.beans.Observable __ )
        {
            АГЕНТ.copy( SOURCE, TARGET );
        }

    }
    
}
