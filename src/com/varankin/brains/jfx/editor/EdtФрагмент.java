package com.varankin.brains.jfx.editor;

import com.varankin.brains.artificial.io.xml.XmlBrains;
import static com.varankin.brains.artificial.io.xml.XmlSvg.*;
import static com.varankin.brains.db.neo4j.Architect.*;
import com.varankin.brains.db.Неизвестный;
import com.varankin.brains.db.Фрагмент;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.*;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 *
 * @author Николай
 */
class EdtФрагмент extends EdtАтрибутныйЭлемент<Фрагмент>
{
    static private final Logger LOGGER = Logger.getLogger( EdtФрагмент.class.getName() );

    EdtФрагмент( Фрагмент элемент )
    {
        super( элемент );
    }
    
    Node загрузить()
    {
        Group group = new Group();
        group.setUserData( ЭЛЕМЕНТ );
        
        String ts = toStringValue( ЭЛЕМЕНТ.атрибут( SVG_ATTR_TRANSFORM, XMLNS_SVG, "" ) );
        group.getTransforms().addAll( toTransforms( ts ) );

        group.getChildren().add( createMarker( 3d ) );

        String атрибутName  = toStringValue( ЭЛЕМЕНТ.атрибут( XmlBrains.XML_NAME, XmlBrains.XMLNS_BRAINS, "" ) );
        
        for( Неизвестный н : ЭЛЕМЕНТ.прочее() )
            group.getChildren().add( new EdtНеизвестный( н ).загрузить() );
        
        return group;
    }
    
    protected static List<Transform> toTransforms( String spec )
    {
        List<Transform> transforms = new ArrayList<>();
        List<String> parsed = new ArrayList<>( Arrays.asList( spec.split( "[\\(\\)]" ) ) );
        parsed.removeAll( Arrays.asList( "" ) );
        Iterator<String> it = parsed.iterator(); // op(args)*
        while( it.hasNext() )
        {
            String op = it.next();
            if( it.hasNext() )
                transforms.add( toTransform( op.trim(), it.next().split( "[\\s\\,]" ) ) );
            else
                LOGGER.log( Level.SEVERE, "Unrecognized {0} specification part: {1}()", 
                    new Object[]{ SVG_ATTR_TRANSFORM, op });
        }
        return transforms;
    }

    private static Transform toTransform( String op, String[] args )
    {
        List<String> parsed = new ArrayList<>( Arrays.asList( args ) );
        parsed.removeAll( Arrays.asList( "" ) );
        args = parsed.toArray( new String[parsed.size()] );
        Double[] da = new Double[args.length];
        for( int i = 0; i < args.length; i++ )
            da[i] = Double.valueOf( args[i].trim() );
        Transform t;
        switch( op ) //.toLowerCase()
        {
            case SVG_TR_TRANSLATE:
                switch( da.length )
                {
                    case 1:  
                        t = new Translate( da[0], 0d ); break;
                    case 2:  
                        t = new Translate( da[0], da[1] ); break;
                    default: 
                        t = new Translate( 0d, 0d );
                        LOGGER.log( Level.SEVERE, "Unrecognized {0} specification part: {1}{2}", 
                            new Object[]{ SVG_ATTR_TRANSFORM, op, 
                                Arrays.toString( da ).replace( '[', '(' ).replace( ']', ')' ) });
                }
                break;
                
            default: 
                t = new Translate( 0d, 0d );
                LOGGER.log( Level.SEVERE, "Unrecognized {0} specification part: {1}{2}", 
                    new Object[]{ SVG_ATTR_TRANSFORM, op, 
                                Arrays.toString( da ).replace( '[', '(' ).replace( ']', ')' ) });
            
        }
        return t;
    }
    
}
