package com.varankin.brains.jfx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

/**
 * Extension of {@link TreeItem} to hold text representation of the item.
 * 
 * @author @copy; Nikolai Varankine
 * 
 * @param <T> The type of the {@link #getValue() value} property within TreeItem.
 */
public class TitledTreeItem<T> extends TreeItem<T>
{

    public TitledTreeItem()
    {
    }

    public TitledTreeItem( T value )
    {
        super( value );
    }

    public TitledTreeItem( T value, Node graphic )
    {
        super( value, graphic );
    }
    
    private final StringProperty title = new SimpleStringProperty();
    public final StringProperty titleProperty() { return title; }
    public final String getTitle() { return title.get(); }
    
    @Override
    public String toString() 
    {
        String text = title.get();
        return text == null || text.isEmpty() ? super.toString() : text;
    }
    
}
