package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import com.varankin.brains.io.xml.Xml;
import com.varankin.brains.io.xml.XmlBrains;
import java.util.Queue;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlSvg.*;
import static com.varankin.brains.jfx.editor.EdtЭлемент.отн;

/**
 *
 * @author Николай
 */
class EdtКлассJava extends EdtЭлемент<DbКлассJava>
{
    EdtКлассJava( DbКлассJava элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        for( DbКонвертер э : ЭЛЕМЕНТ.конвертеры() )
            group.getChildren().add( new EdtКонвертер( э ).загрузить( false ) );
        
        return group;
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        DbАрхив архив = ЭЛЕМЕНТ.архив();
        DbТекстовыйБлок блок;
        DbГрафика графика;
        DbИнструкция инструкция;
        int[] a, xy;
        
        // название и позиция заголовка класса
        ЭЛЕМЕНТ.определить( XmlBrains.XML_NAME, XmlBrains.XMLNS_BRAINS, "Новый класс" );
        if( path.isEmpty() ) return false;
        a = path.poll();
        ЭЛЕМЕНТ.определить( SVG_ATTR_TRANSFORM, XMLNS_SVG,  
                String.format( "translate(%d,%d)", a[0], a[1] ) );
        
//        // заголовок
//        графика = (DbГрафика)архив.создатьНовыйЭлемент( SVG_ELEMENT_TEXT, XMLNS_SVG );
//        //графика.определить( SVG_ATTR_X, XMLNS_SVG, 0 );
//        //графика.определить( SVG_ATTR_Y, XMLNS_SVG, 0 );
//        блок = (DbТекстовыйБлок)архив.создатьНовыйЭлемент( Xml.XML_CDATA, null );
//        блок.текст( "Класс" );
//        графика.тексты().add( блок );
//        ЭЛЕМЕНТ.графики().add( графика );
        
        // название класса в заголовок
        графика = (DbГрафика)архив.создатьНовыйЭлемент( SVG_ELEMENT_TEXT, XMLNS_SVG );
        //xy = path.isEmpty() ? new int[]{+80,0} : отн( path.poll(), a );
        //графика.определить( SVG_ATTR_X, XMLNS_SVG, xy[0] );
        //графика.определить( SVG_ATTR_Y, XMLNS_SVG, xy[1] );
        инструкция = (DbИнструкция)архив.создатьНовыйЭлемент( Xml.PI_ELEMENT, null );
        инструкция.процессор( "xpath" );
        инструкция.код( "../@name" );
        графика.инструкции().add( инструкция );
        ЭЛЕМЕНТ.графики().add( графика );
        
        return path.isEmpty();
    }
    
}
