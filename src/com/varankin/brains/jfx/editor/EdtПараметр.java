package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.type.DbПараметр;
import com.varankin.brains.jfx.db.*;

import java.util.List;
import java.util.Queue;
import javafx.collections.ObservableList;
import javafx.scene.*;

import static com.varankin.brains.db.xml.XmlBrains.*;

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
        children.addAll( загрузить( ЭЛЕМЕНТ.классы(), 0, XML_JAVA ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.параметры(), 1, XML_PARAMETER ) );
        
        return group;
    }
    
    @Override
    protected void тексты( List<Node> children )
    {
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        int[] a;
        
        // название, значение и позиция заголовка параметра
        if( path.isEmpty() ) return false;
        позиция( a = path.poll() );
        
        // название параметра
        ЭЛЕМЕНТ.графики().add( название( "Новый параметр", "../@" + XML_NAME ) );
        
        // значение параметра
        ЭЛЕМЕНТ.тексты().add( блок( "Значение" ) );
        ЭЛЕМЕНТ.графики().add( надпись( "../text()", 
            path.isEmpty() ? new int[]{+120,0} : отн( path.poll(), a ) ) );
        
        return path.isEmpty();
    }
}
