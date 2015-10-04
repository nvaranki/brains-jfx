package com.varankin.brains.jfx;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.binding.ListBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;

/**
 * Список выбранных {@linkplain TreeItem элементов} {@linkplain TreeView дерева}.
 * 
 * @param <E> тип элемента.
 */
public class SelectionListBinding<E> extends ListBinding<E> 
{
    private final ObservableList<E> LIST;
    private final MultipleSelectionModel<TreeItem<E>> MODEL;

    public SelectionListBinding( MultipleSelectionModel<TreeItem<E>> model ) 
    {
        LIST = FXCollections.<E>observableArrayList();
        MODEL = model;
        super.bind( model.getSelectedItems() );
    }

//    /**
//     * Связывает данный список со списком выбора в {@link TreeView}.
//     *
//     * @param list список выбора в {@link TreeView}.
//     */
//    public void bind( ObservableList<TreeItem<E>> list ) 
//    {
//        super.bind( list );
//    }

    @Override
    protected ObservableList<E> computeValue() 
    {
        List<E> value = //tree == null ? Collections.emptyList() :
                getSelectedItems().stream()
                .flatMap( (TreeItem<E> i) -> Stream.of( i.getValue() ) )
                .collect( Collectors.toList() );
        LIST.setAll( value );
        return LIST;
    }

    public ObservableList<TreeItem<E>> getSelectedItems() 
    {
        return MODEL.getSelectedItems();
    }
    
}
