package com.varankin.brains.jfx;

import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.artificial.factory.structured.Структурный;
import com.varankin.brains.artificial.io.Фабрика;
import com.varankin.brains.artificial.Элемент;
import com.varankin.property.PropertyMonitor;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

/**
 * Узел дерева для произвольного объекта.
 */
class BrowserNode extends TreeItem<Элемент>
{
    private final String метка;
    private PropertyChangeListener монитор;
    private PropertyChangeListener наблюдатель;

    BrowserNode( Элемент элемент, String метка, Node image )
    {
        super( элемент, image );
        this.метка = метка;
    }
    
    PropertyChangeListener addMonitor( BrowserNodeBuilder строитель, PropertyChangeListener наблюдатель )
    {
        PropertyMonitor pmp = getMonitoredProcess();
        if( pmp != null )
        {
            if( наблюдатель != null )
            {
                pmp.addPropertyChangeListener( наблюдатель );
                this.наблюдатель = наблюдатель;
            }
            наблюдатель = строитель.фабрикаМониторов().создать( this );
        }
        return наблюдатель;
    }
    
    void expand( BrowserNodeBuilder строитель, PropertyChangeListener наблюдатель )
    {
        Элемент элемент = getValue();
        if( элемент instanceof Структурный )
        {
            for( Элемент э : ((Структурный)элемент).элементы() )
            {
                BrowserNode вставка = строитель.узел( э );
                getChildren().add( вставка );
                вставка.expand( строитель, вставка.addMonitor( строитель, наблюдатель ) );
            }
        }
    }
    
    PropertyMonitor getMonitoredProcess()
    {
        Элемент элемент = getValue();
        return элемент instanceof Процесс && элемент instanceof PropertyMonitor ? 
                (PropertyMonitor)элемент : null;
    }

    private PropertyMonitor getMonitoredElement()
    {
        Элемент элемент = getValue();
        return элемент instanceof PropertyMonitor ? (PropertyMonitor)элемент : null;
    }

    private PropertyMonitor getMonitoredCollection()
    {
        Элемент элемент = getValue();
        if( элемент instanceof Структурный )
        {
            Collection<Элемент> элементы = ( (Структурный)элемент ).элементы();
            return элементы instanceof PropertyMonitor ? (PropertyMonitor)элементы : null;
        }
        return null;
    }

    void addMonitor( Фабрика<BrowserNode,PropertyChangeListener> фабрика )
    {
        PropertyMonitor pme = getMonitoredElement();
        PropertyMonitor pmc = getMonitoredCollection();
        if( pme != null || pmc != null )
        {
            монитор = фабрика.создать( this );
            if( pme != null ) pme.addPropertyChangeListener( монитор );
            if( pmc != null ) pmc.addPropertyChangeListener( монитор );
        }
    }

    @Deprecated
    void _addMonitor( BrowserNodeBuilder строитель, PropertyChangeListener наблюдатель )
    {
        Элемент элемент = getValue();
        boolean структурный = элемент instanceof Структурный;
        boolean property = элемент instanceof PropertyMonitor;
        boolean process = элемент instanceof Процесс;
        if( структурный || property )
            монитор = new BrowserMonitor( this, строитель );
        if( property )
        {
            PropertyMonitor pm = (PropertyMonitor)элемент;
            pm.addPropertyChangeListener( монитор );
            if( process && наблюдатель != null )
                pm.addPropertyChangeListener( this.наблюдатель = наблюдатель );
        }
        if( структурный )
        {
            Collection<Элемент> элементы = ( (Структурный)элемент ).элементы();
            if( элементы instanceof PropertyMonitor )
                ( (PropertyMonitor)элементы ).addPropertyChangeListener( монитор );
        }
    }

    void removeMonitor()
    {
        Элемент элемент = getValue();
        if( монитор != null )
        {
            if( элемент instanceof PropertyMonitor )
                ( (PropertyMonitor)элемент ).removePropertyChangeListener( монитор );
            if( элемент instanceof Структурный )
            {
                Collection<Элемент> элементы = ( (Структурный)элемент ).элементы();
                if( элементы instanceof PropertyMonitor )
                    ( (PropertyMonitor)элементы ).removePropertyChangeListener( монитор );
            }
            монитор = null;
        }
        if( наблюдатель != null )
        {
            if( элемент instanceof PropertyMonitor )
                ( (PropertyMonitor)элемент ).removePropertyChangeListener( наблюдатель );
            наблюдатель = null;
        }
    }

    PropertyChangeListener наблюдатель()
    {
        PropertyChangeListener н = наблюдатель;
        TreeItem<Элемент> item = this;
        while( н == null && item != null )
        {
            TreeItem<Элемент> parent = item.getParent();
            if( parent instanceof BrowserNode )
                н = ( (BrowserNode)parent ).наблюдатель;
            else if( parent == null )
                н = ((BrowserNode)item).монитор;
            item = parent;
        }
        return н;
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
