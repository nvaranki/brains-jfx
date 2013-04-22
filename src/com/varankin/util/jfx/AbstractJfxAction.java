package com.varankin.util.jfx;

import com.varankin.util.Текст;
import java.io.InputStream;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.*;
import javafx.scene.Node;
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

    private StringProperty text;
    private ObjectProperty<Tooltip> tooltip;
    private BooleanProperty disable;
    private ObjectProperty<Node> icon;
    
    public final KeyCombination shortcut;

    protected AbstractJfxAction( Текст словарь )
    {
        this.text = new SimpleStringProperty( AbstractJfxAction.this, "text", словарь.текст( NAME ) );
        this.tooltip = new SimpleObjectProperty<>( AbstractJfxAction.this, "tooltip", tooltip( словарь.текст( SHORT_DESCRIPTION ) ) );
        this.icon = new SimpleObjectProperty<>( icon( словарь.текст( SMALL_ICON ) ) );
        this.shortcut = shortcut( словарь.текст( MNEMONIC_KEY ) );
        this.disable = new SimpleBooleanProperty( AbstractJfxAction.this, "disable", false );
    }
    
    protected final Node icon( String value )
    {
        String path = value.toString();
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
    
    public ObjectProperty<Tooltip> tooltipProperty()
    {
        return tooltip;
    }
    
    public ObjectProperty<Node> iconProperty()
    {
        return icon;
    }
    
    public Button makeButton()
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
