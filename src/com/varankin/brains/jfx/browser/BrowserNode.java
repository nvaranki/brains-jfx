package com.varankin.brains.jfx.browser;

import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.factory.Вложенный;
import com.varankin.brains.factory.runtime.RtЭлемент;
import com.varankin.brains.factory.structured.Структурный;
import com.varankin.characteristic.Наблюдатель;
import com.varankin.property.PropertyMonitor;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

/**
 * Узел дерева для произвольного элемента.
 *
 * @author &copy; 2015 Николай Варанкин
 */
class BrowserNode<T> extends TreeItem<T>
{
    private final String МЕТКА;
    private final Consumer<? super T> РАСКРЫВАТЕЛЬ;
    private final Consumer<? super Процесс.Состояние> МАЛЯР;
    private PropertyChangeListener монитор;
    private Наблюдатель<T> наблюдатель;

    /**
     * @param элемент
     * @param метка
     * @param image
     * @param строитель построитель узлов.
     */
    BrowserNode( T элемент, String метка, Node image, BrowserNodeBuilder<T> строитель )
    {
        super( элемент, image );
        МЕТКА = метка;
        РАСКРЫВАТЕЛЬ = ( T t ) ->
        {
            BrowserNode<T> вставка = строитель.узел( t );
            getChildren().add( строитель.позиция( вставка, getChildren() ), вставка );
            вставка.expand();
        };
        МАЛЯР = ( Процесс.Состояние с ) ->
        {
            строитель.фабрикаКартинок().setBgColor( getGraphic(), с );
        };
    }
    
    void раскрасить( Процесс.Состояние состояние )
    {
        МАЛЯР.accept( состояние );
    }
    
    void вставить( T элемент )
    {
        РАСКРЫВАТЕЛЬ.accept( элемент );
    }
    
    void удалить( T элемент )
    {
        TreeItem<T> удаляемый = null;
        for( TreeItem<T> узел : getChildren() )
            if( элемент.equals( узел.getValue() ) )
            {
                удаляемый = узел;
                break;
            }
        if( удаляемый != null )
        {
            removeTreeItemChildren( удаляемый );
            getChildren().remove( удаляемый );
        }
    }
    
    /**
     * Строит поддерево от данного узла.
     */
    void expand()
    {
        T value = getValue();
        if( value instanceof RtЭлемент )
        {
            ((RtЭлемент)value).части().значение().stream().forEach( РАСКРЫВАТЕЛЬ );
        }
        else
        {
            Структурный узел = Вложенный.извлечь( Структурный.class, value );
            if( узел != null )
                ((Collection)узел.элементы()).stream().forEach( РАСКРЫВАТЕЛЬ );
        }
    }
    
    void addMonitor()
    {
        T value = getValue();
        
        PropertyMonitor pm = Вложенный.извлечь( PropertyMonitor.class, value );
        if( pm != null )
            pm.listeners().add( монитор = new BrowserMonitor<>( this ) );
        
        if( value instanceof RtЭлемент )
            ((RtЭлемент)value).части().наблюдатели()
                    .add( наблюдатель = new BrowserObserver<>( this ) );
        //TODO Процесс.СОСТОЯНИЕ
    }

    void removeMonitor()
    {
        T value = getValue();
        
        PropertyMonitor pm = Вложенный.извлечь( PropertyMonitor.class, value );
        if( pm != null )
            pm.listeners().remove( монитор );
        монитор = null;
        
        if( value instanceof RtЭлемент )
            ((RtЭлемент)value).части().наблюдатели().remove( наблюдатель );
        наблюдатель = null;
        //TODO Процесс.СОСТОЯНИЕ
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
            ((BrowserNode)узел).removeMonitor();
    }

}
