package com.varankin.brains.jfx.browser;

import com.varankin.brains.appl.ФабрикаНазваний;
import com.varankin.brains.artificial.*;
import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.factory.observable.НаблюдаемыеСвойства;
import com.varankin.brains.factory.Составной;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.OneToOneListBinding;
import com.varankin.characteristic.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.binding.ListBinding;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;

/**
 * FXML-контроллер навигатора по проектам. 
 * 
 * @author &copy; 2015 Николай Варанкин
 */
public class BrowserController implements Builder<TitledPane>
{
    private static final Logger LOGGER = Logger.getLogger(
            BrowserController.class.getName(),
            BrowserController.class.getPackage().getName() + ".text" );
    private static final String RESOURCE_CSS  = "/fxml/browser/Browser.css";
    private static final String CSS_CLASS = "browser";

    public static final String RESOURCE_FXML  = "/fxml/browser/Browser.fxml";
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getResourceBundle();

    private static final ФабрикаНазваний фабрикаНазваний = new ФабрикаНазваний( JavaFX.getInstance().контекст.специфика );
    private static final BrowserRenderer фабрикаКартинок = new BrowserRenderer();
    private static final Comparator<TreeItem<Элемент>> TREE_ITEM_COMPARATOR = 
            Comparator.comparing( (TreeItem<Элемент> ti) -> ti.getValue() instanceof Составной ? -1 : +1 )
            .thenComparing( (TreeItem<Элемент> ti) -> фабрикаНазваний.индекс( ti.getValue() ) )
            .thenComparing( (TreeItem<Элемент> ti) -> ti.toString() );

    @FXML private BrowserToolBarController toolbarController;
    @FXML private BrowserPopupController popupController;
    @FXML private ToolBar toolbar;
    @FXML private TreeView<Элемент> tree;
    @FXML private ContextMenu popup;

    /**
     * Создает панель навигатора. 
     * Применяется в конфигурации без FXML.
     * 
     * @return панель навигатора. 
     */
    @Override
    public TitledPane build()
    {
        popupController = new BrowserPopupController();
        toolbarController = new BrowserToolBarController();
        
        toolbar = toolbarController.build();
        popup = popupController.build();
        
        tree = new TreeView<>();
        tree.setShowRoot( true );
        tree.setEditable( false );
        tree.setContextMenu( popup );
        HBox.setHgrow( tree, Priority.ALWAYS );

        Pane box = new HBox();
        box.getChildren().addAll( toolbar, tree );
        
        TitledPane pane = new TitledPane( RESOURCE_BUNDLE.getString( "browser.title" ), box );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
        tree.setCellFactory( ( TreeView<Элемент> treeView ) -> new BrowserTreeCell<>( treeView ) );
        tree.setRoot( построитьДерево( JavaFX.getInstance().контекст.мыслитель ) );
        tree.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        
        ListBinding<Элемент> selectionListBinding = new OneToOneListBinding<>( 
                tree.getSelectionModel().getSelectedItems(), (TreeItem<Элемент> i) -> i.getValue() );
        toolbarController.selectionProperty().bind( selectionListBinding );
        popupController.selectionProperty().bind( selectionListBinding );

    }
    
    //<editor-fold defaultstate="collapsed" desc="методы">
    
    private final static Predicate<Object> ФИЛЬТР_ЭЛЕМЕНТ = (Object o) -> o instanceof Элемент;
    private final static Predicate<Object> ФИЛЬТР_СОСТАВНОЙ = (Object o) -> o instanceof Составной;
    private final static Predicate<Object> ФИЛЬТР_НАБЛЮДАЕМЫЙ = (Object o) -> o instanceof Наблюдаемый;
    private final static Predicate<Object> ФИЛЬТР_ПРОЦЕСС = (Object o) -> o instanceof Процесс;
    private final static Predicate<Object> ФИЛЬТР_СВОЙСТВЕННЫЙ = (Object o) -> o instanceof Свойственный;
    private final static Predicate<Object> ФИЛЬТР_СОСТАВИТЕЛЬ = (Object o) -> o instanceof Составитель;
    private final static Predicate<Object> ФИЛЬТР_МАЛЯР = (Object o) -> o instanceof Маляр;
    
    private final static Function<Object,Элемент>      ТИП_ЭЛЕМЕНТ = (Object o) -> (Элемент)o;
    private final static Function<Object,Составной>    ТИП_СОСТАВНОЙ = (Object o) -> (Составной)o;
    private final static Function<Object,Наблюдаемый>  ТИП_НАБЛЮДАЕМЫЙ = (Object o) -> (Наблюдаемый)o;
    private final static Function<Object,Свойственный> ТИП_СВОЙСТВЕННЫЙ = (Object o) -> (Свойственный)o;
    
    private final static Function<Составной,Stream<Collection<?>>> ЭКСТРАКТОР_СОСТАВЛЯЮЩИХ
            = (Составной э) -> э.состав().stream();
    private final static Function<Составной,Collection<?>> ЭКСТРАКТОР_СОСТАВ
            = (Составной э) -> э.состав();
    private final static Function<Наблюдаемый,Collection<Наблюдатель<?>>> ЭКСТРАКТОР_НАБЛЮДАТЕЛИ
            = (Наблюдаемый э) -> э.наблюдатели();
    private final static Function<Свойственный,Map<String,Свойство<?>>> ЭКСТРАКТОР_КАРТЫ_СВОЙСТВ
            = (Свойственный э) -> э.свойства();
    private final static Function<Map<String,Свойство<?>>,Свойство<?>> ЭКСТРАКТОР_СВОЙСТВА_С
            = (Map<String,Свойство<?>> э) -> э.get( НаблюдаемыеСвойства.СОСТОЯНИЕ );
    
    private final static Consumer<TreeItem<Элемент>> ОПЕРАТОР_РАЗОБРАТЬ_УЗЕЛ
            = (TreeItem<Элемент> ti) -> разобратьДерево( ti );
    private final static Consumer<Collection<Наблюдатель<?>>> ОПЕРАТОР_УБРАТЬ_СОСТАВИТЕЛЯ
            = (Collection<Наблюдатель<?>> c) -> c.removeAll(
                    c.stream().filter( ФИЛЬТР_СОСТАВИТЕЛЬ ).collect( Collectors.toList() ) );
    private final static Consumer<Collection<Наблюдатель<?>>> ОПЕРАТОР_УБРАТЬ_МАЛЯРА
            = (Collection<Наблюдатель<?>> c) -> c.removeAll(
                    c.stream().filter( ФИЛЬТР_МАЛЯР ).collect( Collectors.toList() ) );
    
    private static TreeItem<Элемент> построитьДерево( Элемент элемент )
    {
        Node марка = фабрикаКартинок.getIcon( элемент );
        TreeItem<Элемент> treeItem = new NamedTreeItem( элемент, марка );
        
        List<TreeItem<Элемент>> список = treeItem.getChildren();
        Consumer<Элемент> составитель = (Элемент э) ->
        {
            TreeItem<Элемент> дерево = построитьДерево( э );
            список.add( позиция( дерево, список, TREE_ITEM_COMPARATOR ), дерево );
        };
        Collections.singleton( элемент ).stream()
                .filter( ФИЛЬТР_СОСТАВНОЙ )
                .flatMap( ТИП_СОСТАВНОЙ.andThen( ЭКСТРАКТОР_СОСТАВЛЯЮЩИХ ) )
                .filter( ФИЛЬТР_ЭЛЕМЕНТ )
                .map( ТИП_ЭЛЕМЕНТ )
                .forEach( составитель );
        наблюдателиСостава( элемент ).forEach(
                ( Collection<Наблюдатель<?>> c ) -> c.add( new Составитель( список ) ) );
        
        Consumer<Процесс.Состояние> маляр = ( Процесс.Состояние с ) -> фабрикаКартинок.setBgColor( марка, с );
        наблюдателиСостояния( элемент ).forEach(
                ( Collection<Наблюдатель<?>> c ) -> c.add( new Маляр( маляр ) ) );
        
        return treeItem;
    }
    
    private static void разобратьДерево( TreeItem<Элемент> ti )
    {
        Элемент элемент = ti.getValue();
        наблюдателиСостава( элемент ).forEach( ОПЕРАТОР_УБРАТЬ_СОСТАВИТЕЛЯ );
        наблюдателиСостояния( элемент ).forEach( ОПЕРАТОР_УБРАТЬ_МАЛЯРА );
        Collection<TreeItem<Элемент>> список = ti.getChildren();
        список.stream().forEach( ОПЕРАТОР_РАЗОБРАТЬ_УЗЕЛ );
        список.clear();
    }
    
    private static Collection<TreeItem<Элемент>> разобратьДерево( Элемент элемент, 
            Collection<TreeItem<Элемент>> список )
    {
        return список.stream()
                .filter( ( TreeItem<Элемент> ti ) -> ti.getValue().equals( элемент ) )
                .peek( ОПЕРАТОР_РАЗОБРАТЬ_УЗЕЛ )
                .collect( Collectors.toList() );
    }
    
    private static Stream<Collection<Наблюдатель<?>>> наблюдателиСостава( Элемент элемент )
    {
        return Collections.singleton( элемент ).stream()
                .filter( ФИЛЬТР_СОСТАВНОЙ )
                .map( ТИП_СОСТАВНОЙ.andThen( ЭКСТРАКТОР_СОСТАВ ) )
                .filter( ФИЛЬТР_НАБЛЮДАЕМЫЙ )
                .map( ТИП_НАБЛЮДАЕМЫЙ.andThen( ЭКСТРАКТОР_НАБЛЮДАТЕЛИ ) );
    }
    
    private static Stream<Collection<Наблюдатель<?>>> наблюдателиСостояния( Элемент элемент )
    {
        return Collections.singleton( элемент ).stream()
                .filter( ФИЛЬТР_ПРОЦЕСС.and( ФИЛЬТР_СВОЙСТВЕННЫЙ ) )
                .map( ТИП_СВОЙСТВЕННЫЙ.andThen( ЭКСТРАКТОР_КАРТЫ_СВОЙСТВ ).andThen( ЭКСТРАКТОР_СВОЙСТВА_С ) )
                .filter( ФИЛЬТР_НАБЛЮДАЕМЫЙ )
                .map( ТИП_НАБЛЮДАЕМЫЙ.andThen( ЭКСТРАКТОР_НАБЛЮДАТЕЛИ ) );
    }
    
    /**
     * Вычисляет рекомендуемую позицию для вставки узла в список.
     * 
     * @param узел   вставляемый узел.
     * @param список список, куда будет вставлен узел.
     * @param comparator средство сравнения узлов.
     * @return позиция для вставки узла в список.
     */
    private static int позиция( TreeItem<Элемент> узел, 
            List<TreeItem<Элемент>> список, Comparator<TreeItem<Элемент>> comparator )
    {
        int позиция = список.size();
        while( позиция > 0 && comparator.compare( узел, список.get( позиция - 1 ) ) < 0 ) позиция--;
        return позиция;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="классы">
    
    private static final class Маляр implements Наблюдатель<Процесс.Состояние>
    {
        final Consumer<? super Процесс.Состояние> МАЛЯР;
        
        Маляр( Consumer<? super Процесс.Состояние> маляр )
        {
            МАЛЯР = маляр;
        }
        
        @Override
        public void отклик( Изменение<Процесс.Состояние> изменение )
        {
            Platform.runLater( () -> МАЛЯР.accept( изменение.АКТУАЛЬНОЕ ) );
        }
    }
    
    private static final class Составитель implements Наблюдатель<Элемент>
    {
        final Collection<TreeItem<Элемент>> СПИСОК;
        
        Составитель( Collection<TreeItem<Элемент>> список )
        {
            СПИСОК = список;
        }
        
        @Override
        public void отклик( Изменение<Элемент> изменение )
        {
            if( изменение.ПРЕЖНЕЕ != null )
            {
                СПИСОК.removeAll( BrowserController.разобратьДерево( изменение.ПРЕЖНЕЕ, СПИСОК ) );
            }
            if( изменение.АКТУАЛЬНОЕ != null )
            {
                СПИСОК.add( BrowserController.построитьДерево( изменение.АКТУАЛЬНОЕ ) );
            }
        }
        
    }
    
    private static final class NamedTreeItem extends TreeItem<Элемент>
    {
        final String метка;
        
        NamedTreeItem( Элемент элемент, Node node )
        {
            super( элемент, node );
            метка = фабрикаНазваний.метка( (Object)getValue() );
        }
        
        @Override
        public String toString()
        {
            return метка;
        }
    }
    
    //</editor-fold>
    
}
