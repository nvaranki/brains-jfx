package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАрхив;
import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbГрафика;
import com.varankin.brains.db.DbИнструкция;
import com.varankin.brains.db.DbПроцессор;
import com.varankin.brains.db.КлючImpl;
import com.varankin.brains.io.xml.Xml;
import com.varankin.brains.io.xml.XmlBrains;
import com.varankin.brains.jfx.db.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javafx.collections.ObservableList;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlBrains.*;
import static com.varankin.brains.io.xml.XmlSvg.*;

/**
 *
 * @author Николай
 */
class EdtПроцессор extends EdtЭлемент<DbПроцессор,FxПроцессор>
{
    EdtПроцессор( FxПроцессор элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );
        ObservableList<Node> children = group.getChildren();
        children.addAll( загрузить( ЭЛЕМЕНТ.параметры() ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.классы() ) );

        return group;
    }
    
    @Override
    public List<DbАтрибутный.Ключ> компоненты()
    {
        List<DbАтрибутный.Ключ> list = new ArrayList<>( super.компоненты() );
        list.add( 0, new КлючImpl( XML_PARAMETER, XMLNS_BRAINS, null ) );
        list.add( 1, new КлючImpl( XML_JAVA, XMLNS_BRAINS, null ) );
        return list;
    }

    @Override
    public boolean составить( Queue<int[]> path )
    {
        DbАрхив архив = ЭЛЕМЕНТ.getSource().архив();
        DbГрафика графика;
        DbИнструкция инструкция;
        int[] a, xy;
        
        // название
        ЭЛЕМЕНТ.getSource().определить( XmlBrains.XML_NAME, XmlBrains.XMLNS_BRAINS, "Новый процессор" );
        if( path.isEmpty() ) return false;
        a = path.poll();
        ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_TRANSFORM, XMLNS_SVG,  
                String.format( "translate(%d,%d)", a[0], a[1] ) );
        
        // видимое название 
        графика = (DbГрафика)архив.создатьНовыйЭлемент( SVG_ELEMENT_TEXT, XMLNS_SVG );
        графика.определить( SVG_ATTR_FILL, XMLNS_SVG, "black" );
        графика.определить( SVG_ATTR_FONT_SIZE, XMLNS_SVG, 10 );
        инструкция = (DbИнструкция)архив.создатьНовыйЭлемент( Xml.PI_ELEMENT, null );
        инструкция.процессор( "xpath" );
        инструкция.код( "../@name" );
        графика.инструкции().add( инструкция );
        ЭЛЕМЕНТ.getSource().графики().add( графика );
        
        // класс точки 
        
        // параметр точки 
        
        return path.isEmpty();
    }

}
