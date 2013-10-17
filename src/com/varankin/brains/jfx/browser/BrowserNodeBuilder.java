package com.varankin.brains.jfx.browser;

import com.varankin.brains.artificial.Элемент;
import com.varankin.brains.appl.*;
import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.artificial.factory.structured.Структурный;
import com.varankin.brains.artificial.io.Фабрика;
import com.varankin.property.PropertyMonitor;
import com.varankin.util.Текст;
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
    private final Текст словарь;
    private final ФабрикаНазваний фабрикаНазваний;
    private final BrowserRenderer фабрикаКартинок;
    private final Фабрика<BrowserNode,PropertyChangeListener> фабрикаМониторов;

    BrowserNodeBuilder( TreeView<Элемент> модель, Map<Locale.Category,Locale> специфика )
    {
        this.модель = модель;
        this.словарь = Текст.ПАКЕТЫ.словарь( BrowserNodeBuilder.class, специфика );
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
    
    BrowserNode узел( Элемент элемент )
    {
        BrowserNode узел = new BrowserNode( элемент, 
                     фабрикаНазваний.метка( (Object)элемент ), 
                     фабрикаКартинок.getIcon( элемент ) );
        узел.addMonitor( фабрикаМониторов );
        return узел;
    }

    /**
     * Возвращает узел для структуры, заданной последовательностью объектов.
     *
     * @param создать {@code true} для создания отсутствующих узлов.
     * @param путь    обратный путь (лист,...,корень) по дереву отображаемых объектов.
     * @return узел, соответствующий {@code путь[0]}, или {@code null}, если такового нет,
     *              а автоматическое создание узлов не предусмотрено.
     */
    @Deprecated
    TreeItem<Элемент> список( boolean создать, Элемент... путь )
    {
        TreeItem<Элемент> узел = null;

        if( путь.length == 0 )
        {
            TreeItem<Элемент> root = модель.getRoot();
            if( root != null )
                узел = root;
            else if( создать )
                модель.setRoot( узел = узел( Элемент.ВСЕ_МЫСЛИТЕЛИ ) );
        }
        else
        {
            // определить базу - узел владельца элемента
            Элемент[] путь1 = new Элемент[путь.length - 1];
            System.arraycopy( путь, 1, путь1, 0, путь1.length );
            TreeItem<Элемент> база = список( создать, путь1 );
            // найти/содать узел для элемента
            Элемент элемент = путь[0];
            узел = найти( элемент, база );
            if( узел == null && база != null && создать )
            {
                узел = узел( элемент );
                int позиция = позиция( фабрикаНазваний.метка( элемент ), база );
                база.getChildren().add( позиция, узел );
            }            
        }
        
        return узел;
    }

    //<editor-fold defaultstate="collapsed" desc="interface Callback">

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
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="utilities">
    
    /**
     *
     * @param string
     * @param список
     * @return
     */
    static private int позиция( String string, TreeItem<Элемент> список )
    {
        int позиция = 0;
        List<TreeItem<Элемент>> children = список.getChildren();
        for( int max = children.size(); позиция < max; позиция++ )
            if( children.get( позиция ).toString().compareTo( string ) > 0 )
                break;
        return позиция;
    }
    
    static private TreeItem<Элемент> найти(
            Элемент элемент, TreeItem<Элемент> список )
    {
        TreeItem<Элемент> кандидат = null;
        if( список != null )
            for( int i = 0, max = список.getChildren().size(); i < max; i++ )
            {
                TreeItem<Элемент> node = список.getChildren().get( i );
                Элемент тест = node.getValue();
                if( тест == null && элемент == null || тест != null && тест.equals( элемент ) )
                {
                    кандидат = node;
                    break;
                }
            }
        return кандидат;
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="classes">
    
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
    
    //</editor-fold>
    
}
