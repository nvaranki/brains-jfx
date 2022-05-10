package com.varankin.brains.jfx;

import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.beans.binding.ListBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Связка списка элементов, производных от списка исходных элементов.
 * 
 * @param <S> тип исходного элемента.
 * @param <E> тип производного элемента.
 * 
 * @author &copy; 2015 Николай Варанкин
 */
public class OneToOneListBinding<S,E> extends ListBinding<E> 
{
    private final ObservableList<E> TARGET;
    private final ObservableList<S> SOURCE;
    final Function<S, ? extends E> CONVERTER;

    public OneToOneListBinding( ObservableList<S> source, Function<S, ? extends E> converter ) 
    {
        super.bind( source );
        TARGET = FXCollections.<E>observableArrayList();
        SOURCE = source;
        CONVERTER = converter;
    }

    @Override
    protected final ObservableList<E> computeValue() 
    {
        TARGET.setAll( SOURCE.stream()
                .map( CONVERTER )
                .collect( Collectors.toList() ) );
        return TARGET;
    }

    public final ObservableList<S> getSource() 
    {
        return SOURCE;
    }
    
}
