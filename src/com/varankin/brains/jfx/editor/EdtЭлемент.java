package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import com.varankin.brains.io.xml.XmlBrains;
import com.varankin.brains.io.xml.XmlSvg;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlSvg.*;

/**
 *
 * @author Николай
 */
abstract class EdtЭлемент<T extends DbЭлемент> extends EdtУзел<T>
{
    EdtЭлемент( T элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        for( DbЗаметка э : ЭЛЕМЕНТ.заметки() )
            group.getChildren().add( new EdtЗаметка( э ).загрузить( false ) );
        for( DbГрафика э : ЭЛЕМЕНТ.графики() )
            group.getChildren().add( new EdtГрафика( э ).загрузить( false ) );
        
        return group;
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        if( path.isEmpty() ) return false;
        int[] s = path.peek();
        ЭЛЕМЕНТ.определить( XmlSvg.SVG_ATTR_TRANSFORM, XmlSvg.XMLNS_SVG, 
                String.format( "translate(%d,%d)", s[0], s[1] ) );
        return true;
    }
    
    @Override
    public List<DbАтрибутный.Ключ> компоненты()
    {
        List<DbАтрибутный.Ключ> list = new ArrayList<>( Arrays.asList( 
                new КлючImpl( XmlBrains.XML_NOTE, XmlBrains.XMLNS_BRAINS, null ), 
                new КлючImpl( SVG_ELEMENT_CIRCLE, XmlSvg.XMLNS_SVG, null ), 
                new КлючImpl( SVG_ELEMENT_ELLIPSE, XmlSvg.XMLNS_SVG, null ), 
                new КлючImpl( SVG_ELEMENT_RECT, XmlSvg.XMLNS_SVG, null ), 
                new КлючImpl( SVG_ELEMENT_LINE, XmlSvg.XMLNS_SVG, null ),
                new КлючImpl( SVG_ELEMENT_POLYLINE, XmlSvg.XMLNS_SVG, null ), 
                new КлючImpl( SVG_ELEMENT_POLYGON, XmlSvg.XMLNS_SVG, null ), 
                new КлючImpl( SVG_ELEMENT_TEXT, XmlSvg.XMLNS_SVG, null ) ) );
        list.addAll( super.компоненты() );
        return list;
    }
    
}
