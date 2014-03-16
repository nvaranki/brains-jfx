package com.varankin.brains.jfx.shared;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Локализованная фабрика ячеек класса {@link Enum}.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class EnumCallBack<T extends Enum<T>> implements Callback<ListView<T>, ListCell<T>>
{
    private final Class<T> CLASS;

    public EnumCallBack( Class<T> c )
    {
        CLASS = c;
    }
   
    @Override
    public ListCell<T> call( ListView<T> view )
    {
        return new EnumCell<>( CLASS );
    }

}
