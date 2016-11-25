package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import com.varankin.brains.jfx.db.*;

import java.util.Queue;
import javafx.collections.ObservableList;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlBrains.*;
import static com.varankin.brains.io.xml.XmlSvg.*;

/**
 *
 * @author Николай
 */
class EdtКонтакт extends EdtЭлемент<DbКонтакт,FxКонтакт>
{
    EdtКонтакт( FxКонтакт элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );
        ObservableList<Node> children = group.getChildren();
        children.addAll( загрузить( ЭЛЕМЕНТ.параметры(), 0, XML_PARAMETER ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.классы(), 1, XML_JAVA ) );
        return group;
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        int[] a;
        
        // тип и позиция контакта
        ЭЛЕМЕНТ.свойства().setValue( (short)0 ); //TODO type selector
        if( path.isEmpty() ) return false;
        позиция( a = path.poll() );
        
        // название контакта 
        ЭЛЕМЕНТ.графики().add( название( "Новый контакт", "../@" + XML_NAME, 
                path.isEmpty() ? new int[]{+5,-5} : отн( path.poll(), a ) ) );
        
        // изображение
        FxГрафика графика = графика( SVG_ELEMENT_CIRCLE );
        графика.атрибут( SVG_ATTR_R ).setValue( 5 );
        //графика.определить( SVG_ATTR_FILL, "black" );
        ЭЛЕМЕНТ.графики().add( графика );
        
        return path.isEmpty();
    }
    
}
