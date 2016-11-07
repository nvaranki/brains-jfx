package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbСигнал;
import com.varankin.brains.db.КлючImpl;
import com.varankin.brains.io.xml.XmlBrains;
import com.varankin.brains.jfx.db.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.*;

/**
 *
 * @author Николай
 */
class EdtСигнал extends EdtЭлемент<DbСигнал,FxСигнал>
{
    EdtСигнал( FxСигнал элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        for( FxПараметр э : ЭЛЕМЕНТ.параметры() )
            group.getChildren().add( new EdtПараметр( э ).загрузить( false ) );
        for( FxКлассJava э : ЭЛЕМЕНТ.классы() )
            group.getChildren().add( new EdtКлассJava( э ).загрузить( false ) );
        
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

//    @Override
//    public Group загрузить( boolean изменяемый, Queue<int[]> path )
//    {
//        Group group = загрузить( изменяемый );
//        ЭЛЕМЕНТ.определить( XmlSvg.SVG_ATTR_TRANSFORM, XmlSvg.XMLNS_SVG, null, String.format( "translate(%d,%d)", x, y ) );
//        return group;
//    }
    
}
