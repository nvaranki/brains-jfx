package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import com.varankin.brains.io.xml.Xml;
import com.varankin.brains.io.xml.XmlBrains;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javafx.scene.*;
import javafx.scene.text.Text;

import static com.varankin.brains.io.xml.XmlSvg.*;

/**
 *
 * @author Николай
 */
class EdtПараметр extends EdtЭлемент<DbПараметр>
{
    EdtПараметр( DbПараметр элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        for( DbПараметр э : ЭЛЕМЕНТ.параметры() )
            group.getChildren().add( new EdtПараметр( э ).загрузить( false ) );
        for( DbКлассJava э : ЭЛЕМЕНТ.классы() )
            group.getChildren().add( new EdtКлассJava( э ).загрузить( false ) );
        
        return group;
    }
    
    @Override
    public List<DbАтрибутный.Ключ> компоненты()
    {
        List<DbАтрибутный.Ключ> list = new ArrayList<>( super.компоненты() );
        list.add( 0, new КлючImpl( XmlBrains.XML_PARAMETER, XmlBrains.XMLNS_BRAINS, null ) );
        list.add( 1, new КлючImpl( XmlBrains.XML_JAVA, XmlBrains.XMLNS_BRAINS, null ) );
        return list;
    }

    @Override
    protected Text текст( DbТекстовыйБлок н )
    {
        Text text = super.текст( н );
        text.setX( 300 );
        return text;
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        DbАрхив архив = ЭЛЕМЕНТ.архив();
        DbТекстовыйБлок блок;
        DbГрафика графика;
        DbИнструкция инструкция;
        int[] a, xy;
        
        // название, значение и позиция заголовка параметра
        ЭЛЕМЕНТ.определить( XmlBrains.XML_NAME, XmlBrains.XMLNS_BRAINS, "Новый параметр" );
        блок = (DbТекстовыйБлок)архив.создатьНовыйЭлемент( Xml.XML_CDATA, null );
        блок.текст( "Значение" );
        ЭЛЕМЕНТ.тексты().add( блок );
        if( path.isEmpty() ) return false;
        a = path.poll();
        ЭЛЕМЕНТ.определить( SVG_ATTR_TRANSFORM, XMLNS_SVG,  
                String.format( "translate(%d,%d)", a[0], a[1] ) );
        
        // заголовок
//        графика = (DbГрафика)архив.создатьНовыйЭлемент( SVG_ELEMENT_TEXT, XMLNS_SVG );
//        //графика.определить( SVG_ATTR_X, XMLNS_SVG, 0 );
//        //графика.определить( SVG_ATTR_Y, XMLNS_SVG, 0 );
//        блок = (DbТекстовыйБлок)архив.создатьНовыйЭлемент( Xml.XML_CDATA, null );
//        блок.текст( "Параметр" );
//        графика.тексты().add( блок );
//        ЭЛЕМЕНТ.графики().add( графика );
        
        // название параметра в заголовок
        графика = (DbГрафика)архив.создатьНовыйЭлемент( SVG_ELEMENT_TEXT, XMLNS_SVG );
        //xy = path.isEmpty() ? new int[]{+80,0} : отн( path.poll(), a );
        //графика.определить( SVG_ATTR_X, XMLNS_SVG, xy[0] );
        //графика.определить( SVG_ATTR_Y, XMLNS_SVG, xy[1] );
        инструкция = (DbИнструкция)архив.создатьНовыйЭлемент( Xml.PI_ELEMENT, null );
        инструкция.процессор( "xpath" );
        инструкция.код( "../@name" );
        графика.инструкции().add( инструкция );
        ЭЛЕМЕНТ.графики().add( графика );
        
        // значение параметра в заголовок
        графика = (DbГрафика)архив.создатьНовыйЭлемент( SVG_ELEMENT_TEXT, XMLNS_SVG );
        xy = path.isEmpty() ? new int[]{+120,0} : отн( path.poll(), a );
        графика.определить( SVG_ATTR_X, XMLNS_SVG, xy[0] );
        графика.определить( SVG_ATTR_Y, XMLNS_SVG, xy[1] );
        инструкция = (DbИнструкция)архив.создатьНовыйЭлемент( Xml.PI_ELEMENT, null );
        инструкция.процессор( "xpath" );
        инструкция.код( "../text()" );
        графика.инструкции().add( инструкция );
        ЭЛЕМЕНТ.графики().add( графика );
        
        return path.isEmpty();
    }
}
