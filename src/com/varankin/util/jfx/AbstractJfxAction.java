package com.varankin.util.jfx;

import com.varankin.util.Текст;
import java.io.InputStream;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.*;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;

/**
 * Контейнер свойств визуально доступного действия, которое автоматически
 * загружает локализованные метки, акселераторы, иконки и т.п.
 *
 * @author &copy; 2012 Николай Варанкин
 */
public abstract class AbstractJfxAction implements EventHandler<ActionEvent>
{
    //TODO copied from javax.swing.Action.java
    public static final String NAME = "Name";
    public static final String SMALL_ICON = "SmallIcon";
    public static final String MNEMONIC_KEY="MnemonicKey";
    //TODO end of copied ...

    private StringProperty text;
    private BooleanProperty disable;
    
    public final Node icon;
    public final KeyCombination shortcut;

    protected AbstractJfxAction( Текст словарь )
    {
        this.text = new SimpleStringProperty( AbstractJfxAction.this, "text", словарь.текст( NAME ) );
        this.icon = icon( словарь.текст( SMALL_ICON ) );
        this.shortcut = shortcut( словарь.текст( MNEMONIC_KEY ) );
        this.disable = new SimpleBooleanProperty( AbstractJfxAction.this, "disable", false );
    }
    
    private Node icon( String value )
    {
        String path = value.toString();
        InputStream stream = path.isEmpty() ? null : getClass().getClassLoader().getResourceAsStream( path );
        return stream != null ? new ImageView( new Image( stream ) ) : null;
    }
    
    private KeyCombination shortcut( String value )
    {
        return value.trim().isEmpty() ? null : new KeyCharacterCombination( value.trim().substring( 0, 1 ) );
    }
    
    public BooleanProperty disableProperty()
    {
        return disable;
    }

    void setEnabled( boolean value )
    {
        disable.setValue( !value );
    }

    public StringProperty textProperty()
    {
        return text;
    }
    
}
