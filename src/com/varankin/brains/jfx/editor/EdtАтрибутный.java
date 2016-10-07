package com.varankin.brains.jfx.editor;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbNameSpace;
import javafx.scene.transform.Transform;

import static com.varankin.brains.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.io.xml.XmlSvg.XMLNS_SVG;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

/**
 *
 * @author Николай
 */
abstract class EdtАтрибутный<T extends DbАтрибутный> implements NodeBuilder
{
    protected final T ЭЛЕМЕНТ;

    protected EdtАтрибутный( T элемент )
    {
        ЭЛЕМЕНТ = элемент;
    }
    
    <T extends Node> T загрузить( T node, boolean изменяемый )
    {
        if( изменяемый ) 
            node.setUserData( ЭЛЕМЕНТ );
        else
            node.getTransforms().addAll( getTransformList() );
        return node;
    }
    
    private List<Transform> getTransformList()
    {
        return toTransforms( DbАтрибутный.toStringValue( ЭЛЕМЕНТ.атрибут( 
                SVG_ATTR_TRANSFORM, XMLNS_SVG, "" ) ) );
    }

    double toSvgDouble( String атрибут, double нет )
    {
        Double v = ЭЛЕМЕНТ.атрибут( атрибут, нет );
        return v != null ? v : нет;
    }

    String toSvgString( String атрибут, String нет )
    {
        String v = ЭЛЕМЕНТ.атрибут( атрибут, нет );
        return v != null ? v : нет;
    }

    Double[] toSvgPoints( String атрибут, Double[] нет )
    {
        Object a = ЭЛЕМЕНТ.атрибут(атрибут, (DbNameSpace)null, null );
        if( a == null )
        {
            return нет;
        }
        List<Double> v = new ArrayList<>();
        for( String p : DbАтрибутный.toStringValue( a ).split( "\\s" ) )
        {
            String[] xy = p.split( "," );
            v.add( Double.valueOf( xy[0].trim() ) );
            v.add( Double.valueOf( xy[1].trim() ) );
        }
        return v.isEmpty() ? нет : v.toArray( new Double[v.size()] );
    }
    
    protected Node createMarker( double s2 )
    {
        Polygon countor = new Polygon( 0d, -s2, s2, s2, -s2, s2 );
        countor.setStroke( Color.RED );
        countor.setFill( Color.RED );
        Line dot = new Line( 0d, 0d, 0d, 0d );
        dot.setStroke( Color.BLACK );
        Group group = new Group();
        group.getChildren().addAll( dot, countor );
        return group;
    }
    
}
