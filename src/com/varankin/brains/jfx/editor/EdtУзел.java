package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbИнструкция;
import com.varankin.brains.db.DbТекстовыйБлок;
import com.varankin.brains.db.DbУзел;
import com.varankin.brains.db.КлючImpl;
import com.varankin.brains.io.xml.Xml;
import com.varankin.brains.io.xml.XmlBrains;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.scene.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 * @author Николай
 */
abstract class EdtУзел<T extends DbУзел> extends EdtАтрибутный<T>
{
    EdtУзел( T элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( new Group(), основной );
        List<Node> children = group.getChildren();

        for( DbИнструкция н : ЭЛЕМЕНТ.инструкции() )
            children.add( new EdtИнструкция( н ).загрузить( false ) );
        if( !ЭЛЕМЕНТ.тексты().isEmpty() )
        {
            //VBox box = new VBox();
            for( DbТекстовыйБлок н : ЭЛЕМЕНТ.тексты() )
                children/*box.getChildren()*/.add( текст( н ) );
            //children.add( box );
        }
        for( DbАтрибутный н : ЭЛЕМЕНТ.прочее() )
            children.add( new EdtНеизвестный( н ).загрузить( false ) );
        
        return group;
    }
    
    protected Text текст( DbТекстовыйБлок н )
    {
        return new SvgTextController( ЭЛЕМЕНТ, н ).build()
                        /*new EdtТекстовыйБлок( н ).загрузить( false )*/;
    }
    
    @Override
    public List<DbАтрибутный.Ключ> компоненты()
    {
        return Arrays.asList( 
                new КлючImpl( Xml.XML_CDATA, XmlBrains.XMLNS_BRAINS, null ), 
                new КлючImpl( Xml.PI_ELEMENT, XmlBrains.XMLNS_BRAINS, null ) );
    }
    
}
