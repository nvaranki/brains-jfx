package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.type.DbФрагмент;
import com.varankin.brains.db.xml.XLinkShow;
import com.varankin.brains.db.xml.XLink;
import com.varankin.brains.db.xml.МаркированныйЗонныйКлюч;
import com.varankin.brains.jfx.db.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import static com.varankin.brains.db.xml.XmlBrains.*;
import static com.varankin.io.xml.svg.XmlSvg.*;

/**
 *
 * @author Николай
 */
class EdtФрагмент extends EdtЭлемент<DbФрагмент,FxФрагмент>
{
    static private final Logger LOGGER = Logger.getLogger( EdtФрагмент.class.getName() );

    EdtФрагмент( FxФрагмент элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );
        ObservableList<Node> children = group.getChildren();
        children.addAll( основной ? 
                загрузить ( ЭЛЕМЕНТ.соединения(), 0, XML_JOINT ) :
                загрузить_( ЭЛЕМЕНТ.соединения(), 0, XML_JOINT ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.параметры(), 1, XML_PARAMETER ) );

        if( основной )
            children.add( createMarker( 3d ) );

        return group;
    }
    
    protected List<Node> загрузить_( ReadOnlyListProperty<FxСоединение> соединенияСнаружи, 
            int pos, String ключ )
    {
        FxКоммутируемый экземпляр = ЭЛЕМЕНТ.экземпляр().getValue();
        Collection<FxСоединение> соединенияВнутри = экземпляр != null ? 
                экземпляр.соединения().getValue() : Collections.emptyList();
        List<Node> nodes = new ArrayList<>();
        for( FxСоединение снаружи : соединенияСнаружи )
        {
            nodes.add( new EdtСоединение( снаружи ).загрузить( false ) );
            String ref = снаружи.название().getValue();
            for( FxСоединение внутри : соединенияВнутри )
            {
                String id = внутри.название().getValue();
                if( ref.equals( id ) )
                {
                    Node image = new EdtСоединение( внутри ).загрузить( false );
                    image.getTransforms().clear();
                    nodes.add( image );
                    //TODO relocatePins();
                }
            }
        }
        
        компоненты.add( pos, new МаркированныйЗонныйКлюч( ключ, XMLNS_BRAINS, null ) );
        
        return nodes;
    }
    
    protected static List<Transform> toTransforms( String spec )
    {
        if( spec == null ) return Collections.emptyList();
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
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        if( path.isEmpty() ) return false;
        позиция( path.poll() );
        ЭЛЕМЕНТ.вид().setValue( XLinkShow.OTHER );
        ЭЛЕМЕНТ.ссылка().setValue( "Ссылка фрагмента" );
        ЭЛЕМЕНТ.графики().add( название( "Новый фрагмент", "../@xlink:" + XLink.XLINK_TITLE ) );
        return path.isEmpty();
    }

}
