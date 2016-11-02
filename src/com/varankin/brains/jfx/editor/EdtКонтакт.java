package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import com.varankin.brains.io.xml.Xml;
import com.varankin.brains.io.xml.XmlBrains;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlSvg.*;

/**
 *
 * @author Николай
 */
class EdtКонтакт extends EdtЭлемент<DbКонтакт>
{
    EdtКонтакт( DbКонтакт элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        for( DbПараметр н : ЭЛЕМЕНТ.параметры() )
            group.getChildren().add( new EdtПараметр( н ).загрузить( false ) );
        
        return group;
    }
    
    @Override
    public List<DbАтрибутный.Ключ> компоненты()
    {
        List<DbАтрибутный.Ключ> list = new ArrayList<>( super.компоненты() );
        list.add( 0, new КлючImpl( XmlBrains.XML_PARAMETER, XmlBrains.XMLNS_BRAINS, null ) );
        return list;
    }

    @Override
    public boolean составить( Queue<int[]> path )
    {
        DbАрхив архив = ЭЛЕМЕНТ.архив();
        DbГрафика графика;
        DbИнструкция инструкция;
        int[] a, xy;
        
        // название, тип и позиция заголовка контакта
        ЭЛЕМЕНТ.определить( XmlBrains.XML_NAME, XmlBrains.XMLNS_BRAINS, "Новый контакт" );
        ЭЛЕМЕНТ.определить( XmlBrains.XML_TYPE, XmlBrains.XMLNS_BRAINS, "Тип &inp; или &out;" );
        if( path.isEmpty() ) return false;
        a = path.poll();
        ЭЛЕМЕНТ.определить( SVG_ATTR_TRANSFORM, XMLNS_SVG,  
                String.format( "translate(%d,%d)", a[0], a[1] ) );
        
        // изображение
        графика = (DbГрафика)архив.создатьНовыйЭлемент( SVG_ELEMENT_CIRCLE, XMLNS_SVG );
        графика.определить( SVG_ATTR_R, XMLNS_SVG, 5 );
        //графика.определить( SVG_ATTR_FILL, XMLNS_SVG, "black" );
        ЭЛЕМЕНТ.графики().add( графика );
        
        // название контакта 
        графика = (DbГрафика)архив.создатьНовыйЭлемент( SVG_ELEMENT_TEXT, XMLNS_SVG );
        xy = path.isEmpty() ? new int[]{+5,-5} : отн( path.poll(), a );
        графика.определить( SVG_ATTR_X, XMLNS_SVG, xy[0] );
        графика.определить( SVG_ATTR_Y, XMLNS_SVG, xy[1] );
        графика.определить( SVG_ATTR_FILL, XMLNS_SVG, "black" );
        графика.определить( SVG_ATTR_FONT_SIZE, XMLNS_SVG, 10 );
        инструкция = (DbИнструкция)архив.создатьНовыйЭлемент( Xml.PI_ELEMENT, null );
        инструкция.процессор( "xpath" );
        инструкция.код( "../@name" );
        графика.инструкции().add( инструкция );
        ЭЛЕМЕНТ.графики().add( графика );
        
        return path.isEmpty();
    }
    
}
