package com.varankin.brains.jfx.browser;

import com.varankin.brains.jfx.ObservableListMirror;
import com.varankin.filter.Фильтр;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.scene.control.TreeItem;

/**
 * Агент эксклюзивного копирования. Копирует исходный список только 
 * если ВСЕ его элементы - заданного типа.
 * 
 * @author &copy; 2015 Николай Варанкин
 */
class ExclusiveTreeItemCopier<E> implements ObservableListMirror.Copier<TreeItem<E>>
{
    private final Фильтр<E> ФИЛЬТР;

    /**
     * @param класс класс - фильтр объектов в TreeItem.
     */
    ExclusiveTreeItemCopier( Фильтр<E> фильтр )
    {
        ФИЛЬТР = фильтр;
    }
    
    @Override
    public void copy( List<TreeItem<E>> source, List<TreeItem<E>> target )
    {
        target.retainAll( source ); // .retainAll(...) сигналит по необходимости :)
        Collection<TreeItem<E>> tmp = new ArrayList<>( source );
        tmp.removeAll( target );
        for( TreeItem<E> item : tmp )
            if( item == null || !ФИЛЬТР.пропускает( item.getValue() ) )
            {
                target.clear(); // это означает Exclusive
                return;
            }
        if( !tmp.isEmpty() ) // .addAll(...) сигналит всегда :(
            target.addAll( tmp );
    }
    
}
