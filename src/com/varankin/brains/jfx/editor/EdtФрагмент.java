package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbСоединение;
import com.varankin.brains.db.DbФрагмент;
import com.varankin.brains.db.Коммутируемый;
import com.varankin.brains.io.xml.XmlBrains;
import com.varankin.brains.jfx.db.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import static com.varankin.brains.io.xml.XmlBrains.BRAINS_ATTR_NAME;
import static com.varankin.brains.io.xml.XmlSvg.*;

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
        children.addAll( загрузить( ЭЛЕМЕНТ.параметры() ) );

        if( основной )
            children.add( createMarker( 3d ) );

        String атрибутName  = ЭЛЕМЕНТ.getSource().атрибут( XmlBrains.XML_NAME, "" );


        Коммутируемый экземпляр = ЭЛЕМЕНТ.getSource().экземпляр();
//        else if( экземпляр instanceof Поле )
//            group.getChildren().add( new EdtПоле( (Поле)экземпляр ).загрузить( false ) );
//        else if( экземпляр instanceof Расчет )
//            group.getChildren().add( new EdtРасчет( (Расчет)экземпляр ).загрузить( false ) );
//        else
//            LOGGER.log( Level.SEVERE, "Unknown instance of fragment: {0}", экземпляр );

        for( FxСоединение снаружи : ЭЛЕМЕНТ.соединения() )
        {
            children.add( new EdtСоединение( снаружи ).загрузить( false ) );
            String ref = снаружи.getSource().атрибут( BRAINS_ATTR_NAME, "" );
            for( DbСоединение внутри : экземпляр.соединения() )
            {
                String id = внутри.атрибут( BRAINS_ATTR_NAME, "" );
                if( ref.equals( id ) )
                {
                    Node image = new EdtСоединение( new FxСоединение( внутри ) ).загрузить( false );
                    image.getTransforms().clear();
                    children.add( image );
                    //TODO relocatePins();
                }
            }
        }

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
