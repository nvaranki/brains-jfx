package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.type.DbТочка;
import com.varankin.brains.jfx.db.*;
import java.util.Queue;
import javafx.collections.ObservableList;
import javafx.scene.*;

import static com.varankin.brains.db.xml.XmlBrains.*;
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
        ObservableList<Node> children = group.getChildren();
        children.addAll( загрузить( ЭЛЕМЕНТ.точки(), 0, XML_POINT ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.параметры(), 1, XML_PARAMETER ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.классы(), 2, XML_JAVA ) );
        return group;
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        if( path.isEmpty() ) return false;
        int[] a = path.poll();
        
        // тип и позиция
        ЭЛЕМЕНТ.контакт().setValue( "Контакт новой точки" );
        позиция( a );
        
        // название 
        ЭЛЕМЕНТ.графики().add( название( "Новая точка", "../@" + XML_NAME, 
                path.isEmpty() ? new int[]{+7,-7} : отн( path.poll(), a ) ) );
        
        // изображение
        FxГрафика графика = графика( SVG_ELEMENT_CIRCLE );
        графика.атрибут( SVG_ATTR_R ).setValue( 7 );
        графика.атрибут( SVG_ATTR_FILL ).setValue( "none" );
        графика.атрибут( SVG_ATTR_STROKE ).setValue( "black" );
        ЭЛЕМЕНТ.графики().add( графика );
        
        return path.isEmpty();
    }
    
}
