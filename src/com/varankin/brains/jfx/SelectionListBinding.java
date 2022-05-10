package com.varankin.brains.jfx;

import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;

/**
 * Список выбранных {@linkplain TreeItem элементов} {@linkplain TreeView дерева}.
 * 
 * @param <E> тип элемента.
 * 
 * @author &copy; 2015 Николай Варанкин
 */
public class SelectionListBinding<E> extends OneToOneListBinding<TreeItem<E>,E> 
{
    @Deprecated
    public SelectionListBinding( MultipleSelectionModel<TreeItem<E>> model ) 
    {
        this( model.getSelectedItems() );
    }

    public SelectionListBinding( ObservableList<TreeItem<E>> selection ) 
    {
        super( selection, (TreeItem<E> i) -> i.getValue() );
    }

    public ObservableList<TreeItem<E>> getSelectedItems() 
    {
        return getSource();
    }
    
}
