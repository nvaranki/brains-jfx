package com.varankin.brains.jfx.browser;

import com.varankin.brains.artificial.Элемент;
import com.varankin.brains.appl.*;
import com.varankin.brains.artificial.io.Фабрика;
import java.beans.PropertyChangeListener;
import java.util.*;
import javafx.scene.control.*;
import javafx.util.Callback;

/**
 * Контроллер обозревателя объектов.
 * Управляет структурой узлов модели в {@linkplain BrowserView форме просмотра структуры объектов}.
 *
 * @author &copy; 2013 Николай Варанкин
 */
final class BrowserNodeBuilder
{
    private final TreeView<Элемент> модель;
    private final ФабрикаНазваний фабрикаНазваний;
    private final BrowserRenderer фабрикаКартинок;
    private final Фабрика<BrowserNode,PropertyChangeListener> фабрикаМониторов;

    BrowserNodeBuilder( TreeView<Элемент> модель, Map<Locale.Category,Locale> специфика )
    {
        this.модель = модель;
        this.фабрикаНазваний = new ФабрикаНазваний( специфика );
        this.фабрикаКартинок = new BrowserRenderer();
        this.фабрикаМониторов = new Фабрика<BrowserNode,PropertyChangeListener>() 
        {
            @Override
            public PropertyChangeListener создать( BrowserNode узел )
            {
                return new BrowserMonitor( узел, BrowserNodeBuilder.this );
            }
        };
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
        int позиция = 0;
        for( int max = список.size(); позиция < max; позиция++ )
            if( список.get( позиция ).toString().compareTo( узел.toString() ) > 0 )
                break;
        return позиция;
    }

    Callback<TreeView<Элемент>,TreeCell<Элемент>> фабрика()
    {
        return new Callback<TreeView<Элемент>,TreeCell<Элемент>>()
        {
            @Override
            public TreeCell<Элемент> call( TreeView<Элемент> treeView )
            {
                return new BrowserTreeCell( treeView );
            }
            
        };
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
        }
        
        @Override
        public void updateItem( Элемент элемент, boolean empty ) 
        {
            super.updateItem( элемент, empty );
            if( empty )
            {
                setText( null );
                setGraphic( null );
            }
            else
            {
                TreeItem<Элемент> item = getTreeItem();
                setText( item.toString() );
                setGraphic( item.getGraphic() );
                }
            }
        }
    
}
