package com.varankin.brains.jfx.browser;

import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.artificial.async.Процесс.Состояние;
import com.varankin.brains.factory.Составной;
import com.varankin.characteristic.*;

import java.util.*;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;

/**
 * Узел дерева для произвольного элемента.
 *
 * @author &copy; 2015 Николай Варанкин
 */
class BrowserNode<T> extends TreeItem<T>
{
    private static final Collection НН = new NullCollection<>();
    
    private final String МЕТКА;
    private final Составитель СОСТАВИТЕЛЬ;
    private final Маляр МАЛЯР;

    /**
     * @param элемент   объект узла.
     * @param строитель построитель узлов.
     */
    BrowserNode( T элемент, BrowserNodeBuilder<T> строитель )
    {
        super( элемент, строитель.марка( элемент ) );
        МЕТКА = строитель.метка( элемент );
        Collection<Наблюдатель<T>> нс = наблюдателиСостава( элемент );
        СОСТАВИТЕЛЬ = нс == НН ? null : new Составитель( ( T t ) ->
        {
            BrowserNode<T> вставка = строитель.узел( t );
            List<TreeItem<T>> children = getChildren();
            children.add( строитель.позиция( вставка, children ), вставка );
        } );
        нс.add( СОСТАВИТЕЛЬ );
        Collection<Наблюдатель<Состояние>> нп = наблюдателиПроцесса( элемент );
        МАЛЯР = нп == НН ? null : new Маляр( ( Процесс.Состояние с ) ->
            строитель.фабрикаКартинок().setBgColor( getGraphic(), с ) );
        нп.add( МАЛЯР );
    }
    
    /**
     * Строит поддерево от данного узла.
     */
    void раскрыть()
    {
        T value = getValue();
        if( СОСТАВИТЕЛЬ != null && value instanceof Составной )
            ((Составной)value).состав().stream().forEach( СОСТАВИТЕЛЬ.РАСКРЫВАТЕЛЬ );
    }
    
    @Override
    public boolean equals( Object o )
    {
        return o instanceof BrowserNode && getValue().equals( ( (BrowserNode)o ).getValue() );
    }

    @Override
    public int hashCode()
    {
        int hash = 7 ^ getValue().hashCode();
        return hash;
    }

    @Override
    public String toString()
    {
        return МЕТКА;
    }
    
    private static <T> void removeTreeItemChildren( TreeItem<T> узел )
    {
        if( !узел.isLeaf() )
        {
            for( TreeItem<T> c : узел.getChildren() )
                removeTreeItemChildren( c );
            узел.getChildren().clear();
        }
        if( узел instanceof BrowserNode )
        {
            BrowserNode bn = (BrowserNode)узел;
            наблюдателиСостава( bn.getValue() ).remove( bn.СОСТАВИТЕЛЬ );
            наблюдателиПроцесса( bn.getValue() ).remove( bn.МАЛЯР );
        }
    }
    
    private class Составитель implements Наблюдатель<T>
    {
        final Consumer<? super T> РАСКРЫВАТЕЛЬ;

        Составитель( Consumer<? super T> раскрыватель ) 
        {
            РАСКРЫВАТЕЛЬ = раскрыватель;
        }
        
        @Override
        public void отклик( Изменение<T> изменение ) 
        {
            if( изменение.ПРЕЖНЕЕ != null )
                Platform.runLater( () -> удалить( изменение.ПРЕЖНЕЕ ) );
            if( изменение.АКТУАЛЬНОЕ != null )
                Platform.runLater( () -> вставить( изменение.АКТУАЛЬНОЕ ) );
        }

        void вставить( T t )
        {
            РАСКРЫВАТЕЛЬ.accept( t );
        }

        void удалить( T t )
        {
            TreeItem<T> удаляемый = null;
            for( TreeItem<T> узел : BrowserNode.this.getChildren() )
                if( t.equals( узел.getValue() ) )
                {
                    удаляемый = узел;
                    break;
                }
            if( удаляемый != null )
            {
                removeTreeItemChildren( удаляемый );
                BrowserNode.this.getChildren().remove( удаляемый );
            }
        }

    }
    
    private class Маляр implements Наблюдатель<Состояние>
    {
        final Consumer<? super Состояние> МАЛЯР;

        Маляр( Consumer<? super Состояние> маляр ) 
        {
            МАЛЯР = маляр;
        }
        
        @Override
        public void отклик( Изменение<Состояние> изменение ) 
        {
            Platform.runLater( () -> МАЛЯР.accept( изменение.АКТУАЛЬНОЕ ) );
        }
    }

    private static Collection<Наблюдатель<Состояние>> наблюдателиПроцесса( Object t ) 
    {
        if( t instanceof Процесс && t instanceof Наблюдаемый )
        {
            return ((Наблюдаемый)t).наблюдатели();
        }
        return НН;
    }

    private static <T> Collection<Наблюдатель<T>> наблюдателиСостава( T t ) 
    {
        if( t instanceof Составной )
        {
            Collection состав = ((Составной)t).состав();
            if( состав instanceof Наблюдаемый )
            {
                return ((Наблюдаемый)состав).наблюдатели();
            }
        }
        return НН;
    }
    
    private static class NullCollection<E> extends AbstractCollection<E>
    {

        @Override
        public Iterator<E> iterator() 
        {
            return Collections.<E>emptyIterator();
        }

        @Override
        public int size() 
        {
            return 0;
        }
        
        @Override
        public boolean add( E e )
        {
            return false;
        }
        
        @Override
        public boolean remove( Object o )
        {
            return false;
        }
        
    }

}
