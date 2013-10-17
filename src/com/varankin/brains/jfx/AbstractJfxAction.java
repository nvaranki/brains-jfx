package com.varankin.brains.jfx;

import com.varankin.util.Текст;
import java.io.InputStream;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.*;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;

/**
 * Контейнер свойств визуально доступного действия, которое автоматически
 * загружает локализованные метки, акселераторы, иконки и т.п.
 *
 * @author &copy; 2013 Николай Варанкин
 */
public abstract class AbstractJfxAction implements EventHandler<ActionEvent>
{
    //TODO copied from javax.swing.Action.java
    public static final String NAME = "Name";
    public static final String SMALL_ICON = "SmallIcon";
    public static final String MNEMONIC_KEY="MnemonicKey";
    public static final String SHORT_DESCRIPTION = "ShortDescription"; // tooltip
    //TODO end of copied ...

    private final Property<String> TEXT;
    private final Property<Tooltip> TOOLTIP;
    private final Property<Boolean> DISABLE;
    private final Property<ImageView> ICON;
    
    public final KeyCombination shortcut;

    protected AbstractJfxAction( Текст словарь )
    {
        this.TEXT = new SimpleStringProperty( AbstractJfxAction.this, "text", словарь.текст( NAME ) );
        this.TOOLTIP = new SimpleObjectProperty<>( AbstractJfxAction.this, "tooltip", tooltip( словарь.текст( SHORT_DESCRIPTION ) ) );
        this.DISABLE = new SimpleBooleanProperty( AbstractJfxAction.this, "disable", false );
        this.ICON = new SimpleObjectProperty<ImageView>( AbstractJfxAction.this, "icon", icon( словарь.текст( SMALL_ICON ) ) )
        {
            @Override
            public ImageView getValue()
            {
                // Node всегда один в одном месте на Scene!!! поэтому всегда дублируется:
                ImageView оригинал = super.getValue();
                return оригинал != null ? new ImageView( оригинал.getImage() ) : null;
            }
        };
        this.shortcut = shortcut( словарь.текст( MNEMONIC_KEY ) );
    }
    
    protected final ImageView icon( String path )
    {
        InputStream stream = path.isEmpty() ? null : getClass().getClassLoader().getResourceAsStream( path );
        return stream != null ? new ImageView( new Image( stream ) ) : null;
    }
    
    protected final Tooltip tooltip( String value )
    {
        return new Tooltip( value != null ? value : "" );
    }
    
    private KeyCombination shortcut( String value )
    {
        return value.trim().isEmpty() ? null : new KeyCharacterCombination( value.trim().substring( 0, 1 ) );
    }
    
    public final Property<Boolean> disableProperty()
    {
        return DISABLE;
    }

    @Deprecated // historical from Swing
    public void setEnabled( boolean value )
    {
        DISABLE.setValue( !value );
    }

    public final Property<String> textProperty()
    {
        return TEXT;
    }
    
    public final Property<Tooltip> tooltipProperty()
    {
        return TOOLTIP;
    }
    
    public final Property<ImageView> iconProperty()
    {
        return ICON;
    }
    
    Button makeButton()
    {
        Button item = new Button();
        item.setOnAction( this );
        item.setMnemonicParsing( false );
        item.setTooltip( new Tooltip() );
        item.tooltipProperty().getValue().textProperty().bind( textProperty() );
        item.graphicProperty().bind( iconProperty() );
        item.disableProperty().bind( disableProperty() );
        return item; 
    }
    
}
