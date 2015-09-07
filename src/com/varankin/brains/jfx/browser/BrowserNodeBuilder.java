package com.varankin.brains.jfx.browser;

import com.varankin.brains.appl.*;
import com.varankin.brains.artificial.io.Фабрика;
import com.varankin.util.LoggerX;
import java.beans.PropertyChangeListener;
import java.util.*;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * Контроллер обозревателя объектов.
 * Управляет структурой узлов модели в {@linkplain BrowserView форме просмотра структуры объектов}.
 *
 * @author &copy; 2015 Николай Варанкин
 */
final class BrowserNodeBuilder<T>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( BrowserNodeBuilder.class );
    
    private final TreeView<T> модель;
    private final ФабрикаНазваний фабрикаНазваний;
    private final BrowserRenderer фабрикаКартинок;
    private final Фабрика<BrowserNode<T>,PropertyChangeListener> фабрикаМониторов;
    private final Callback<TreeView<T>,TreeCell<T>> ФАБРИКА;

    BrowserNodeBuilder( TreeView<T> модель, Map<Locale.Category,Locale> специфика )
    {
        this.модель = модель;
        this.фабрикаНазваний = new ФабрикаНазваний( специфика );
        this.фабрикаКартинок = new BrowserRenderer();
        this.фабрикаМониторов = (BrowserNode<T> узел) -> new BrowserMonitor<>( узел, BrowserNodeBuilder.this );
        ФАБРИКА = ( TreeView<T> treeView ) -> new BrowserTreeCell<>( treeView );
    }
    
    BrowserRenderer фабрикаКартинок()
    {
        return фабрикаКартинок;
    }

    Фабрика<BrowserNode<T>,PropertyChangeListener> фабрикаМониторов()
    {
        return фабрикаМониторов;
    }
    
    /**
     * Создает новый узел дерева для элемента.
     * 
     * @param элемент элемент узла.
     * @return созданный узел.
     */
    BrowserNode<T> узел( T элемент )
    {
        BrowserNode<T> узел = new BrowserNode<>( элемент, 
                     фабрикаНазваний.метка( (Object)элемент ), 
                     фабрикаКартинок.getIcon( элемент ) );
        узел.addMonitor( фабрикаМониторов );
        return узел;
    }
    
    /**
     * Вычисляет рекомендуемую позицию для вставки узла в список.
     * 
     * @param узел   вставляемый узел.
     * @param список список, куда будет вставлен узел.
     * @return позиция для вставки узла.
     */
    int позиция( TreeItem<T> узел, List<TreeItem<T>> список )
    {
        int индексУзла = ФабрикаНазваний.индекс( узел.getValue() );
        int позиция = 0;
        for( int max = список.size(); позиция < max; позиция++ )
        {
            TreeItem<T> тест = список.get( позиция );
            int индексТеста = ФабрикаНазваний.индекс( тест.getValue() );
            if( индексТеста < индексУзла ) continue;
            if( индексТеста > индексУзла ) break;
            if( тест.toString().compareTo( узел.toString() ) > 0 ) break;
        }
        return позиция;
    }

    Callback<TreeView<T>,TreeCell<T>> фабрика()
    {
        return ФАБРИКА;
    }
    
    /**
     * Элемент отображения произвольного узла дерева.
     */
    private static class BrowserTreeCell<T> extends TreeCell<T>
    {
        BrowserTreeCell( TreeView<T> treeView ) 
        {
            setOnDragDetected( (MouseEvent e)-> 
            {
                LOGGER.getLogger().fine( "C: OnDragDetected" ); 
                SnapshotParameters snapParams = new SnapshotParameters();
                snapParams.setFill( Color.TRANSPARENT );
                Dragboard dndb = startDragAndDrop( TransferMode.LINK );
                dndb.setDragView( snapshot( snapParams, null ) );
                dndb.setContent( Collections.singletonMap( DataFormat.PLAIN_TEXT, "data" ) );
                //dragImageView.startFullDrag();
                e.consume(); 
            } );
        }
        
        @Override
        public void updateItem( T элемент, boolean empty ) 
        {
            super.updateItem( элемент, empty );
            if( empty )
            {
                setText( null );
                setGraphic( null );
                setUserData( null );
            }
            else
            {
                TreeItem<T> item = getTreeItem();
                setText( item.toString() );
                setGraphic( item.getGraphic() );
                setUserData( элемент );
            }
        }
        
    }
    
}
