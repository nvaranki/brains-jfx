package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.jfx.db.FxReadOnlyProperty;
import com.varankin.brains.jfx.db.FxАтрибутный;
import static com.varankin.brains.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.io.xml.XmlSvg.XMLNS_SVG;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Transform;

/**
 *
 * @author &copy; 2020 Николай Варанкин
 */
abstract class EdtАтрибутный<D extends DbАтрибутный, T extends FxАтрибутный<D>> 
        implements NodeBuilder
{
    protected final T ЭЛЕМЕНТ;

    protected EdtАтрибутный( T элемент )
    {
        ЭЛЕМЕНТ = элемент;
    }
    
    @Deprecated
    @Override
    public boolean составить( Queue<int[]> path )
    {
        return true;
    }
    
    <T extends Node> T загрузить( T node, boolean основной )
    {
        node.setUserData( ЭЛЕМЕНТ );
        node.setManaged( false );
        if( !основной ) 
            node.getTransforms().addAll( getTransformList() );
        return node;
    }
    
    private List<Transform> getTransformList()
    {
        FxReadOnlyProperty атрибут = ЭЛЕМЕНТ.атрибут( SVG_ATTR_TRANSFORM, XMLNS_SVG, FxReadOnlyProperty.class );
        return toTransforms( DbАтрибутный.toStringValue( атрибут.getValue() ) );
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
    
    protected static Color toSvgColor( String s )
    {
        return "none".equals( s ) ? null : Color.valueOf( s );
    }
    
}
