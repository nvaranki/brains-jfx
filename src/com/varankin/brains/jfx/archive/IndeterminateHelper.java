package com.varankin.brains.jfx.archive;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Менеджер состояния 
 * {@linkplain javafx.scene.control.CheckBox#indeterminateProperty() неопределенности}.
 *
 * @author &copy; 2016 Николай Варанкин
 */
final class IndeterminateHelper implements ChangeListener<Boolean>
{
    private final BooleanProperty indeterminateProperty;
    private final Property<Boolean> modelProperty;
    private final ChangeListener<Boolean> icl;

    IndeterminateHelper( BooleanProperty indeterminateProperty, Property<Boolean> modelProperty )
    {
        this.indeterminateProperty = indeterminateProperty;
        this.modelProperty = modelProperty;
        this.icl = (v, o, n) -> { if( n ) modelProperty.setValue( null ); };
        indeterminateProperty.addListener( icl );
        modelProperty.addListener( IndeterminateHelper.this );
        changed( null, null, modelProperty.getValue() );
    }

    @Override
    public void changed( ObservableValue<? extends Boolean> v, Boolean o, Boolean n )
    {
        Platform.runLater( () -> indeterminateProperty.set( n == null ) );
    }

    void removeListeners()
    {
        indeterminateProperty.removeListener( icl );
        modelProperty.removeListener( this );
    }
    
}
