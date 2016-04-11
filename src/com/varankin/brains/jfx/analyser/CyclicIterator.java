package com.varankin.brains.jfx.analyser;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Varankine
 */
class CyclicIterator<E> implements Iterator<E>
{
    
    private final Iterable<E> source;
    private Iterator<E> it;

    CyclicIterator( Iterable<E> source )
    {
        this.source = source != null ? source : Collections.emptyList();
        this.it = this.source.iterator();
    }

    @Override
    public boolean hasNext()
    {
        if( !it.hasNext() )
        {
            it = source.iterator();
        }
        return it.hasNext();
    }

    @Override
    public E next()
    {
        if( hasNext() )
        {
            return it.next();
        }
        else
        {
            throw new NoSuchElementException();
        }
    }
    
}
