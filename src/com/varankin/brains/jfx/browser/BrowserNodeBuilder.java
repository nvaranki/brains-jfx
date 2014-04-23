package com.varankin.brains.jfx.browser;

import com.varankin.brains.appl.*;
import com.varankin.brains.artificial.io.Фабрика;
import com.varankin.brains.artificial.Элемент;
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
 * @author &copy; 2013 Николай Варанкин
 */
final class BrowserNodeBuilder
{
    private static final LoggerX LOGGER = LoggerX.getLogger( BrowserNodeBuilder.class );
    
    private final TreeView<Элемент> модель;
    private final ФабрикаНазваний фабрикаНазваний;
    private final BrowserRenderer фабрикаКартинок;
    private final Фабрика<BrowserNode,PropertyChangeListener> фабрикаМониторов;
    private final Callback<TreeView<Элемент>,TreeCell<Элемент>> ФАБРИКА;

    BrowserNodeBuilder( TreeView<Элемент> модель, Map<Locale.Category,Locale> специфика )
    {
        this.модель = модель;
        this.фабрикаНазваний = new ФабрикаНазваний( специфика );
        this.фабрикаКартинок = new BrowserRenderer();
        this.фабрикаМониторов = (BrowserNode узел) -> new BrowserMonitor( узел, BrowserNodeBuilder.this );
        ФАБРИКА = ( TreeView<Элемент> treeView ) -> new BrowserTreeCell( treeView );
    }
    
    BrowserRenderer фабрикаКартинок()
    {
        return фабрикаКартинок;
    }

    Фабрика<BrowserNode,PropertyChangeListener> фабрикаМониторов()
    {
        return фабрикаМониторов;
    }
    
    /**
     * Создает новый узел дерева для элемента.
     * 
     * @param элемент элемент узла.
     * @return созданный узел.
     */
    BrowserNode узел( Элемент элемент )
    {
        BrowserNode узел = new BrowserNode( элемент, 
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
    int позиция( TreeItem<Элемент> узел, List<TreeItem<Элемент>> список )
    {
        int индексУзла = фабрикаНазваний.индекс( узел.getValue().getClass() );
        int позиция = 0;
        for( int max = список.size(); позиция < max; позиция++ )
        {
            TreeItem<Элемент> тест = список.get( позиция );
            int индексТеста = фабрикаНазваний.индекс( тест.getValue().getClass() );
            if( индексТеста < индексУзла ) continue;
            if( индексТеста > индексУзла ) break;
            if( тест.toString().compareTo( узел.toString() ) > 0 ) break;
        }
        return позиция;
    }

    Callback<TreeView<Элемент>,TreeCell<Элемент>> фабрика()
    {
        return ФАБРИКА;
    }
    
    /**
     * Элемент отображения произвольного узла дерева.
     */
    private static class BrowserTreeCell extends TreeCell<Элемент>
    {
        private final TreeView<Элемент> treeView;

        BrowserTreeCell( TreeView<Элемент> treeView ) 
        {
            this.treeView = treeView;
        
            this.setOnDragDetected( (MouseEvent e)-> 
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
        public void updateItem( Элемент элемент, boolean empty ) 
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
                TreeItem<Элемент> item = getTreeItem();
                setText( item.toString() );
                setGraphic( item.getGraphic() );
                setUserData( элемент );
            }
        }
        
    }
    
}
