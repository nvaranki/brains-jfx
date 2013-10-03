package com.varankin.brains.jfx;

import com.varankin.filter.Фильтр;
import java.util.*;
import javafx.beans.*;
import javafx.beans.Observable;
import javafx.collections.*;

/**
 *
 * @author Николай
 */
public final class ObservableListFilter<E> extends AbstractObservableListFilter<E>
{
    private final Фильтр<E> ФИЛЬТР;

    public ObservableListFilter( ObservableList<E> soutce, Фильтр<E> фильтр )
    {
        super( soutce );
        ФИЛЬТР = фильтр;
        soutce.addListener( new InvalidationListenerImpl() );
    }

    private class InvalidationListenerImpl implements InvalidationListener
    {
        @Override
        public void invalidated( Observable _ )
        {
            TARGET.retainAll( SOURCE ); // .retainAll(...) сигналит по необходимости :)
            Collection<E> tmp = new ArrayList<>( SOURCE );
            tmp.removeAll( TARGET );
            for( Iterator<E> it = tmp.iterator(); it.hasNext(); )
                if( !ФИЛЬТР.пропускает( it.next() ) )  
                    it.remove();
            if( !tmp.isEmpty() ) // .addAll(...) сигналит всегда :(
                TARGET.addAll( tmp );
        }
    }

}
