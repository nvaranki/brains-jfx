package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАрхив;
import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbГрафика;
import com.varankin.brains.db.DbУзел;
import com.varankin.brains.db.КлючImpl;
import com.varankin.brains.io.xml.Xml;
import com.varankin.brains.io.xml.XmlBrains;
import com.varankin.brains.jfx.db.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.text.Text;

import static com.varankin.brains.io.xml.XmlSvg.SVG_ELEMENT_CIRCLE;
import static com.varankin.brains.io.xml.XmlSvg.XMLNS_SVG;

/**
 *
 * @author Николай
 */
abstract class EdtУзел<D extends DbУзел, T extends FxУзел<D>> extends EdtАтрибутный<D,T>
{
    private ListChangeListener<FxАтрибутный> составитель;
    protected final List<DbАтрибутный.Ключ> компоненты;
    
    EdtУзел( T элемент )
    {
        super( элемент );
        компоненты = new ArrayList<>(30);
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( new Group(), основной );
        List<Node> children = group.getChildren();
        составитель = ( ListChangeListener.Change<? extends FxАтрибутный> c ) 
            -> onListPropertyChanged( c, children, 
                i -> EdtФабрика.getInstance().создать( i ).загрузить( false ) );

        инструкции( children );
        тексты( children );
//        for( FxАтрибутный н : ЭЛЕМЕНТ.прочее() )
//            children.add( new EdtАтрибутный<D, T>( н ).загрузить( false ) );
        
        return group;
    }
    
    protected List<Node> загрузить( ReadOnlyListProperty<? extends FxАтрибутный<?>> p )
    {
        List<Node> list = p.stream()
            .map( э -> EdtФабрика.getInstance().создать( э ).загрузить( false ) )
            .collect( Collectors.toList() );
        p.addListener( составитель() );
        return list;
    }
    
    protected void инструкции( List<Node> children )
    {
        children.addAll( загрузить( ЭЛЕМЕНТ.инструкции() ) );
        компоненты.add( new КлючImpl( Xml.PI_ELEMENT, XmlBrains.XMLNS_BRAINS, null ) );
    }
    
    protected void тексты( List<Node> children )
    {
        children.addAll( загрузить( ЭЛЕМЕНТ.тексты() ) );
        компоненты.add( new КлючImpl( Xml.XML_CDATA, XmlBrains.XMLNS_BRAINS, null ) );
    }
    
    @Override
    public List<DbАтрибутный.Ключ> компоненты()
    {
        return компоненты;
    }
    
    protected final ListChangeListener<FxАтрибутный> составитель()
    {
        return составитель;
    }
    
    protected FxГрафика графика( String тип )
    {
        DbАрхив архив = ЭЛЕМЕНТ.getSource().архив();
        return FxФабрика.getInstance().создать(
                (DbГрафика)архив.создатьНовыйЭлемент( тип, XMLNS_SVG ) );
    }
    
    private static <T> void onListPropertyChanged( 
            ListChangeListener.Change<? extends T> c, 
            List<Node> children, 
            Function<? super T, ? extends Node> builder )
    {
        while( c.next() )
            if( c.wasPermutated() )
                for( int i = c.getFrom(); i < c.getTo(); ++i ) {}
            else if( c.wasUpdated() )
            {
                //update item
            }
            else
            {
                List<? extends T> removed = c.getRemoved();
                List<Node> cToRemove = children.stream()
                        .filter( i -> removed.contains( (T)i.getUserData() ) )
                        .collect( Collectors.toList() );
                List<Node> cToAdd = c.getAddedSubList().stream()
                    .map( builder )
                    .collect( Collectors.toList() );
                Platform.runLater( () -> { children.removeAll( cToRemove ); children.addAll( cToAdd ); } );
            }
    }
   
}
