package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import com.varankin.brains.io.xml.Xml;
import com.varankin.brains.io.xml.XmlBrains;
import com.varankin.brains.io.xml.XmlSvg;
import com.varankin.brains.jfx.db.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.ObservableList;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlSvg.*;

/**
 *
 * @author Николай
 */
abstract class EdtЭлемент<D extends DbЭлемент, T extends FxЭлемент<D>> extends EdtУзел<D,T>
{
    EdtЭлемент( T элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );
        ObservableList<Node> children = group.getChildren();
        children.addAll( загрузить( ЭЛЕМЕНТ.заметки() ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.графики() ) );

        return group;
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        if( path.isEmpty() ) return false;
        int[] s = path.peek();
        ЭЛЕМЕНТ.getSource().определить( XmlSvg.SVG_ATTR_TRANSFORM, XmlSvg.XMLNS_SVG, 
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
    
    static protected int[] отн( int[] pt, int[] base )
    {
        int[] copy = Arrays.copyOf( pt, pt.length );
        for( int i = 0, max = Math.min( pt.length, base.length ); i < max; i++ )
            copy[i] -= base[i];
        return copy;
    }
    
    protected List<Node> загрузить( ReadOnlyListProperty<? extends FxАтрибутный<?>> p, int pos, String ключ )
    {
        компоненты.add( pos, new КлючImpl( ключ, XmlBrains.XMLNS_BRAINS, null ) );
        return загрузить( p );
    }
    
    void позиция( int[] xy )
    {
        ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_TRANSFORM, XMLNS_SVG,  
                String.format( "translate(%d,%d)", xy[0], xy[1] ) );
    }
    
    /**
     * видимое название 
     * 
     * @param текст
     * @param ссылка
     * @return 
     */
    protected FxГрафика название( String текст, String ссылка )
    {
        ЭЛЕМЕНТ.название().setValue( текст );
        return надпись( ссылка );
    }
    
    protected FxГрафика надпись( String ссылка, int[] xy )
    {
        FxГрафика графика = надпись( ссылка );
        графика.определить( SVG_ATTR_X, xy[0] );
        графика.определить( SVG_ATTR_Y, xy[1] );
        return графика;
    }
    
    protected FxГрафика надпись( String ссылка )
    {
        FxИнструкция инструкция = (FxИнструкция)FxФабрика.getInstance().создать( 
            ЭЛЕМЕНТ.getSource().архив().создатьНовыйЭлемент( Xml.PI_ELEMENT, null ) );
        инструкция.процессор().setValue( "xpath" );
        инструкция.код().setValue( ссылка );
        
        FxГрафика графика = графика( SVG_ELEMENT_TEXT );
        графика.определить( SVG_ATTR_FILL, "black" );
        графика.определить( SVG_ATTR_FONT_SIZE, 10 );
        графика.инструкции().add( инструкция );
        
        return графика;
    }
    
    /**
     * видимое название
     * 
     * @param текст
     * @param ссылка
     * @param x
     * @param y
     * @return 
     */
    protected FxГрафика название( String текст, String ссылка, int[] xy )
    {
        FxГрафика графика = название( текст, ссылка );
        графика.getSource().определить( SVG_ATTR_X, XMLNS_SVG, xy[0] );
        графика.getSource().определить( SVG_ATTR_Y, XMLNS_SVG, xy[1] );
        return графика;
    }
    
}
