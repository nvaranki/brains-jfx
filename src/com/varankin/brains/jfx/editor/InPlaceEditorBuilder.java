package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.Атрибутный;
import java.util.Collection;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 *
 * @author Николай
 */
public interface InPlaceEditorBuilder
{

    public static double asDouble( Атрибутный элемент, String атрибут, double нет )
    {
        Double v = элемент.атрибут( атрибут, нет );
        return v != null ? v : нет;
    }

    public static String asString( Атрибутный элемент, String атрибут, String нет )
    {
        String v = элемент.атрибут( атрибут, нет );
        return v != null ? v : нет;
    }

    public static Color asColor( Атрибутный элемент, String атрибут, String нет )
    {
        String s = asString( элемент, атрибут, нет );
        return s == null || "none".equals( s ) ? null : Color.valueOf( s );
    }
    
    public static Collection<Node> childrenOf( Parent parent )
    {
        Collection<Node> children;
        if( parent instanceof Group )
            children = ((Group)parent).getChildren();
        else if( parent instanceof Pane )
            children = ((Pane)parent).getChildren();
        else
            children = null;//new ArrayList<>(); //TODO LOGGER.log()
        return children;
    }

}
