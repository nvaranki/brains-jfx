package com.varankin.brains.jfx.db;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Varankine
 */
final class FxSet<E, X> extends AbstractSet<E>
{
    
    private final Collection<X> source;
    private final Set<E> image;
    private final Function<E, X> extractant;

    FxSet( Collection<X> source, Function<X, E> wrapper, Function<E, X> extractant )
    {
        this.source = source;
        this.image = new HashSet<>( source.stream().map( wrapper ).collect( Collectors.toSet() ) );
        this.extractant = extractant;
    }

    @Override
    public boolean add( E e )
    {
        return source.add( extractant.apply( e ) ) && image.add( e );
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
