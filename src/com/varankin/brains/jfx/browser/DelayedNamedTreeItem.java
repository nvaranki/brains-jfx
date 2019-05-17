package com.varankin.brains.jfx.browser;

import com.varankin.brains.appl.ФабрикаНазваний;
import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.artificial.Элемент;
import com.varankin.brains.factory.observable.НаблюдаемыеСвойства;
import com.varankin.brains.factory.Составной;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.characteristic.Наблюдаемый;
import com.varankin.characteristic.Наблюдатель;
import com.varankin.characteristic.Свойственный;
import com.varankin.characteristic.Свойство;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;

/**
 * Модификация класса {@link TreeItem} для навигатора по проектам. 
 * Выполняется отложенное построение структуры дерева, только на один уровень,
 * для экономии ресурсов и времени. Метод {@link #toString()} возвращает 
 * предопределенный текст, который применяется для отображения 
 * справа от марки в узле дерева.
 * 
 * @author &copy; 2019 Николай Варанкин
 */
final class DelayedNamedTreeItem extends TreeItem<Элемент>
{
    private final String метка;
    private boolean настроено;

    DelayedNamedTreeItem( Элемент элемент )
    {
        super( элемент, фабрикаКартинок.getIcon( элемент ) );
        метка = фабрикаНазваний.метка( (Object)элемент );
        Consumer<Процесс.Состояние> заливка = с -> фабрикаКартинок.setBgColor( getGraphic(), с );
        наблюдателиСостояния( элемент ).forEach( c -> c.add( new Маляр( заливка ) ) );
        состояния( элемент ).map( i -> i.значение() )
                .filter( i -> i instanceof Процесс.Состояние )
                .map( i -> (Процесс.Состояние)i )
                .forEach( i -> заливка.accept( i ) ); // обозначить текущее значение
        expandedProperty().addListener( (v, o, n) -> 
        { 
            // отложенное построение структуры дерева, только на один уровень
            if( n && !настроено ) Platform.runLater( this::настроитьСостав ); 
        });
    }
    
    void построитьДерево( Элемент элемент )
    {
        DelayedNamedTreeItem дерево = new DelayedNamedTreeItem( элемент );
        List<TreeItem<Элемент>> список = getChildren();
        список.add( дерево.позиция( список, TREE_ITEM_COMPARATOR ), дерево );
    }

    void разобратьДерево( Элемент элемент )
    {
        getChildren().removeAll(getChildren().stream()
                .filter( ti -> ti.getValue().equals( элемент ) )
                .peek( DelayedNamedTreeItem::разобратьДерево )
                .collect( Collectors.toList() ) );
    }
    
    @Override
    public String toString()
    {
        return метка;
    }
    
    /**
     * Переопределяет значение, чтобы узел стал раскрываемым до
     * момента наполнения составляющими.
     * 
     * @return {@code false} для {@linkplain Составной составных} элементов.
     */
    @Override
    public boolean isLeaf() 
    { 
        return настроено ? super.isLeaf() : !( getValue() instanceof Составной ); 
    }

    private void настроитьСостав()
    {
        Элемент элемент = getValue();
        List<TreeItem<Элемент>> список = getChildren();
        
        Consumer<Элемент> составитель = э ->
        {
            DelayedNamedTreeItem дерево = new DelayedNamedTreeItem( э );
            список.add( дерево.позиция( список, TREE_ITEM_COMPARATOR ), дерево );
        };
        Collections.singleton( элемент ).stream()
                .filter( ФИЛЬТР_СОСТАВНОЙ )
                .flatMap( ТИП_СОСТАВНОЙ.andThen( ЭКСТРАКТОР_СОСТАВЛЯЮЩИХ ) )
                .filter( ФИЛЬТР_ЭЛЕМЕНТ )
                .map( ТИП_ЭЛЕМЕНТ )
                .forEach( составитель );
        наблюдателиСостава( элемент ).forEach( c -> c.add( new Составитель( this ) ) );

        настроено = true;
    }

    /**
     * Вычисляет рекомендуемую позицию для вставки узла в список.
     * 
     * @param узел   вставляемый узел.
     * @param список список, куда будет вставлен узел.
     * @param comparator средство сравнения узлов.
     * @return позиция для вставки узла в список.
     */
    private int позиция( List<TreeItem<Элемент>> список, Comparator<TreeItem<Элемент>> comparator )
    {
        int позиция = список.size();
        while( позиция > 0 && comparator.compare( this, список.get( позиция - 1 ) ) < 0 ) позиция--;
        return позиция;
    }
    
    //<editor-fold defaultstate="collapsed" desc="статические методы">
    
    private static void разобратьДерево( TreeItem<Элемент> ti )
    {
        Элемент элемент = ti.getValue();
        наблюдателиСостава( элемент ).forEach( ОПЕРАТОР_УБРАТЬ_СОСТАВИТЕЛЯ );
        наблюдателиСостояния( элемент ).forEach( ОПЕРАТОР_УБРАТЬ_МАЛЯРА );
        Collection<TreeItem<Элемент>> список = ti.getChildren();
        список.stream().forEach( DelayedNamedTreeItem::разобратьДерево );
        список.clear();
    }
    
    private static Stream<Collection<Наблюдатель<?>>> наблюдателиСостава( Элемент элемент )
    {
        return Collections.singleton( элемент ).stream()
                .filter( o -> o instanceof Составной )
                .map( ТИП_СОСТАВНОЙ.andThen( Составной::состав ) )
                .filter( ФИЛЬТР_НАБЛЮДАЕМЫЙ )
                .map( ТИП_НАБЛЮДАЕМЫЙ.andThen( ЭКСТРАКТОР_НАБЛЮДАТЕЛИ ) );
    }
    
    private static Stream<Свойство<?>> состояния( Элемент элемент )
    {
        return (Stream)Collections.singleton( элемент ).stream()
                .filter( ФИЛЬТР_СВОЙСТВЕННЫЙ )
                .map( ТИП_СВОЙСТВЕННЫЙ.andThen( Свойственный::свойства )
                        .andThen( э -> э.свойство( НаблюдаемыеСвойства.СОСТОЯНИЕ ) ) )
                .filter(  э -> э != null );
    }
    private static Stream<Collection<Наблюдатель<?>>> наблюдателиСостояния( Элемент элемент )
    {
        return состояния( элемент )
                .filter( ФИЛЬТР_НАБЛЮДАЕМЫЙ )
                .map( ТИП_НАБЛЮДАЕМЫЙ.andThen( ЭКСТРАКТОР_НАБЛЮДАТЕЛИ ) );
    }
    
    private static final ФабрикаНазваний фабрикаНазваний = new ФабрикаНазваний( JavaFX.getInstance().контекст.специфика );
    private static final BrowserRenderer фабрикаКартинок = new BrowserRenderer();

    private static final Comparator<TreeItem<Элемент>> TREE_ITEM_COMPARATOR = 
            Comparator.comparing( (TreeItem<Элемент> ti) -> ti.getValue() instanceof Составной ? -1 : +1 )
            .thenComparing( ti -> ФабрикаНазваний.индекс( ti.getValue() ) )
            .thenComparing( TreeItem::toString );

    private final static Predicate<Object> ФИЛЬТР_ЭЛЕМЕНТ = o -> o instanceof Элемент;
    private final static Predicate<Object> ФИЛЬТР_СОСТАВНОЙ = o -> o instanceof Составной;
    private final static Predicate<Object> ФИЛЬТР_НАБЛЮДАЕМЫЙ = o -> o instanceof Наблюдаемый;
    private final static Predicate<Object> ФИЛЬТР_СВОЙСТВЕННЫЙ = o -> o instanceof Свойственный;
    
    private final static Function<Object,Элемент>      ТИП_ЭЛЕМЕНТ = o -> (Элемент)o;
    private final static Function<Object,Составной>    ТИП_СОСТАВНОЙ = o -> (Составной)o;
    private final static Function<Object,Наблюдаемый>  ТИП_НАБЛЮДАЕМЫЙ = o -> (Наблюдаемый)o;
    private final static Function<Object,Свойственный> ТИП_СВОЙСТВЕННЫЙ = o -> (Свойственный)o;
    
    private final static Function<Составной,Stream<Collection<?>>> ЭКСТРАКТОР_СОСТАВЛЯЮЩИХ
            = э -> э.состав().stream();
    private final static Function<Наблюдаемый,Collection<Наблюдатель<?>>> ЭКСТРАКТОР_НАБЛЮДАТЕЛИ
            = Наблюдаемый::наблюдатели;

    private final static Consumer<Collection<Наблюдатель<?>>> ОПЕРАТОР_УБРАТЬ_СОСТАВИТЕЛЯ
            = c -> c.removeAll( c.stream().filter( o -> o instanceof Составитель ).collect( Collectors.toList() ) );
    private final static Consumer<Collection<Наблюдатель<?>>> ОПЕРАТОР_УБРАТЬ_МАЛЯРА
            = c -> c.removeAll( c.stream().filter( o -> o instanceof Маляр ).collect( Collectors.toList() ) );
    
    //</editor-fold>
    
}
