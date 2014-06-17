package com.varankin.brains.jfx.editor;

import com.varankin.brains.artificial.io.xml.Xml;
import com.varankin.brains.artificial.io.xml.XmlBrains;
import com.varankin.brains.artificial.io.xml.XmlSvg;
import static com.varankin.brains.artificial.io.xml.XmlSvg.*;
import static com.varankin.brains.db.neo4j.Architect.*;
import com.varankin.brains.db.Инструкция;
import com.varankin.brains.db.Неизвестный;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;


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
                    Text text = new Text();
                    text.setX( toSvgDouble( SVG_ATTR_X, 0d ) );
                    text.setY( toSvgDouble( SVG_ATTR_Y, 0d ) );
                    for( Неизвестный н : ЭЛЕМЕНТ.прочее())
                        if( н.тип().название() == null )
                        {
                            text.setText( н.атрибут( Xml.XML_TEXT, "" ) );
                        }
                        else if( н instanceof Инструкция )
                        {
                            text.setText( ((Инструкция)н).выполнить() ); //TODO или показать?
                        }
                        else
                        {
                            text.setText( "DEBUG: text is here" );
                        }
                    s = toSvgString( SVG_ATTR_FILL, null );
                    if( s != null ) text.setFill( toSvgColor( s ) );
                    s = toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) text.setStroke( toSvgColor( s ) );
                    node = text;
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
