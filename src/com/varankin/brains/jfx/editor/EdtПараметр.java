package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import com.varankin.brains.io.xml.Xml;
import com.varankin.brains.io.xml.XmlBrains;
import java.util.Queue;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlSvg.*;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

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
    public Group загрузить( boolean изменяемый )
    {
        Group group = super.загрузить( изменяемый );

        for( DbПараметр н : ЭЛЕМЕНТ.параметры() )
            group.getChildren().add( new EdtПараметр( н ).загрузить( false ) );
        for( DbКлассJava э : ЭЛЕМЕНТ.классы() )
            group.getChildren().add( new EdtКлассJava( э ).загрузить( false ) );
        
        return group;
    }
    
    @Override
    public Group загрузить( boolean изменяемый, Queue<int[]> path )
    {
        int[] xy = path.poll();
        if( !path.isEmpty() ) return null;
        Group group = загрузить( изменяемый );
        DbАрхив архив = ЭЛЕМЕНТ.пакет().архив();
        DbТекстовыйБлок блок;
        DbГрафика графика;
        DbИнструкция инструкция;
        
        // название и позиция параметра
        ЭЛЕМЕНТ.определить( XmlBrains.XML_NAME, XmlBrains.XMLNS_BRAINS, "Новый параметр" );
        ЭЛЕМЕНТ.определить( SVG_ATTR_TRANSFORM, XMLNS_SVG,  
                String.format( "translate(%d,%d)", xy[0], xy[1] ) );
        
        // образец значения параметра
        блок = (DbТекстовыйБлок)архив.создатьНовыйЭлемент( Xml.XML_CDATA, null );
        блок.текст( "Значение" );
        ЭЛЕМЕНТ.тексты().add( блок );
        
        // заголовок
        графика = (DbГрафика)архив.создатьНовыйЭлемент( SVG_ELEMENT_TEXT, XMLNS_SVG );
        графика.определить( SVG_ATTR_X, XMLNS_SVG, 0 );
        //графика.определить( SVG_ATTR_Y, XMLNS_SVG, null, 0 );
        блок = (DbТекстовыйБлок)архив.создатьНовыйЭлемент( Xml.XML_CDATA, null );
        блок.текст( "Параметр" );
        графика.тексты().add( блок );
        ЭЛЕМЕНТ.графики().add( графика );
        
        // название параметра в заголовок
        графика = (DbГрафика)архив.создатьНовыйЭлемент( SVG_ELEMENT_TEXT, XMLNS_SVG );
        графика.определить( SVG_ATTR_X, XMLNS_SVG, 80 );
        //графика.определить( SVG_ATTR_Y, XMLNS_SVG, null, xy[1] );
        инструкция = (DbИнструкция)архив.создатьНовыйЭлемент( Xml.PI_ELEMENT, null );
        инструкция.процессор( "xpath" );
        инструкция.код( "../@name" );
        графика.инструкции().add( инструкция );
        ЭЛЕМЕНТ.графики().add( графика );
        
        // значение параметра в заголовок
        графика = (DbГрафика)архив.создатьНовыйЭлемент( SVG_ELEMENT_TEXT, XMLNS_SVG );
        графика.определить( SVG_ATTR_X, XMLNS_SVG, 200 );
        //графика.определить( SVG_ATTR_Y, XMLNS_SVG, null, xy[1] );
        инструкция = (DbИнструкция)архив.создатьНовыйЭлемент( Xml.PI_ELEMENT, null );
        инструкция.процессор( "xpath" );
        инструкция.код( "../text()" );
        графика.инструкции().add( инструкция );
        ЭЛЕМЕНТ.графики().add( графика );
        
        return group;
    }
}
