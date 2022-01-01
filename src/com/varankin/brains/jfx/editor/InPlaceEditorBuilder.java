package com.varankin.brains.jfx.editor;

import java.util.Collection;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import com.varankin.brains.db.type.DbАтрибутный;
import com.varankin.brains.db.xml.XmlBrains;

import static com.varankin.brains.db.type.DbАтрибутный.*;
/**
 *
 * @author Николай
 */
public interface InPlaceEditorBuilder
{

    public static double asDouble( DbАтрибутный элемент, String атрибут, double нет )
    {
        Double v = toDoubleValue( элемент.атрибут( атрибут, XmlBrains.XMLNS_BRAINS, нет ) );
        return v != null ? v : нет;
    }

    public static String asString( DbАтрибутный элемент, String атрибут, String нет )
    {
        String v = toStringValue( элемент.атрибут( атрибут, XmlBrains.XMLNS_BRAINS, нет ) );
        return v != null ? v : нет;
    }

    public static Color asColor( DbАтрибутный элемент, String атрибут, String нет )
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
