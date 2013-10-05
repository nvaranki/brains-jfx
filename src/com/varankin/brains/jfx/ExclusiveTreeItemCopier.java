package com.varankin.brains.jfx;

import com.varankin.brains.artificial.Элемент;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.scene.control.TreeItem;

/**
 * Агент эксклюзивный копирования. Копирует исходный список только 
 * если ВСЕ его элементы - заданного типа.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
class ExclusiveTreeItemCopier implements ObservableListMirror.Copier<TreeItem<Элемент>>
{
    private final Class<?> КЛАСС;

    /**
     * @param класс класс - фильтр объектов в TreeItem.
     */
    ExclusiveTreeItemCopier( Class<?> класс )
    {
        КЛАСС = класс;
    }
    
    @Override
    public void copy( List<TreeItem<Элемент>> source, List<TreeItem<Элемент>> target )
    {
        target.retainAll( source ); // .retainAll(...) сигналит по необходимости :)
        Collection<TreeItem<Элемент>> tmp = new ArrayList<>( source );
        tmp.removeAll( target );
        for( TreeItem<Элемент> item : tmp )
            if( item == null || !КЛАСС.isInstance( item.getValue() ) )
            {
                target.clear(); // это означает Exclusive
                return;
            }
        if( !tmp.isEmpty() ) // .addAll(...) сигналит всегда :(
            target.addAll( tmp );
    }

}
