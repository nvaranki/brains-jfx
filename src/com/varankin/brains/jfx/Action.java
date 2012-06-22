package com.varankin.brains.jfx;

import com.varankin.util.Текст;

import java.io.InputStream;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
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
public abstract class Action implements EventHandler<ActionEvent>
{
    //TODO copied from javax.swing.Action.java
    public static final String NAME = "Name";
    public static final String SMALL_ICON = "SmallIcon";
    public static final String MNEMONIC_KEY="MnemonicKey";
    //TODO end of copied ...

    private String text;
    private BooleanProperty disable;
    
    protected final JavaFX jfx;
    protected final Текст словарь;

    public final Node icon;
    public final KeyCombination shortcut;
    //public final EventHandler<ActionEvent> handler;

    Action( JavaFX jfx, Текст словарь )
    {
        this.jfx = jfx;
        this.словарь = словарь;
        this.text = словарь.текст( NAME );
        this.icon = icon( словарь.текст( SMALL_ICON ) );
        this.shortcut = shortcut( словарь.текст( MNEMONIC_KEY ) );
        this.disable = new SimpleBooleanProperty( Action.this, "disabled", false );
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
    
    BooleanProperty disableProperty()
    {
        return disable;
    }

    void setEnabled( boolean value )
    {
        disable.setValue( !value );
    }

    String getText()
    { 
        return text;
    }

    void setText( String value )
    {
        text = value != null ? value : "";
        for( MenuItem target : jfx.getMenuItems( this ) )
            target.setText( text );
    }
    
//    static class MenuItemEnabledEventType extends EventType<MenuItemEnabledEvent>
//    {
//    }
//    
//    final class MenuItemEnabledEvent extends Event
//    {
//        private final boolean enabled;
//
//        private MenuItemEnabledEvent( boolean enabled )
//        {
//            super( Action.this, null, new MenuItemEnabledEventType() );
//            this.enabled = enabled;
//        }
//
//        public boolean isEnabled()
//        {
//            return enabled;
//        }
//        
//    }

}
