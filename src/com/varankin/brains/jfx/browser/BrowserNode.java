package com.varankin.brains.jfx.browser;

import com.varankin.brains.factory.Proxy;
import com.varankin.brains.factory.structured.Структурный;
import com.varankin.brains.artificial.io.Фабрика;
import com.varankin.brains.artificial.Элемент;
import com.varankin.property.PropertyMonitor;
import java.beans.PropertyChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

/**
 * Узел дерева для произвольного {@linkplain Элемент элемента}.
 */
class BrowserNode extends TreeItem<Элемент>
{
    private final String метка;
    private PropertyChangeListener монитор;

    BrowserNode( Элемент элемент, String метка, Node image )
    {
        super( элемент, image );
        this.метка = метка;
    }
    
    /**
     * Строит поддерево от данного узла.
     * 
     * @param строитель построитель узлов.
     */
    void expand( BrowserNodeBuilder строитель )
    {
        Элемент элемент = getValue();
        if( элемент instanceof Структурный )
            for( Элемент э : ((Структурный)элемент).элементы() )
            {
                BrowserNode вставка = строитель.узел( э );
                getChildren().add( строитель.позиция( вставка, getChildren() ), вставка );
                вставка.expand( строитель );
            }
    }
    
    void addMonitor( Фабрика<BrowserNode,PropertyChangeListener> фабрика )
    {
        Элемент элемент = getValue();
        if( элемент instanceof PropertyMonitor )
        {
            монитор = фабрика.создать( this );
            ( (PropertyMonitor)элемент ).наблюдатели().add( монитор );
        }
        if( элемент instanceof Proxy )
        {
            Элемент оригинал = ((Proxy)элемент).оригинал();
            if( оригинал instanceof PropertyMonitor )
                ( (PropertyMonitor)оригинал ).наблюдатели().add( монитор );
        }
    }

    void removeMonitor()
    {
        Элемент элемент = getValue();
        if( монитор != null )
        {
            if( элемент instanceof PropertyMonitor )
                ( (PropertyMonitor)элемент ).наблюдатели().remove( монитор );
            if( элемент instanceof Proxy )
            {
                Элемент оригинал = ((Proxy)элемент).оригинал();
                if( оригинал instanceof PropertyMonitor )
                    ( (PropertyMonitor)оригинал ).наблюдатели().remove( монитор );
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
