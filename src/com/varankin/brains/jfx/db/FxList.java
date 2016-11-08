package com.varankin.brains.jfx.db;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Varankine
 */
final class FxList<E, X> extends AbstractList<E>
{
    private final Collection<X> source;
    private final List<E> image;
    private final Function<E, X> extractant;

    FxList( Collection<X> source, Function<X, E> wrapper, Function<E, X> extractant )
    {
        this.source = source;
        this.image = new LinkedList<>( source.stream().map( wrapper ).collect( Collectors.toList() ) );
        this.extractant = extractant;
    }

    @Override
    public E get( int index )
    {
        return image.get( index );
    }
    
    @Override
    public E remove( int index )
    {
        E removed = image.remove( index );
        if( removed != null )
            source.remove( extractant.apply( removed ) );
        return removed;
    }
    
    @Override
    public void add( int index, E e )
    {
        X ee = extractant.apply( e );
        if( source.add( ee ) ) 
            image.add( index, e );
        else
            source.remove( ee );
    }

    @Override
    public Iterator<E> iterator()
    {
        return new Iterator<E>()
        {
            final Iterator<E> it = image.iterator();
            E last;

            @Override
            public boolean hasNext()
            {
                return it.hasNext();
            }

            @Override
            public E next()
            {
                return last = it.next();
            }

            @Override
            public void remove()
            {
                source.remove( extractant.apply( last ) );
                it.remove();
            }
        };
    }

    @Override
    public int size()
    {
        return image.size();
    }

}
