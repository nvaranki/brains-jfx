package com.varankin.brains.jfx.archive;

import com.varankin.brains.appl.ФабрикаНазваний;
import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbЗаметка;
import com.varankin.brains.factory.Составной;
import com.varankin.brains.jfx.db.*;
import com.varankin.brains.jfx.db.FxЭлемент;
import com.varankin.characteristic.Изменение;
import com.varankin.characteristic.Наблюдатель;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
 * @author &copy; 2019 Николай Варанкин
 */
abstract class AbstractTreeItem extends TreeItem<FxАтрибутный>
{
    protected static final String RB_BASE_NAME = AbstractTreeItem.class.getPackage().getName() + ".text";
    protected static final Logger LOGGER = Logger.getLogger( AbstractTreeItem.class.getName(), RB_BASE_NAME );
    protected static final ResourceBundle RB = LOGGER.getResourceBundle();
    protected static final String ЗАГРУЗКА = RB.getString( "loading" );

    private final StringProperty textProperty = new SimpleStringProperty();
    private final ObjectProperty<Tooltip> tooltipProperty = new SimpleObjectProperty<>();
    private final Callback<FxАтрибутный,AbstractTreeItem> фабрика;
    protected final ChangeListener<Boolean> пд;
    private Составитель составитель;

    AbstractTreeItem( FxАтрибутный value, Callback<FxАтрибутный,AbstractTreeItem> фабрика )
    {
        super( value );
        this.фабрика = фабрика;
        this.пд = this::onExpandedChanged;
        expandedProperty().addListener( пд ); // PopulateTask удалит пд
        создатьВременныеПотомки(); // временно, до раскрытия узла 
//        if( !( value instanceof FxУзел | value instanceof FxМусор ) )
//            this.setExpanded( true ); // снимет лишние отметки о раскрываемости узла
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
    private void построитьДерево( FxАтрибутный элемент )
    {
        AbstractTreeItem дерево = фабрика.call( элемент );
        List<TreeItem<FxАтрибутный>> список = getChildren();
        список.add( дерево.позиция( список, TREE_ITEM_COMPARATOR ), дерево );
    }
    
    /**
     * Удаляет поддерево с заданным элементом.
     *
     * @param элемент элемент-потомок.
     */
    private void разобратьДерево( FxАтрибутный элемент )
    {
        getChildren().removeAll( getChildren().stream()
                .filter( ti -> ti.getValue().equals( элемент ) )
                .peek( AbstractTreeItem::разобратьПотомковДерева )
                .collect( Collectors.toList() ) );
    }
    
    /**
     * отложенное построение элемента дерева
     */
    private void построитьПотомки( Collection<FxАтрибутный> состав )
    {
        List<TreeItem<FxАтрибутный>> список = getChildren();
        for( FxАтрибутный e : состав )
        {
            AbstractTreeItem дерево = фабрика.call( e );
            список.add( дерево.позиция( список, TREE_ITEM_COMPARATOR ), дерево );
        }
        удалитьВременныеПотомки();
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
    private int позиция( List<TreeItem<FxАтрибутный>> список, Comparator<TreeItem<FxАтрибутный>> comparator )
    {
        int позиция = список.size();
        while( позиция > 0 && comparator.compare( this, список.get( позиция - 1 ) ) < 0 ) позиция--;
        return позиция;
    }

    @Deprecated //TODO временно здесь
    static int сравнитьПоИндексу( DbЗаметка заметка1, DbЗаметка заметка2 )
    {
        Long глубина1 = заметка1.глубина();
        Long глубина2 = заметка2.глубина();
        long значение1 = глубина1 != null ? глубина1 : 0L; //TODO Integer.MAX_VALUE;
        long значение2 = глубина2 != null ? глубина2 : 0L; //TODO Integer.MAX_VALUE;
        long расстояние = значение1 - значение2;
        return 
                расстояние < 0L ? (int)Math.max( Integer.MIN_VALUE, расстояние ) :
                расстояние > 0L ? (int)Math.min( расстояние, Integer.MAX_VALUE ) :
                0;
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
            ObservableValue<String> name;
            Node icon;
            Tooltip tooltip;
        }
        
        @Override
        protected Descr call() throws Exception
        {
            FxАтрибутный<?> элемент = AbstractTreeItem.this.getValue();
            String метка = метка( элемент.тип().getValue() ); // нельзя в StringBinding!
            Descr d = new Descr();
            Property<String> fxp = 
                    элемент instanceof FxЭлемент ? 
                    ((FxЭлемент<?>)элемент).название() : 
                     элемент instanceof FxNameSpace ? 
                    ((FxNameSpace)элемент).название() : 
                     элемент instanceof FxПакет ? 
                    ((FxПакет)элемент).название() : 
                    элемент instanceof FxАрхив ? 
                    ((FxАрхив)элемент).название() : 
                    new SimpleStringProperty( метка );
            d.name = Bindings.createStringBinding( () -> 
            {
                // замена пустого названия на название типа
                String название = fxp.getValue();
                return название == null || название.trim().isEmpty() ? метка : название;
            }, fxp );
            d.icon = марка( элемент );
            d.tooltip = подсказка( элемент );
            return d;
        }
        
        /**
         * Обновляет ячейку по загруженным данным.
         */
        @Override
        protected void succeeded()
        {
            Descr d = getValue();
            AbstractTreeItem.this.textProperty.bind( d.name );
            AbstractTreeItem.this.graphicProperty().setValue( d.icon );
            AbstractTreeItem.this.tooltipProperty.setValue( d.tooltip );
        }
        
        @Override
        protected void failed()
        {
            Throwable exception = getException();
            if( exception != null )
                LOGGER.log( Level.SEVERE, "LookupTask: " + AbstractTreeItem.this.getValue(), exception );
            else
                LOGGER.log( Level.SEVERE, "LookupTask" );
        }
        
    }
    
    /**
     * Сборщик информации о коллекции элемента.
     */
    class PopulateTask extends Task<Collection<FxАтрибутный>>
    {
        final Iterable<ObservableList<? extends FxАтрибутный>> коллекции;

        PopulateTask( Iterable<ObservableList<? extends FxАтрибутный>> коллекции )
        {
            this.коллекции = коллекции;
        }

        /**
         * @return содержимое коллекции.
         * @throws Exception 
         */
        @Override
        protected Collection<FxАтрибутный> call() throws Exception
        {
            List<FxАтрибутный> c = new LinkedList<>();
            коллекции.forEach( к -> c.addAll( к ) );
            return c;
        }

        /**
         * Обновляет ячейку по загруженным данным.
         */
        @Override
        protected void succeeded()
        {
            построитьПотомки( PopulateTask.this.getValue() );
            // установить наблюдатели на коллекции
            if( составитель == null ) составитель = new Составитель( AbstractTreeItem.this );
            коллекции.forEach( к -> к.addListener( составитель ) );
            expandedProperty().removeListener( пд );
        }
        
        @Override
        protected void failed()
        {
            Throwable exception = getException();
            if( exception != null )
                LOGGER.log( Level.SEVERE, "PopulateTask: " + AbstractTreeItem.this.getValue(), exception );
            else
                LOGGER.log( Level.SEVERE, "PopulateTask" );
        }
        
    }

    //</editor-fold>
        
    //<editor-fold defaultstate="collapsed" desc="статические методы, классы и константы">
    
    private static void разобратьПотомковДерева( TreeItem<FxАтрибутный> ti )
    {
        Collection<TreeItem<FxАтрибутный>> список = ti.getChildren();
        // удалить своих наблюдателей с коллекций
        Составитель составитель = ti instanceof AbstractTreeItem ? ((AbstractTreeItem)ti).составитель : null;
        if( составитель != null )
            список.stream()
                .map( i -> (FxАтрибутный<? extends DbАтрибутный>)i.getValue() )
                .flatMap( e -> e.коллекции().values().stream() )
                .forEach( c -> c.removeListener( составитель ) );
        // удалить всех потомков
        список.forEach( AbstractTreeItem::разобратьПотомковДерева );
        список.clear();
    }
    
    /**
     * Контроллер структуры узлов дерева.
     * 
     * @author &copy; 2016 Николай Варанкин
     */
    private static class Составитель implements Наблюдатель<FxАтрибутный>, ListChangeListener<FxАтрибутный>
    {
        private final AbstractTreeItem treeItem;

        Составитель( AbstractTreeItem treeItem )
        {
            this.treeItem = treeItem;
        }

        @Override
        public void отклик( Изменение<FxАтрибутный> изменение )
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

        @Override
        public void onChanged( Change<? extends FxАтрибутный> c )
        {
            while( c.next() )
                if( c.wasPermutated() )
                {
                    for( int i = c.getFrom(); i < c.getTo(); ++i )
                    {
                        //permutate
                    }
                }
                else if( c.wasUpdated() )
                {
                    //update item
                }
                else
                {
                    c.getRemoved().forEach( i -> Platform.runLater( () -> treeItem.разобратьДерево( i ) ) );
                    c.getAddedSubList().forEach( i -> Platform.runLater( () -> treeItem.построитьДерево( i ) ) );
                }
        }

    }

    private static class LoadingTreeItem extends TreeItem<FxАтрибутный>
    {
        @Override
        public String toString()
        {
            return ЗАГРУЗКА;
        }
    }
    
    protected static final Comparator<TreeItem<FxАтрибутный>> TREE_ITEM_COMPARATOR = 
            Comparator.comparing( (TreeItem<FxАтрибутный> ti) -> ti.getValue() instanceof Составной ? -1 : +1 )
            .thenComparing( ti -> ФабрикаНазваний.индекс( ti.getValue() ) )
            .thenComparing( TreeItem::toString );

    //</editor-fold>
    
}
