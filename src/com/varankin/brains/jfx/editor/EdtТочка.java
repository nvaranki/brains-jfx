package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import com.varankin.brains.io.xml.Xml;
import com.varankin.brains.io.xml.XmlBrains;
import com.varankin.brains.jfx.db.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlSvg.*;

/**
 *
 * @author Николай
 */
class EdtТочка extends EdtЭлемент<DbТочка,FxТочка>
{
    EdtТочка( FxТочка элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        for( FxТочка э : ЭЛЕМЕНТ.точки() )
            group.getChildren().add( new EdtТочка( э ).загрузить( false ) );
        for( FxПараметр э : ЭЛЕМЕНТ.параметры() )
            group.getChildren().add( new EdtПараметр( э ).загрузить( false ) );
        for( FxКлассJava э : ЭЛЕМЕНТ.классы() )
            group.getChildren().add( new EdtКлассJava( э ).загрузить( false ) );
        
        return group;
    }
    
    @Override
    public List<DbАтрибутный.Ключ> компоненты()
    {
        List<DbАтрибутный.Ключ> list = new ArrayList<>( super.компоненты() );
        list.add( 0, new КлючImpl( XmlBrains.XML_JAVA, XmlBrains.XMLNS_BRAINS, null ) );
        list.add( 1, new КлючImpl( XmlBrains.XML_PARAMETER, XmlBrains.XMLNS_BRAINS, null ) );
        list.add( 2, new КлючImpl( XmlBrains.XML_POINT, XmlBrains.XMLNS_BRAINS, null ) );
        return list;
    }

    @Override
    public boolean составить( Queue<int[]> path )
    {
        DbАрхив архив = ЭЛЕМЕНТ.getSource().архив();
        DbГрафика графика;
        DbИнструкция инструкция;
        int[] a, xy;
        
        // название, тип и позиция заголовка точки
        ЭЛЕМЕНТ.getSource().определить( XmlBrains.XML_NAME, XmlBrains.XMLNS_BRAINS, "Новая точка" );
        ЭЛЕМЕНТ.getSource().определить( XmlBrains.XML_PIN, XmlBrains.XMLNS_BRAINS, "Контакт" );
        if( path.isEmpty() ) return false;
        a = path.poll();
        ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_TRANSFORM, XMLNS_SVG,  
                String.format( "translate(%d,%d)", a[0], a[1] ) );
        
        // изображение
        графика = (DbГрафика)архив.создатьНовыйЭлемент( SVG_ELEMENT_CIRCLE, XMLNS_SVG );
        графика.определить( SVG_ATTR_R, XMLNS_SVG, 7 );
        графика.определить( SVG_ATTR_FILL, XMLNS_SVG, "none" );
        графика.определить( SVG_ATTR_STROKE, XMLNS_SVG, "black" );
        ЭЛЕМЕНТ.getSource().графики().add( графика );
        
        // название точки 
        графика = (DbГрафика)архив.создатьНовыйЭлемент( SVG_ELEMENT_TEXT, XMLNS_SVG );
        xy = path.isEmpty() ? new int[]{+7,-7} : отн( path.poll(), a );
        графика.определить( SVG_ATTR_X, XMLNS_SVG, xy[0] );
        графика.определить( SVG_ATTR_Y, XMLNS_SVG, xy[1] );
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
