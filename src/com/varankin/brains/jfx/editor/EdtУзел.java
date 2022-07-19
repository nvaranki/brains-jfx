package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.type.DbУзел;
import com.varankin.brains.db.xml.ЗонныйКлюч;
import com.varankin.brains.db.xml.type.XmlГрафика;
import com.varankin.brains.db.xml.type.XmlИнструкция;
import com.varankin.brains.db.xml.type.XmlТекстовыйБлок;
import com.varankin.brains.jfx.db.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.*;

import static com.varankin.io.xml.svg.XmlSvg.SVG_ATTR_FILL;
import static com.varankin.io.xml.svg.XmlSvg.SVG_ATTR_FONT_SIZE;
import static com.varankin.io.xml.svg.XmlSvg.SVG_ATTR_X;
import static com.varankin.io.xml.svg.XmlSvg.SVG_ATTR_Y;
import static com.varankin.io.xml.svg.XmlSvg.SVG_ELEMENT_TEXT;

/**
 *
 * @author &copy; 2022 Николай Варанкин
 */
abstract class EdtУзел<D extends DbУзел, T extends FxУзел<D>> extends EdtАтрибутный<D,T>
{
    private ListChangeListener<FxАтрибутный> составитель;
    protected final List<ЗонныйКлюч> компоненты;
    
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
                i -> EdtФабрика.getInstance().apply( i ).загрузить( false ) );

        инструкции( children );
        тексты( children );
//        for( FxАтрибутный н : ЭЛЕМЕНТ.прочее() )
//            children.add( new EdtАтрибутный<D, T>( н ).загрузить( false ) );
        
        return group;
    }
    
    protected List<Node> загрузить( ReadOnlyListProperty<? extends FxАтрибутный<?>> p )
    {
        List<Node> list = p.stream()
            .map( э -> EdtФабрика.getInstance().apply( э ).загрузить( false ) )
            .collect( Collectors.toList() );
        p.addListener( составитель() );
        return list;
    }
    
    protected void инструкции( List<Node> children )
    {
        children.addAll( загрузить( ЭЛЕМЕНТ.инструкции() ) );
        компоненты.add( XmlИнструкция.КЛЮЧ_Э_ИНСТРУКЦИЯ );
    }
    
    protected void тексты( List<Node> children )
    {
        children.addAll( загрузить( ЭЛЕМЕНТ.тексты() ) );
        компоненты.add( XmlТекстовыйБлок.КЛЮЧ_Э_Т_БЛОК );
    }
    
    @Override
    public List<ЗонныйКлюч> компоненты()
    {
        return компоненты;
    }
    
    protected final ListChangeListener<FxАтрибутный> составитель()
    {
        return составитель;
    }
    
    protected FxГрафика графика( String тип )
    {
        return (FxГрафика)ЭЛЕМЕНТ.архив().создатьНовыйЭлемент( XmlГрафика.КЛЮЧ_Э_ГРАФИКА.get( тип ) );
    }
    
    protected FxИнструкция инструкция( String процессор, String код )
    {
        FxИнструкция и = (FxИнструкция)ЭЛЕМЕНТ.архив().создатьНовыйЭлемент( XmlИнструкция.КЛЮЧ_Э_ИНСТРУКЦИЯ );
        и.процессор().setValue( процессор );
        и.код().setValue( код );
        return и;
    }
    
    protected FxТекстовыйБлок блок( String значение )
    {
        FxТекстовыйБлок тб = (FxТекстовыйБлок)ЭЛЕМЕНТ.архив().создатьНовыйЭлемент( XmlТекстовыйБлок.КЛЮЧ_Э_Т_БЛОК );
        тб.текст().setValue( значение );
        return тб;
    }
    
    protected FxГрафика надпись( String ссылка, int[] xy )
    {
        FxГрафика графика = надпись( ссылка );
        графика.атрибут( SVG_ATTR_X ).setValue( xy[0] );
        графика.атрибут( SVG_ATTR_Y ).setValue( xy[1] );
        return графика;
    }
    
    protected FxГрафика надпись( String ссылка )
    {
        FxГрафика графика = графика( SVG_ELEMENT_TEXT );
        графика.атрибут( SVG_ATTR_FILL ).setValue( "black" );
        графика.атрибут( SVG_ATTR_FONT_SIZE ).setValue( 10 );
        графика.инструкции().add( инструкция( "xpath", ссылка ) );
        return графика;
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
