package com.varankin.brains.jfx.browser;

import com.varankin.brains.factory.Вложенный;
import com.varankin.brains.factory.structured.Структурный;
import com.varankin.brains.artificial.io.Фабрика;
import com.varankin.brains.artificial.Элемент;
import com.varankin.brains.factory.runtime.RtЭлемент;
import com.varankin.property.PropertyMonitor;
import java.beans.PropertyChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

import static com.varankin.brains.factory.Вложенный.извлечь;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * Узел дерева для произвольного элемента.
 */
class BrowserNode<T> extends TreeItem<T>
{
    private final String метка;
    private PropertyChangeListener монитор;

    BrowserNode( T элемент, String метка, Node image )
    {
        super( элемент, image );
        this.метка = метка;
    }
    
    /**
     * Строит поддерево от данного узла.
     * 
     * @param строитель построитель узлов.
     */
    void expand( BrowserNodeBuilder<T> строитель )
    {
        Consumer<? super T> expander = ( T t ) ->
            {
                BrowserNode<T> вставка = строитель.узел( t );
                getChildren().add( строитель.позиция( вставка, getChildren() ), вставка );
                вставка.expand( строитель );
            };
        T value = getValue();
        if( value instanceof RtЭлемент )
        {
            ((RtЭлемент)value).части().значение().stream().forEach( expander );
        }
        else
        {
            Структурный узел = извлечь( Структурный.class, value );
            if( узел != null )
                ((Collection)узел.элементы()).stream().forEach( expander );
        }
    }
    
    void addMonitor( Фабрика<BrowserNode<T>,PropertyChangeListener> фабрика )
    {
        T элемент = getValue();
        if( элемент instanceof PropertyMonitor )
        {
            монитор = фабрика.создать( this );
            ( (PropertyMonitor)элемент ).listeners().add( монитор );
        }
        if( элемент instanceof Вложенный )
        {
            Object оригинал = ((Вложенный)элемент).вложение();
            if( оригинал instanceof PropertyMonitor )
                ( (PropertyMonitor)оригинал ).listeners().add( монитор );
        }
    }

    void removeMonitor()
    {
        T элемент = getValue();
        if( монитор != null )
        {
            if( элемент instanceof PropertyMonitor )
                ( (PropertyMonitor)элемент ).listeners().remove( монитор );
            if( элемент instanceof Вложенный )
            {
                Object оригинал = ((Вложенный)элемент).вложение();
                if( оригинал instanceof PropertyMonitor )
                    ( (PropertyMonitor)оригинал ).listeners().remove( монитор );
            }
            монитор = null;
        }
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
        return метка;
    }
    
}
