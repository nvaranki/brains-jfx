package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import com.varankin.brains.io.xml.Xml;
import com.varankin.brains.io.xml.XmlBrains;
import java.util.Queue;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.io.xml.XmlSvg.SVG_ATTR_X;
import static com.varankin.brains.io.xml.XmlSvg.SVG_ELEMENT_TEXT;
import static com.varankin.brains.io.xml.XmlSvg.XMLNS_SVG;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

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
        
        // название и позиция заголовка класса
        ЭЛЕМЕНТ.определить( XmlBrains.XML_NAME, XmlBrains.XMLNS_BRAINS, "Новый класс" );
        int[] xy = path.poll();
        if( !path.isEmpty() ) return false;
        ЭЛЕМЕНТ.определить( SVG_ATTR_TRANSFORM, XMLNS_SVG,  
                String.format( "translate(%d,%d)", xy[0], xy[1] ) );
        
        // заголовок
        графика = (DbГрафика)архив.создатьНовыйЭлемент( SVG_ELEMENT_TEXT, XMLNS_SVG );
        графика.определить( SVG_ATTR_X, XMLNS_SVG, 0 );
        //графика.определить( SVG_ATTR_Y, XMLNS_SVG, null, 0 );
        блок = (DbТекстовыйБлок)архив.создатьНовыйЭлемент( Xml.XML_CDATA, null );
        блок.текст( "Класс" );
        графика.тексты().add( блок );
        ЭЛЕМЕНТ.графики().add( графика );
        
        // название класса в заголовок
        графика = (DbГрафика)архив.создатьНовыйЭлемент( SVG_ELEMENT_TEXT, XMLNS_SVG );
        графика.определить( SVG_ATTR_X, XMLNS_SVG, 80 );
        //графика.определить( SVG_ATTR_Y, XMLNS_SVG, null, xy[1] );
        инструкция = (DbИнструкция)архив.создатьНовыйЭлемент( Xml.PI_ELEMENT, null );
        инструкция.процессор( "xpath" );
        инструкция.код( "../@name" );
        графика.инструкции().add( инструкция );
        ЭЛЕМЕНТ.графики().add( графика );
        
        return true;
    }
    
}
