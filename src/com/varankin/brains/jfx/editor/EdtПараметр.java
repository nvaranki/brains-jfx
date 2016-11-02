package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import com.varankin.brains.io.xml.Xml;
import com.varankin.brains.io.xml.XmlBrains;
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
        
        // название и позиция заголовка параметра
        ЭЛЕМЕНТ.определить( XmlBrains.XML_NAME, XmlBrains.XMLNS_BRAINS, "Новый параметр" );
        int[] xy = path.poll();
        if( !path.isEmpty() ) return false;
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
        
        return true;
    }
}
