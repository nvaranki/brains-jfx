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
                    node = text;
                    break;
                    
                case XmlSvg.SVG_ELEMENT_POLYLINE:
                    Polyline path = new Polyline();
                    path.getPoints().setAll( toSvgPoints( SVG_ATTR_POINTS, new Double[]{0d,0d} ) );
                    path.setStroke( Color.valueOf( toSvgString( SVG_ATTR_STROKE, "black" ) ) );
                    node = path;
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
    
    
}   
