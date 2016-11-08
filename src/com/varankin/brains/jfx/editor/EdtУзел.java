package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbУзел;
import com.varankin.brains.db.КлючImpl;
import com.varankin.brains.io.xml.Xml;
import com.varankin.brains.io.xml.XmlBrains;
import com.varankin.brains.jfx.db.*;
import java.util.Arrays;
import java.util.List;
import javafx.scene.*;
import javafx.scene.text.Text;

/**
 *
 * @author Николай
 */
abstract class EdtУзел<D extends DbУзел, T extends FxУзел<D>> extends EdtАтрибутный<D,T>
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

        for( FxИнструкция н : ЭЛЕМЕНТ.инструкции() )
            children.add( new EdtИнструкция( н ).загрузить( false ) );
        if( !ЭЛЕМЕНТ.тексты().isEmpty() )
        {
            //VBox box = new VBox();
            for( FxТекстовыйБлок н : ЭЛЕМЕНТ.тексты() )
                children/*box.getChildren()*/.add( текст( н ) );
            //children.add( box );
        }
//        for( FxАтрибутный н : ЭЛЕМЕНТ.прочее() )
//            children.add( new EdtАтрибутный<D, T>( н ).загрузить( false ) );
        
        return group;
    }
    
    protected Text текст( FxТекстовыйБлок н )
    {
        return new SvgTextController( ЭЛЕМЕНТ.getSource(), н.getSource() ).build()
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
