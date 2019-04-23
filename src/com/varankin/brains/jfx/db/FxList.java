package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.Коллекция;
import com.varankin.brains.db.Транзакция;
import com.varankin.characteristic.Наблюдатель;
import java.util.AbstractList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author &copy; 2019 Николай Варанкин
 */
final class FxList<X extends DbАтрибутный, E extends FxАтрибутный<X>> extends AbstractList<E>
{
    private final DbАтрибутный owner;
    private final Коллекция<X> source;
    private final Function<E, X> extractant;
    private final Function<X, E> wrapper;
    private List<E> image;

    FxList( Коллекция<X> source, DbАтрибутный owner, Function<X, E> wrapper, Function<E, X> extractant )
    {
        this.source = source;
        this.owner = owner;
        this.extractant = extractant;
        this.wrapper = wrapper;
    }
    
    Collection<Наблюдатель<X>> наблюдатели()
    {
        return source.наблюдатели();
    }
    
    private List<E> loadFromSource()
    {
        try( final Транзакция т = owner.архив().транзакция() )
        {
            т.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, owner.архив() );
            List<E> list = new LinkedList<>( source.stream().map( wrapper ).collect( Collectors.toList() ) );
            т.завершить( true );
            return list;
        }
        catch( Exception e )
        {
            throw new RuntimeException( "FxList.loadFromSource(): " + owner, e );
        }
    }

    private boolean removeFromSource( E e )
    {
        X элемент = extractant.apply( e );
        try( final Транзакция т = owner.транзакция() )
        {
            т.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, owner.архив() );
            boolean removed = source.remove( элемент );
            т.завершить( removed );
            return removed;
        }
        catch( Exception ex )
        {
            throw new RuntimeException( "FxList.removeFromSource(): " + owner, ex );
        }
    }

    private boolean addToSource( E e )
    {
        X элемент = extractant.apply( e );
        try( final Транзакция т = owner.транзакция() )
        {
            т.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, owner.архив() );
            boolean added = source.add( элемент );
            т.завершить( added );
            return added;
        }
        catch( Exception ex )
        {
            throw new RuntimeException( "FxList.addToSource(): " + owner, ex );
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
        // некоторые операции могут обновлять непосредственно 
        // Коллекции, поэтому в процессе обработки их событий
        // к добавлению в этот список предъявляется объекты 
        // FxАтрибутный, для которых вложенный объект DbАтрибутный
        // уже представлен в Коллекции; дополнительная проверка 
        // выяснит необходимость включения объекта FxАтрибутный 
        // в данный список
        if( addToSource( e ) || image.stream()
                .map( FxАтрибутный::getSource )
                .noneMatch( a -> Objects.equals( a, e.getSource() ) ) )
            image.add( index, e );
    }

    @Override
    public int size()
    {
        if( image == null ) image = loadFromSource();
        return image.size();
    }

}
