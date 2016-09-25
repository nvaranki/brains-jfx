package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.*;
import com.varankin.brains.jfx.JavaFX;

import java.util.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;

import static com.varankin.brains.jfx.archive.ArchiveResourceFactory.*;

/**
 * Модификация класса {@link TreeItem} для навигатора по проектам, 
 * с отдельными папками для коллекций. 
 * Выполняется отложенное построение структуры дерева, только на один уровень,
 * для экономии ресурсов и времени. 
 * 
 * @author &copy; 2016 Николай Варанкин
 */
final class FoldedTreeItem extends AbstractTreeItem
{
    private final Коллекция<? extends DbАтрибутный> коллекция;

    /**
     * Узел дерева для элемента.
     * 
     * @param элемент владелец узла.
     */
    FoldedTreeItem( DbАтрибутный элемент )
    {
        super( э -> new FoldedTreeItem( э ), элемент );
        коллекция = null;
        textProperty().setValue( ЗАГРУЗКА ); // временно, до загрузки узла
        JavaFX.getInstance().execute( new LookupTask() ); // марка, метка и подсказка
    }
            
    /**
     * Узел дерева для коллекции элемента.
     * 
     * @param элемент владелец коллекции.
     * @param коллекция коллекция как узел дерева.
     * @param тип тип коллекции.
     */
    private FoldedTreeItem( DbАтрибутный элемент, Коллекция<? extends DbАтрибутный> коллекция, String тип )
    {
        super( э -> new FoldedTreeItem( э ), элемент, марка( тип ) );
        this.коллекция = коллекция;
        textProperty().setValue( метка( тип ) );
    }

    @Override
    protected void onExpandedChanged( ObservableValue<? extends Boolean> v, Boolean o, Boolean n )
    {
        if( n )
            if( коллекция != null )
            {
                // отложенное заполнение коллекции
                JavaFX.getInstance().execute( 
                    new PopulateTask( Collections.singleton( коллекция ) ) );
                // PopulateTask удалит временных потомков и пд в случае успеха
            }
            else
            {
                // отложенная подготовка папок для коллекций
                DbАтрибутный элемент = getValue();
                for( Map.Entry<String,Коллекция> e : коллекции( getValue() ).entrySet() )
                    getChildren().add( new FoldedTreeItem( элемент, e.getValue(), e.getKey() ) );
                удалитьВременныеПотомки();
                expandedProperty().removeListener( пд );
            }
    }
    
}
