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
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Queue;
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
    
    @Deprecated
    @Override
    public Node загрузить( boolean изменяемый, Queue<int[]> path )
    {
        return this.загрузить( изменяемый );
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
        Object a = ЭЛЕМЕНТ.атрибут( атрибут, (DbNameSpace)null, null );
        if( a == null ) return нет;
        List<Double> v = new ArrayList<>();
        if( a.getClass().isArray() && !( a instanceof char[] ) )
            for( int i = 0, max = Array.getLength( a ); i < max; i++ )
                v.add( Array.getDouble( a, i ) );
         else
            for( String p : DbАтрибутный.toStringValue( a ).split( "[\\s,]" ) )
                v.add( Double.valueOf( p.trim() ) );
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
    
    @Override
    public List<DbАтрибутный.Ключ> компоненты()
    {
        return Collections.emptyList();
    }
    
}
