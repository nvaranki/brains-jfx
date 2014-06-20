package com.varankin.brains.jfx.editor;

import com.varankin.brains.artificial.io.xml.XmlSvg;
import static com.varankin.brains.artificial.io.xml.XmlSvg.*;
import com.varankin.brains.db.Неизвестный;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;


/**
 *
 * @author Николай
 */
class EdtНеизвестный extends EdtАтрибутныйЭлемент<Неизвестный>
{

    EdtНеизвестный( Неизвестный элемент )
    {
        super( элемент );
    }
    
    Node загрузить( boolean изменяемый )
    {
        Node node;
        String s;
        
        if( XmlSvg.XMLNS_SVG.equals( ЭЛЕМЕНТ.тип().uri() ) )
            switch( ЭЛЕМЕНТ.тип().название() )
            {
                case XmlSvg.SVG_ELEMENT_TEXT:
                    SvgTextController tc = new SvgTextController( ЭЛЕМЕНТ, изменяемый );
                    node = tc.build();
                    break;
                    
                case XmlSvg.SVG_ELEMENT_RECT:
                    Rectangle rect = new Rectangle();
                    rect.setX( toSvgDouble( SVG_ATTR_X, 0d ) );
                    rect.setY( toSvgDouble( SVG_ATTR_Y, 0d ) );
                    rect.setWidth( toSvgDouble( SVG_ATTR_WIDTH, 0d ) );
                    rect.setHeight(toSvgDouble( SVG_ATTR_HEIGHT, 0d ) );
                    s = toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) rect.setStroke( toSvgColor( s ) );
                    s = toSvgString( SVG_ATTR_FILL, null );
                    if( s != null ) rect.setFill( toSvgColor( s ) );
                    node = rect;
                    break;
                    
                case XmlSvg.SVG_ELEMENT_LINE:
                    Line line = new Line();
                    line.setStartX( toSvgDouble( SVG_ATTR_X1, 0d ) );
                    line.setStartY( toSvgDouble( SVG_ATTR_Y1, 0d ) );
                    line.setEndX( toSvgDouble( SVG_ATTR_X2, 0d ) );
                    line.setEndY( toSvgDouble( SVG_ATTR_Y2, 0d ) );
                    s = toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) line.setStroke( toSvgColor( s ) );
                    node = line;
                    break;
                    
                case XmlSvg.SVG_ELEMENT_POLYLINE:
                    Polyline path = new Polyline();
                    path.getPoints().setAll( toSvgPoints( SVG_ATTR_POINTS, new Double[]{0d,0d} ) );
                    s = toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) path.setStroke( toSvgColor( s ) );
                    node = path;
                    break;
                    
                case XmlSvg.SVG_ELEMENT_CIRCLE:
                    Circle circle = new Circle();
                    circle.setCenterX( toSvgDouble( SVG_ATTR_CX, 0d ) );
                    circle.setCenterY( toSvgDouble( SVG_ATTR_CY, 0d ) );
                    circle.setRadius( toSvgDouble( SVG_ATTR_R, 0d ) );
                    s = toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) circle.setStroke( toSvgColor( s ) );
                    s = toSvgString( SVG_ATTR_FILL, null );
                    if( s != null ) circle.setFill( toSvgColor( s ) );
                    node = circle;
                    break;
                    
                default:
                    Group group = new Group();
                    for( Неизвестный н : ЭЛЕМЕНТ.прочее() )
                        group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
                    node = group;
            }
        else
        {
            Group group = new Group();
            for( Неизвестный н : ЭЛЕМЕНТ.прочее() )
                group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
            node = group;
        }
        
        node.setUserData( ЭЛЕМЕНТ );
        return node;
    }

    protected static Color toSvgColor( String s )
    {
        return "none".equals( s ) ? null : Color.valueOf( s );
    }
    
    
}   
