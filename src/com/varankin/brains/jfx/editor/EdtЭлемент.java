package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.type.DbЭлемент;
import com.varankin.brains.db.xml.BrainsКлюч;
import com.varankin.brains.db.xml.SvgКлюч;
import com.varankin.brains.db.xml.ЗонныйКлюч;
import com.varankin.brains.db.xml.XmlBrains;
import com.varankin.io.xml.svg.XmlSvg;
import com.varankin.brains.jfx.db.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.ObservableList;
import javafx.scene.*;

import static com.varankin.io.xml.svg.XmlSvg.*;

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
        FxProperty атрибут = ЭЛЕМЕНТ.атрибут( XmlSvg.SVG_ATTR_TRANSFORM, XmlSvg.XMLNS_SVG, FxProperty.class );
        атрибут.setValue( String.format( "translate(%d,%d)", s[0], s[1] ) );
        return true;
    }
    
    @Override
    public List<ЗонныйКлюч> компоненты()
    {
        List<ЗонныйКлюч> list = new ArrayList<>( Arrays.asList( 
                new BrainsКлюч( XmlBrains.XML_NOTE ), 
                new SvgКлюч( SVG_ELEMENT_CIRCLE ), 
                new SvgКлюч( SVG_ELEMENT_ELLIPSE ), 
                new SvgКлюч( SVG_ELEMENT_RECT ), 
                new SvgКлюч( SVG_ELEMENT_LINE ),
                new SvgКлюч( SVG_ELEMENT_POLYLINE ), 
                new SvgКлюч( SVG_ELEMENT_POLYGON ), 
                new SvgКлюч( SVG_ELEMENT_TEXT ) ) );
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
    
    protected List<Node> загрузить( ReadOnlyListProperty<? extends FxАтрибутный<?>> p, int pos, String тип )
    {
        компоненты.add( pos, new BrainsКлюч( тип ) );
        return загрузить( p );
    }
    
    void позиция( int[] xy )
    {
        FxProperty атрибут = ЭЛЕМЕНТ.атрибут( SVG_ATTR_TRANSFORM, XMLNS_SVG, FxProperty.class );
        атрибут.setValue( String.format( "translate(%d,%d)", xy[0], xy[1] ) );
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
        графика.атрибут( SVG_ATTR_X ).setValue( xy[0] );
        графика.атрибут( SVG_ATTR_Y ).setValue( xy[1] );
        return графика;
    }
    
}
