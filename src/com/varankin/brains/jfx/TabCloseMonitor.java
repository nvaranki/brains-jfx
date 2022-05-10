package com.varankin.brains.jfx;

import java.util.Iterator;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;

/**
 * Монитор закрытия закладки пользователем.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class TabCloseMonitor implements ListChangeListener<Tab> 
{
    private final ObservableList<TitledSceneGraph> views;

    TabCloseMonitor( ObservableList<TitledSceneGraph> views )
    {
        this.views = views;
    }

    @Override
    public void onChanged( Change<? extends Tab> change )
    {
        while( change.next() )
            if( change.wasRemoved() )
                for( Tab tab : change.getRemoved() )
                    for( Iterator<TitledSceneGraph> it = views.iterator(); it.hasNext(); )
                        if( it.next().node.equals( tab.getContent() ) )
                            it.remove();
    }

}
