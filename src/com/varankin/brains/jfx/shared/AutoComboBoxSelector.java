package com.varankin.brains.jfx.shared;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

/**
 * Автоматический селектор заданного элемента из списка {@link ComboBox},
 * как только он заменяется.
 * 
 * @param <E> класс элемента списка.
 *
 * @author &copy; 2014 Николай Варанкин
 */
public class AutoComboBoxSelector<E> implements ChangeListener<ObservableList<E>>
{
    private final ComboBox<E> box;
    private final int index;

    public AutoComboBoxSelector( ComboBox<E> node, int select )
    {
        box = node;
        index = select;
    }

    @Override
    public void changed( ObservableValue<? extends ObservableList<E>> observable, 
            ObservableList<E> oldValue, ObservableList<E> newValue )
    {
        if( newValue != null && !newValue.isEmpty() )
        {
            box.selectionModelProperty().getValue().select( index );
        }
    }
    
}
