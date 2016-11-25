package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.Транзакция;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
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
final class FxList<E extends FxАтрибутный<?>, X extends DbАтрибутный> extends AbstractList<E>
{
    private final DbАтрибутный owner;
    private final Collection<X> source;
    private final Function<E, X> extractant;
    private final Function<X, E> wrapper;
    private List<E> image;

    FxList( Collection<X> source, DbАтрибутный owner, Function<X, E> wrapper, Function<E, X> extractant )
    {
        this.source = source;
        this.owner = owner;
        this.extractant = extractant;
        this.wrapper = wrapper;
    }
    
    private List<E> loadFromSource()
    {
        try( final Транзакция т = owner.транзакция() )
        {
            return new LinkedList<>( source.stream().map( wrapper ).collect( Collectors.toList() ) );
        }
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private boolean removeFromSource( E e )
    {
        X элемент = extractant.apply( e );
        try( final Транзакция т = owner.транзакция() )
        {
            boolean removed = source.remove( элемент );
            т.завершить( removed );
            return removed;
        }
        catch( Exception ex )
        {
            throw new RuntimeException( ex );
        }
    }

    private boolean addToSource( E e )
    {
        X элемент = extractant.apply( e );
        try( final Транзакция т = owner.транзакция() )
        {
            boolean added = source.add( элемент );
            т.завершить( added );
            return added;
        }
        catch( Exception ex )
        {
            throw new RuntimeException( ex );
        }
    }
    
    @Override
    public E get( int index )
    {
        if( image == null ) image = loadFromSource();
        return image.get( index );
    }
    
    @Override
    public E remove( int index )
    {
        if( image == null ) image = loadFromSource();
        E removed = image.remove( index );
        if( removed != null )
            removeFromSource( removed );
        return removed;
    }
    
    @Override
    public void add( int index, E e )
    {
        if( image == null ) image = loadFromSource();
        if( addToSource( e ) )
            image.add( index, e );
    }

//    @Override
//    public Iterator<E> iterator()
//    {
//        if( image == null ) image = loadFromSource();
//        return new Iterator<E>()
//        {
//            final Iterator<E> it = image.iterator();
//            E last;
//
//            @Override
//            public boolean hasNext()
//            {
//                return it.hasNext();
//            }
//
//            @Override
//            public E next()
//            {
//                return last = it.next();
//            }
//
//            @Override
//            public void remove()
//            {
//                removeFromSource( last );
//                it.remove();
//            }
//        };
//    }
//
    @Override
    public int size()
    {
        if( image == null ) image = loadFromSource();
        return image.size();
//        try( final Транзакция т = owner.транзакция() )
//        {
//            return source.size();
//        }
//        catch( Exception ex )
//        {
//            throw new RuntimeException( ex );
//        }
    }

}
