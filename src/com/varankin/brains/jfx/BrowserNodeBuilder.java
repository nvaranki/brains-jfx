package com.varankin.brains.jfx;

import com.varankin.brains.artificial.Элемент;
import com.varankin.brains.appl.*;
import com.varankin.brains.artificial.factory.structured.Структурный;
import com.varankin.util.MonitoredSet;
import com.varankin.util.PropertyHolder;
import com.varankin.util.Текст;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.logging.*;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;

/**
 * Контроллер обозревателя объектов.
 * Управляет структурой узлов модели в {@linkplain BrowserView форме просмотра структуры объектов}.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class BrowserNodeBuilder implements BrainsListener
{
    static private final Logger LOGGER = Logger.getLogger( BrowserNodeBuilder.class.getName() );

    private final TreeView<Элемент> модель;
    private final Текст словарь;
    private final ФабрикаНазваний фабрикаНазваний;
    private final BrowserRenderer фабрикаКартинок;

    BrowserNodeBuilder( TreeView<Элемент> модель, Map<Locale.Category,Locale> специфика )
    {
        this.модель = модель;
        this.словарь = Текст.ПАКЕТЫ.словарь( BrowserNodeBuilder.class, специфика );
        this.фабрикаНазваний = new ФабрикаНазваний( специфика );
        this.фабрикаКартинок = new BrowserRenderer();
    }

    TreeItem<Элемент> узел( Элемент элемент )
    {
        Узел узел = new Узел( элемент, 
                     фабрикаНазваний.метка( (Object)элемент ), 
                     фабрикаКартинок.getIcon( элемент ) );
        узел.addChildMonitor( this );
        if( элемент instanceof Структурный )
            for( Элемент э : ((Структурный)элемент).элементы() )
                узел.getChildren().add( узел( э ) );
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
    
    //<editor-fold defaultstate="collapsed" desc="interface BrainsListener">
    
    @Override
    public void brainsElementAdded( BrainsEvent event )
    {
        Platform.runLater( new Appender( event.элемент(), event.группа() ) );
    }
    
    @Override
    public void brainsElementChanged( BrainsEvent event )
    {
        Platform.runLater( new Changer( event.элемент(), event.группа() ) );
    }
    
    @Override
    public void brainsElementRemoved( BrainsEvent event )
    {
        Platform.runLater( new Remover( event.элемент(), event.группа() ) );
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
    
    /**
     * Узел дерева для произвольного объекта.
     */
    static private class Узел extends TreeItem<Элемент>
    {
        private final String метка;
        private PropertyChangeListener монитор;
        
        Узел( Элемент элемент, String метка, Node image )
        {
            super( элемент, image );
            this.метка = метка;
        }
        
        void addChildMonitor( BrowserNodeBuilder строитель )
        {
            Элемент элемент = getValue();
            if( элемент instanceof Структурный )
            {
                Collection<Элемент> элементы = ((Структурный)элемент).элементы();
                if( элементы instanceof PropertyHolder )
                {
                    монитор = new PropertyChangeListenerImpl( this, строитель );
                    ((PropertyHolder)элементы).addPropertyChangeListener( монитор );
                }
            }
        }
        
        void removeChildMonitor()
        {
            Элемент элемент = getValue();
            if( монитор != null )
                if( элемент instanceof Структурный )
                {
                    Collection<Элемент> элементы = ((Структурный)элемент).элементы();
                    if( элементы instanceof PropertyHolder )
                    {
                        ((PropertyHolder)элементы).removePropertyChangeListener( монитор );
                        монитор = null;
                    }
                }
        }
        
        @Override
        public boolean equals( Object o )
        {
            return o instanceof Узел && getValue().equals( ((Узел)o).getValue() );
        }

        @Override
        public int hashCode()
        {
            int hash = 7 ^ getValue().hashCode();
            return hash;
        }

        @Override
        public String toString()
        {
            return метка;
        }
        
    }
    
    private static class PropertyChangeListenerImpl implements PropertyChangeListener
    {
        private final Узел УЗЕЛ;
        private final BrowserNodeBuilder СТРОИТЕЛЬ;

        PropertyChangeListenerImpl( Узел узел, BrowserNodeBuilder строитель )
        {
            УЗЕЛ = узел;
            СТРОИТЕЛЬ = строитель;
        }

        @Override
        public void propertyChange( PropertyChangeEvent evt )
        {
            Элемент элемент;
            
            switch( evt.getPropertyName() )
            {
                case MonitoredSet.PROPERTY_ADDED:
                    элемент = (Элемент)evt.getNewValue();
                    Platform.runLater( new OnPropertyAdded( элемент ) );
                    break;
                
                case MonitoredSet.PROPERTY_REMOVED:
                    элемент = (Элемент)evt.getOldValue();
                    Platform.runLater( new OnPropertyRemoved( элемент ) );
                    break;
                
                default:
                    LOGGER.log( Level.SEVERE, "Unsupported change to node received: {0}", evt.getPropertyName() );
            }            
        }

        private class OnPropertyAdded implements Runnable
        {
            private final Элемент ЭЛЕМЕНТ;

            OnPropertyAdded( Элемент элемент )
            {
                ЭЛЕМЕНТ = элемент;
            }

            @Override
            public void run()
            {
                УЗЕЛ.getChildren().add( СТРОИТЕЛЬ.узел( ЭЛЕМЕНТ ) );
            }
        };

        private class OnPropertyRemoved implements Runnable
        {
            private final Элемент ЭЛЕМЕНТ;

            OnPropertyRemoved( Элемент элемент )
            {
                ЭЛЕМЕНТ = элемент;
            }

            @Override
            public void run()
            {
                TreeItem<Элемент> удаляемый = null;
                for( TreeItem<Элемент> узел : УЗЕЛ.getChildren() )
                    if( ЭЛЕМЕНТ.equals( узел.getValue() ) )
                    {
                        удаляемый = узел;
                        break;
                    }
                if( удаляемый != null )
                {
                    removeTreeItemChildren( удаляемый );
                    УЗЕЛ.getChildren().remove( удаляемый );
                }
            }
        };

    }
    
    private static <T> void removeTreeItemChildren( TreeItem<T> ti )
    {
        if( !ti.isLeaf() )
        {
            for( TreeItem<T> c : ti.getChildren() )
                removeTreeItemChildren( c );
            ti.getChildren().clear();
            if( ti instanceof Узел )
                ((Узел)ti).removeChildMonitor();
        }
    }

    private class Appender implements Runnable
    {
        private final Элемент элемент;
        private final Элемент[] группа;
        
        Appender( Элемент элемент, Элемент... группа )
        {
            this.элемент = элемент;
            this.группа = группа;
        }
        
        @Override
        public void run()
        {
            TreeItem<Элемент> список = список( true, группа );
            if( список != null )
            {
                TreeItem<Элемент> узел = узел( элемент );
                int позиция = позиция( узел.toString(), список );
                список.getChildren().add( позиция, узел );
            }
            else
                LOGGER.log( Level.SEVERE, словарь.текст( "add.orphan", элемент ) );
        }
        
    }
    
    private class Changer implements Runnable
    {
        private final Элемент элемент;
        private final Элемент[] группа;
        
        Changer( Элемент элемент, Элемент... группа )
        {
            this.элемент = элемент;
            this.группа = группа;
        }
        
        @Override
        public void run()
        {
            TreeItem<Элемент> список = список( false, группа );
            TreeItem<Элемент> кандидат = найти( элемент, список );
            if( кандидат != null )
            {
                //TODO список.getChildren().remove( кандидат );
            }
            else
                LOGGER.log( Level.WARNING, словарь.текст( "change.missing", элемент ) );
        }
        
    }
    
    private class Remover implements Runnable
    {
        private final Элемент элемент;
        private final Элемент[] группа;
        
        Remover( Элемент элемент, Элемент... группа )
        {
            this.элемент = элемент;
            this.группа = группа;
        }
        
        @Override
        public void run()
        {
            TreeItem<Элемент> список = список( false, группа );
            TreeItem<Элемент> кандидат = найти( элемент, список );
            if( кандидат != null )
            {
                список.getChildren().remove( кандидат );
            }
            else
                LOGGER.log( Level.WARNING, словарь.текст( "remove.missing", элемент ) );
        }
    }
    
    //</editor-fold>
    
}
