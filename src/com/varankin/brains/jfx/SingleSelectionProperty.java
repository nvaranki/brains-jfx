package com.varankin.brains.jfx;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.control.SingleSelectionModel;

/**
 *
 * @author Николай
 */
public class SingleSelectionProperty<T> extends SimpleObjectProperty<T>
{
    private SingleSelectionModel<T> model;
    private final ChangeListener<T> selectionListener, propertyListener;

    public SingleSelectionProperty()
    {
        selectionListener = new ValueSetter<>( this );
        propertyListener = new SelectionSetter();
    }

    public SingleSelectionProperty( T initialValue )
    {
        super( initialValue );
        selectionListener = new ValueSetter<>( this );
        propertyListener = new SelectionSetter();
    }

    public SingleSelectionProperty( Object bean, String name )
    {
        super( bean, name );
        selectionListener = new ValueSetter<>( this );
        propertyListener = new SelectionSetter();
    }

    public SingleSelectionProperty( Object bean, String name, T initialValue )
    {
        super( bean, name, initialValue );
        selectionListener = new ValueSetter<>( this );
        propertyListener = new SelectionSetter();
    }

    public void setModel( SingleSelectionModel<T> model )
    {
        this.model = model;
        model.selectedItemProperty()
                .addListener( new WeakChangeListener<>( selectionListener ) ); //TODO Weak?
        addListener( new WeakChangeListener<>( propertyListener ) ); //TODO Weak?
    }
    
    private class SelectionSetter implements ChangeListener<T>
    {
        @Override
        public void changed( ObservableValue<? extends T> __, T oldValue, T newValue )
        {
            if( newValue != null )
                model.select( newValue );
            else
                model.clearSelection();
                
//            if( newValue != null ? !newValue.equals( oldValue ) : oldValue != null )
//            {
//                model.select( newValue );
//                int index = target.getItems().indexOf( newValue );
//                if( index >= 0 )
//                    model.select( index );
//                else if( newValue == null )
//                    model.clearSelection();
//                else
//                    LOGGER.log( Level.FINE, "Unsupported TimeUnit.{}", newValue );
//            }
        }
    }
    
}
