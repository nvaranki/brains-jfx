package com.varankin.brains.jfx;

import java.util.*;
import javafx.beans.InvalidationListener;
import javafx.collections.*;

/**
 *
 * @author Николай
 */
public abstract class AbstractObservableListFilter<E> extends AbstractList<E> implements ObservableList<E>
{
    protected final ObservableList<E> SOURCE;
    protected final ObservableList<E> TARGET;

    protected AbstractObservableListFilter( ObservableList<E> soutce )
    {
        SOURCE = soutce;
        TARGET = FXCollections.observableArrayList( soutce );
    }

    @Override
    public boolean addAll( E... es )
    {
        return TARGET.addAll( es );
    }

    @Override
    public void addListener( ListChangeListener<? super E> listener )
    {
        TARGET.addListener( listener );
    }

    @Override
    public void addListener( InvalidationListener listener )
    {
        TARGET.addListener( listener );
    }

    @Override
    public E get( int index )
    {
        return TARGET.get( index );
    }

    @Override
    public void remove( int iS, int iE )
    {
        TARGET.remove( iS, iE );
    }

    @Override
    public boolean removeAll( E... es )
    {
        return TARGET.removeAll( es );
    }

    @Override
    public void removeListener( ListChangeListener<? super E> listener )
    {
        TARGET.removeListener( listener );
    }

    @Override
    public void removeListener( InvalidationListener listener )
    {
        TARGET.removeListener( listener );
    }

    @Override
    public boolean retainAll( E... es )
    {
        return TARGET.retainAll( es );
    }

    @Override
    public boolean setAll( E... es )
    {
        return TARGET.setAll( es );
    }

    @Override
    public boolean setAll( Collection<? extends E> clctn )
    {
        return TARGET.setAll( clctn );
    }

    @Override
    public int size()
    {
        return TARGET.size();
    }
    
}
