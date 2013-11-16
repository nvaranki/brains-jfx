/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.varankin.brains.jfx.analyser;

import java.util.logging.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;

/**
 *
 * @author &copy; 2013 Николай Варанкин
 */
public class ListeningComboBoxSetter<T> implements ChangeListener<T>
{
    private static final Logger LOGGER = Logger.getLogger( ListeningComboBoxSetter.class.getName() );

    private final ComboBox<T> target;

    public ListeningComboBoxSetter( ComboBox<T> comboBox )
    {
        target = comboBox;
    }

    @Override
    public void changed( ObservableValue<? extends T> _, T oldValue, T newValue )
    {
        if( newValue != null ? !newValue.equals( oldValue ) : oldValue != null )
        {
            int index = target.getItems().indexOf( newValue );
            if( index >= 0 )
                target.getSelectionModel().select( index );
            else if( newValue == null )
                target.getSelectionModel().clearSelection();
            else
                LOGGER.log( Level.FINE, "Unsupported TimeUnit.{}", newValue );
        }
    }
    
}
