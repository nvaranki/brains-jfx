package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.jfx.JavaFX;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;

/**
 * Модификация класса {@link TreeItem} для навигатора по проектам, 
 * с общими потомками для всех коллекций. 
 * Выполняется отложенное построение структуры дерева, только на один уровень,
 * для экономии ресурсов и времени. 
 * 
 * @author &copy; 2016 Николай Варанкин
 */
final class MergedTreeItem extends AbstractTreeItem
{

    /**
     * Узел дерева для элемента.
     * 
     * @param элемент владелец узла.
     */
    MergedTreeItem( DbАтрибутный элемент )
    {
        super( э -> new MergedTreeItem( э ), элемент );
        textProperty().setValue( ЗАГРУЗКА ); // временно, до загрузки узла
        JavaFX.getInstance().execute( new LookupTask() ); // марка, метка и подсказка
    }

    @Override
    protected void onExpandedChanged( ObservableValue<? extends Boolean> v, Boolean o, Boolean n )
    {
        if( n ) 
            JavaFX.getInstance().execute(
                new PopulateTask( (Iterable)коллекции( getValue() ).values() ) );
            // PopulateTask удалит временных потомков и пд в случае успеха
    }

}
