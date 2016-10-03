package com.varankin.brains.jfx.archive;

import com.varankin.brains.appl.ФабрикаНазваний;
import com.varankin.brains.db.*;
import com.varankin.brains.factory.Составной;
import com.varankin.characteristic.Изменение;
import com.varankin.characteristic.Наблюдатель;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.util.Callback;

import static com.varankin.brains.jfx.archive.ArchiveResourceFactory.*;

/**
 * Основа модификации класса {@link TreeItem} для навигатора по проектам. 
 * Выполняется отложенное построение структуры дерева, только на один уровень,
 * для экономии ресурсов и времени. 
 * 
 * @author &copy; 2016 Николай Варанкин
 */
abstract class AbstractTreeItem extends TreeItem<DbАтрибутный>
{
    protected static final String RB_BASE_NAME = AbstractTreeItem.class.getPackage().getName() + ".text";
    protected static final Logger LOGGER = Logger.getLogger( AbstractTreeItem.class.getName(), RB_BASE_NAME );
    protected static final ResourceBundle RB = LOGGER.getResourceBundle();
    protected static final String ЗАГРУЗКА = RB.getString( "loading" );

    private final StringProperty textProperty = new SimpleStringProperty();
    private final ObjectProperty<Tooltip> tooltipProperty = new SimpleObjectProperty<>();
    private final Callback<DbАтрибутный,AbstractTreeItem> фабрика;
    protected final ChangeListener<Boolean> пд;

    AbstractTreeItem( DbАтрибутный value, Callback<DbАтрибутный,AbstractTreeItem> фабрика)
    {
        super( value );
        this.фабрика = фабрика;
        this.пд = this::onExpandedChanged;
        expandedProperty().addListener( пд ); // PopulateTask удалит пд
        создатьВременныеПотомки(); // временно, до раскрытия узла 
        if( !( value instanceof DbУзел ) )
            this.setExpanded( true ); // снимет лишние отметки о раскрываемости узла
    }
    
    //<editor-fold defaultstate="collapsed" desc="методы">
    
    protected abstract void onExpandedChanged( ObservableValue<? extends Boolean> v, Boolean o, Boolean n );

    final StringProperty textProperty()
    {
        return textProperty;
    }
    
    final ObjectProperty<Tooltip> tooltipProperty()
    {
        return tooltipProperty;
    }
    
    /**
     * Строит поддерево из заданного элемента.
     *
     * @param элемент элемент-потомок.
     */
    final void построитьДерево( DbАтрибутный элемент )
    {
        AbstractTreeItem дерево = фабрика.call( элемент );
        List<TreeItem<DbАтрибутный>> список = getChildren();
        список.add( дерево.позиция( список, TREE_ITEM_COMPARATOR ), дерево );
    }
    
    /**
     * Удаляет поддерево с заданным элементом.
     *
     * @param элемент элемент-потомок.
     */
    final void разобратьДерево( DbАтрибутный элемент )
    {
        getChildren().removeAll( getChildren().stream()
                .filter( ti -> ti.getValue().equals( элемент ) )
                .peek( AbstractTreeItem::разобратьПотомковДерева )
                .collect( Collectors.toList() ) );
    }
    
    /**
     * отложенное построение элемента дерева
     */
    private void построитьПотомки( Collection<DbАтрибутный> состав )
    {
        List<TreeItem<DbАтрибутный>> список = getChildren();
        for( DbАтрибутный e : состав )
        {
            AbstractTreeItem дерево = фабрика.call( e );
            список.add( дерево.позиция( список, TREE_ITEM_COMPARATOR ), дерево );
        }
        удалитьВременныеПотомки();
    }
    
    private void установитьНаблюдатели( Iterable<Коллекция<? extends DbАтрибутный>> коллекции )
    {
        for( Коллекция<? extends DbАтрибутный> к : коллекции )
        {
            Наблюдатель составитель = new Составитель( this );
            к.наблюдатели().add( составитель );
        }
    }
    
    private void создатьВременныеПотомки()
    {
        getChildren().add( new LoadingTreeItem() );
    }
    
    protected final void удалитьВременныеПотомки()
    {
        getChildren().removeAll( getChildren().stream()
            .filter( c -> c instanceof LoadingTreeItem ).collect( Collectors.toList() ) );
    }
    
    /**
     * Вычисляет рекомендуемую позицию для вставки узла в список.
     *
     * @param узел   вставляемый узел.
     * @param список список, куда будет вставлен узел.
     * @param comparator средство сравнения узлов.
     * @return позиция для вставки узла в список.
     */
    private int позиция( List<TreeItem<DbАтрибутный>> список, Comparator<TreeItem<DbАтрибутный>> comparator )
    {
        int позиция = список.size();
        while( позиция > 0 && comparator.compare( this, список.get( позиция - 1 ) ) < 0 ) позиция--;
        return позиция;
    }

    @Override
    public final String toString()
    {
        return textProperty().getValue();
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="классы">
    
    /**
     * Сборщик информации об элементе.
     */
    protected class LookupTask extends Task<LookupTask.Descr>
    {

        class Descr
        {
            String name;
            Node icon;
            Tooltip tooltip;
        }
        
        @Override
        protected Descr call() throws Exception
        {
            DbАтрибутный элемент = AbstractTreeItem.this.getValue();
            try( Транзакция т = элемент.транзакция() )
            {
                т.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, элемент );
                Descr d = new Descr();
                d.name = метка( элемент );
                d.icon = марка( элемент );
                d.tooltip = подсказка( элемент );
                т.завершить( true );
                return d;
            }
        }
        
        /**
         * Обновляет ячейку по загруженным данным.
         */
        @Override
        protected void succeeded()
        {
            Descr d = getValue();
            AbstractTreeItem.this.textProperty.setValue( d.name );
            AbstractTreeItem.this.graphicProperty().setValue( d.icon );
            AbstractTreeItem.this.tooltipProperty.setValue( d.tooltip );
        }
        
        @Override
        protected void failed()
        {
            Throwable exception = getException();
            if( exception != null )
                LOGGER.log( Level.SEVERE, "LookupTask", exception );
            else
                LOGGER.log( Level.SEVERE, "LookupTask" );
        }
        
    }
    
    /**
     * Сборщик информации о коллекции элемента.
     */
    class PopulateTask extends Task<Collection<DbАтрибутный>>
    {
        final Iterable<Коллекция<? extends DbАтрибутный>> коллекции;

        PopulateTask( Iterable<Коллекция<? extends DbАтрибутный>> коллекции )
        {
            this.коллекции = коллекции;
        }

        /**
         * @return содержимое коллекции.
         * @throws Exception 
         */
        @Override
        protected Collection<DbАтрибутный> call() throws Exception
        {
            DbАтрибутный элемент = AbstractTreeItem.this.getValue();
            try( Транзакция т = элемент.транзакция() )
            {
                т.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, элемент );
                List<DbАтрибутный> c = new LinkedList<>();
                for( Коллекция<? extends DbАтрибутный> к : коллекции )
                    c.addAll( к );
                т.завершить( true );
                return c;
            }
        }

        /**
         * Обновляет ячейку по загруженным данным.
         */
        @Override
        protected void succeeded()
        {
            построитьПотомки( PopulateTask.this.getValue() );
            установитьНаблюдатели( коллекции );
            expandedProperty().removeListener( пд );
        }
        
        @Override
        protected void failed()
        {
            Throwable exception = getException();
            if( exception != null )
                LOGGER.log( Level.SEVERE, "PopulateTask", exception );
            else
                LOGGER.log( Level.SEVERE, "PopulateTask" );
        }
        
    }

    //</editor-fold>
        
    //<editor-fold defaultstate="collapsed" desc="статические методы, классы и константы">
    
    protected static Map<String,Коллекция> коллекции( DbАтрибутный элемент )
    {
        Function<? super Method,? extends String> nm = m -> m.getName();
        Function<? super Method,? extends Коллекция> xm = new XM( элемент );
        Collector<Method, ?, Map<String, Коллекция>> toMap = 
                Collectors.toMap( nm, xm ); // название метода -> коллекция
        return элемент == null ? Collections.emptyMap() :
            Arrays.stream( элемент.getClass().getMethods() )
                .filter( m -> Коллекция.class.isAssignableFrom( m.getReturnType() ) )
                .collect( toMap );
    }
    
    private static void разобратьПотомковДерева( TreeItem<DbАтрибутный> ti )
    {
        Collection<TreeItem<DbАтрибутный>> список = ti.getChildren();
        // удалить своих наблюдателей с коллекций
        список.stream()
                .map( i -> i.getValue() )
                .flatMap( e -> коллекции( e ).values().stream() )
                .map( c -> (Collection<Наблюдатель<?>>)c.наблюдатели() )
                .forEach( ОПЕРАТОР_УБРАТЬ_СОСТАВИТЕЛЯ );
        // удалить всех потомков
        список.forEach( AbstractTreeItem::разобратьПотомковДерева );
        список.clear();
    }
    
    /**
     * Контроллер структуры узлов дерева.
     * 
     * @author &copy; 2016 Николай Варанкин
     */
    private static class Составитель implements Наблюдатель<DbАтрибутный>
    {
        private final AbstractTreeItem treeItem;

        Составитель( AbstractTreeItem treeItem )
        {
            this.treeItem = treeItem;
        }

        @Override
        public void отклик( Изменение<DbАтрибутный> изменение )
        {
            if( изменение.ПРЕЖНЕЕ != null )
            {
                Platform.runLater( () -> treeItem.разобратьДерево( изменение.ПРЕЖНЕЕ ) );
            }
            if( изменение.АКТУАЛЬНОЕ != null )
            {
                Platform.runLater( () -> treeItem.построитьДерево( изменение.АКТУАЛЬНОЕ ) );
            }
        }

    }

    /**
     * Извлекатель коллекции элемента.
     */
    private static class XM implements Function<Method,Коллекция>
    {
        final DbАтрибутный элемент;
        
        public XM( DbАтрибутный элемент )
        {
            this.элемент = элемент;
        }
        
        @Override
        public Коллекция apply( Method m )
        {
            try
            {
                m.setAccessible( true ); // проблема с унаследованным public final
                return (Коллекция)m.invoke( элемент );
            }
            catch( SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex )
            {
                LOGGER.log( Level.SEVERE, m.getName(), ex );
                return null;
            }
        }
        
    }
    
    private static class LoadingTreeItem extends TreeItem<DbАтрибутный>
    {
        @Override
        public String toString()
        {
            return ЗАГРУЗКА;
        }
    }
    
    protected static final Comparator<TreeItem<DbАтрибутный>> TREE_ITEM_COMPARATOR = 
            Comparator.comparing( (TreeItem<DbАтрибутный> ti) -> ti.getValue() instanceof Составной ? -1 : +1 )
            .thenComparing( ti -> ФабрикаНазваний.индекс( ti.getValue() ) )
            .thenComparing( TreeItem::toString );

    private final static Consumer<Collection<Наблюдатель<?>>> ОПЕРАТОР_УБРАТЬ_СОСТАВИТЕЛЯ
            = c -> c.removeAll( c.stream().filter( o -> o instanceof Составитель ).collect( Collectors.toList() ) );
    
    //</editor-fold>
    
}
