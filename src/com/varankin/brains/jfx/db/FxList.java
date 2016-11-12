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
    private final Function<E, X> extractant;
    private final Function<X, E> wrapper;
    private List<E> image;

    FxList( Collection<X> source, Function<X, E> wrapper, Function<E, X> extractant )
    {
        this.source = source;
        this.extractant = extractant;
        this.wrapper = wrapper;
    }
    
    private void load()
    {
        image = new LinkedList<>( source.stream().map( wrapper ).collect( Collectors.toList() ) );
    }

    @Override
    public E get( int index )
    {
        if( image == null ) load();
        return image.get( index );
    }
    
    @Override
    public E remove( int index )
    {
        if( image == null ) load();
        E removed = image.remove( index );
        if( removed != null )
            source.remove( extractant.apply( removed ) );
        return removed;
    }
    
    @Override
    public void add( int index, E e )
    {
        if( image == null ) load();
        X ee = extractant.apply( e );
        if( source.add( ee ) ) 
            image.add( index, e );
    }

    @Override
    public Iterator<E> iterator()
    {
        if( image == null ) load();
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
        return source.size();
    }

}
