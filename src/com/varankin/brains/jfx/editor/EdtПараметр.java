package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import com.varankin.brains.io.xml.Xml;
import com.varankin.brains.io.xml.XmlBrains;
import com.varankin.brains.jfx.db.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javafx.collections.ObservableList;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlSvg.*;

/**
 *
 * @author Николай
 */
class EdtПараметр extends EdtЭлемент<DbПараметр,FxПараметр>
{
    EdtПараметр( FxПараметр элемент )
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
        list.add( 0, new КлючImpl( XmlBrains.XML_PARAMETER, XmlBrains.XMLNS_BRAINS, null ) );
        list.add( 1, new КлючImpl( XmlBrains.XML_JAVA, XmlBrains.XMLNS_BRAINS, null ) );
        return list;
    }

    @Override
    protected void тексты( List<Node> children )
    {
//        Text text = new SvgTextController( ЭЛЕМЕНТ.getSource(), н.getSource() ).build();
//        //text.setX( 300 );
//        return text;
//        return null; 
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        DbАрхив архив = ЭЛЕМЕНТ.getSource().архив();
        FxТекстовыйБлок блок;
        FxГрафика графика;
        FxИнструкция инструкция;
        int[] a, xy;
        
        // название, значение и позиция заголовка параметра
        ЭЛЕМЕНТ.getSource().определить( XmlBrains.XML_NAME, XmlBrains.XMLNS_BRAINS, "Новый параметр" );
        блок = (FxТекстовыйБлок)FxФабрика.getInstance().создать( архив.создатьНовыйЭлемент( Xml.XML_CDATA, null ) );
        блок.текст().setValue( "Значение" );
        ЭЛЕМЕНТ.тексты().add( блок );
        if( path.isEmpty() ) return false;
        a = path.poll();
        ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_TRANSFORM, XMLNS_SVG,  
                String.format( "translate(%d,%d)", a[0], a[1] ) );
        
        // название параметра в заголовок
        графика = (FxГрафика)FxФабрика.getInstance().создать( архив.создатьНовыйЭлемент( SVG_ELEMENT_TEXT, XMLNS_SVG ) );
        инструкция = (FxИнструкция)FxФабрика.getInstance().создать( архив.создатьНовыйЭлемент( Xml.PI_ELEMENT, null ) );
        инструкция.процессор().setValue( "xpath" );
        инструкция.код().setValue( "../@name" );
        графика.инструкции().add( инструкция );
        ЭЛЕМЕНТ.графики().add( графика );
        
        // значение параметра в заголовок
        графика = (FxГрафика)FxФабрика.getInstance().создать( архив.создатьНовыйЭлемент( SVG_ELEMENT_TEXT, XMLNS_SVG ) );
        xy = path.isEmpty() ? new int[]{+120,0} : отн( path.poll(), a );
        графика.getSource().определить( SVG_ATTR_X, XMLNS_SVG, xy[0] );
        графика.getSource().определить( SVG_ATTR_Y, XMLNS_SVG, xy[1] );
        инструкция = (FxИнструкция)FxФабрика.getInstance().создать( архив.создатьНовыйЭлемент( Xml.PI_ELEMENT, null ) );
        инструкция.процессор().setValue( "xpath" );
        инструкция.код().setValue( "../text()" );
        графика.инструкции().add( инструкция );
        ЭЛЕМЕНТ.графики().add( графика );
        
        return path.isEmpty();
    }
}
