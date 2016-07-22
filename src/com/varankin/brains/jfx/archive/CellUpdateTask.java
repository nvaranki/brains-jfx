package com.varankin.brains.jfx.archive;

import com.varankin.brains.appl.ФабрикаНазваний;
import com.varankin.brains.db.*;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.TitledTreeItem;
import com.varankin.property.PropertyMonitor;
import com.varankin.util.LoggerX;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.logging.*;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

/**
 * Задача обновления ячейки (строки) навигатора по данным архива.
 *
 * @author &copy; 2015 Николай Варанкин
 */
final class CellUpdateTask extends Task<Void>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( CellUpdateTask.class );
    private static final Comparator<Атрибутный> CMP = ( Атрибутный a1, Атрибутный a2 ) -> 
        Integer.valueOf( ФабрикаНазваний.индекс( a2.getClass() ) ).compareTo( 
        Integer.valueOf( ФабрикаНазваний.индекс( a1.getClass() ) ) ); // инверсно

    private final TreeCell<Атрибутный> treeCell;
    private final TreeItem<Атрибутный> treeItem;
    private final Collection<Атрибутный> потомки;
    private final boolean loadGraphic;
    private final PropertyChangeListener наблюдатель;
    private final Collection<PropertyMonitor> мониторы;
    
    private volatile String название, подсказка;
    private volatile Node картинка;

    CellUpdateTask( TreeCell<Атрибутный> cell )
    {
        treeCell = cell;
        treeItem = cell.getTreeItem();
        loadGraphic = treeItem.getGraphic() == null;
        потомки = new ArrayList<>();
        наблюдатель = (МониторКоллекции)cell.getProperties().get( CellАтрибутный.PCL );
        мониторы = (Collection<PropertyMonitor>)cell.getProperties().get( CellАтрибутный.CCPCL );
    }

    /**
     * Загружает информацию по ячейке из архива.
     * 
     * @return {@code null}.
     * @throws Exception 
     */
    @Override
    protected Void call() throws Exception
    {
        Атрибутный item = treeItem.getValue();
        try( Транзакция т = item.транзакция() )
        {
            загрузить( item );
            т.завершить( true );
        }
        for( PropertyMonitor м : мониторы )
        {
            м.listeners().add( наблюдатель );
        }
        return null;
    }

    /**
     * Обновляет ячейку по загруженным данным.
     */
    @Override
    protected void succeeded()
    {
        // пока call() работала, ячейка могла выйти из употребления!
        if( treeItem.equals( treeCell.getTreeItem() ) )
        {
            // обновить отображение
            treeCell.setText( название );
            treeCell.setTooltip( new Tooltip( подсказка ) );
            if( loadGraphic )
                treeCell.setGraphic( картинка ); // иначе отображение разваливается!!! Java v.8u5
            if( loadGraphic )
                treeItem.setGraphic( картинка );
            if( treeItem instanceof TitledTreeItem )
                ((TitledTreeItem)treeItem).titleProperty().set( название );
            
            // обновить потомки ячейки по indb, иначе ячейку не раскрыть!
            List<Атрибутный> показать = new LinkedList<>( потомки );
            List<TreeItem<Атрибутный>> показано = treeItem.getChildren();
            List<TreeItem<Атрибутный>> исчезло = new ArrayList<>();
            for( TreeItem<Атрибутный> i : показано )
                if( !показать.remove( i.getValue() ) ) // вычеркнуть, если уже показан
                    исчезло.add( i ); // показан, но уже не существует
            показано.removeAll( исчезло );
            for( Атрибутный v : показать )
                вставить( new TitledTreeItem<>( v ), показано ); // в правильную позицию
        }
    }
    
    @Override
    protected void failed()
    {
        Throwable exception = getException();
        if( exception != null )
        {
            LOGGER.log( Level.SEVERE, "002005001S", treeItem.getValue(), exception.getMessage() );
            LOGGER.getLogger().log( Level.FINE, "", exception );
        }
    }
    
    static void вставить( TreeItem<Атрибутный> item, List<TreeItem<Атрибутный>> items )
    {
        Атрибутный value = item.getValue();
        int b = 0, e = items.size(), pos = ( b + e )/2;
        while( b != e )
        {
            if( CMP.compare( value, items.get( pos ).getValue() ) < 0 )
                e = pos;
            else if( b == pos )
                b++;
            else
                b = pos;
            pos = ( b + e )/2;
        }
        items.add( pos, item );
        if( LOGGER.getLogger().isLoggable( Level.FINEST ) )
            LOGGER.log( Level.FINEST, "Inserting {1} at branch index {0}", 
                    new Object[]{ pos, value.getClass().getSimpleName() } );
    }

    private void загрузить( Архив узел )
    {
        картинка = null;//картинка( узел );
        название = LOGGER.text( "cell.archive.local" ); //TODO distinguish local and remote архив
        подсказка = LOGGER.text( "cell.archive" );
        потомки.addAll( узел.пакеты() );
        потомки.addAll( узел.namespaces() );
        потомки.addAll( узел.мусор() );
        мониторы.add( узел.пакеты() );
        мониторы.add( узел.namespaces() );
        мониторы.add( узел.мусор() );
    }
    
    private void загрузить( DbБиблиотека узел )
    {
        картинка = icon( "icons16x16/new-library.png" );
        название = замена( узел.название(), "cell.library" );
        подсказка = LOGGER.text( "cell.library" );
        потомки.addAll( узел.модули() );
        потомки.addAll( узел.поля() );
        потомки.addAll( узел.процессоры() );
        потомки.addAll( узел.расчеты() );
        потомки.addAll( узел.классы() );
        потомки.addAll( узел.ленты() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.модули() );
        мониторы.add( узел.поля() );
        мониторы.add( узел.процессоры() );
        мониторы.add( узел.расчеты() );
        мониторы.add( узел.классы() );
        мониторы.add( узел.ленты() );
        мониторы.add( узел.заметки() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( Заметка узел )
    {
        картинка = icon( "icons16x16/properties.png" );
        название = LOGGER.text( "cell.note" );
        подсказка = LOGGER.text( "cell.note" );
        потомки.addAll( узел.тексты() );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.тексты() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( Инструкция узел )
    {
        картинка = null;//картинка( узел );
        название = LOGGER.text( "cell.instruction" );
        подсказка = LOGGER.text( "cell.instruction" );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( КлассJava узел )
    {
        картинка = icon( "icons16x16/JavaIcon.gif" );
        название = замена( узел.название(), "cell.class.java" );
        подсказка = LOGGER.text( "cell.class.java" );
        потомки.addAll( узел.конвертеры() );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.конвертеры() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( DbПараметр узел )
    {
        картинка = null;//картинка( узел );
        название = замена( узел.название(), "cell.parameter.java" );
        подсказка = LOGGER.text( "cell.parameter.java" );
        потомки.addAll( узел.классы() );
        потомки.addAll( узел.коды() );
        потомки.addAll( узел.параметры() );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.классы() );
        мониторы.add( узел.коды() );
        мониторы.add( узел.параметры() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( DbКонтакт узел )
    {
        картинка = icon( "icons16x16/pin.png" );
        название = замена( узел.название(), "cell.pin" );
        подсказка = LOGGER.text( "cell.pin" );
        потомки.addAll( узел.классы() );
        потомки.addAll( узел.параметры());
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.классы() );
        мониторы.add( узел.параметры() );
        мониторы.add( узел.заметки() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( DbМодуль узел )
    {
        картинка = icon( "icons16x16/module.png" );
        название = замена( узел.название(), "cell.module" );
        подсказка = LOGGER.text( "cell.module" );
        потомки.addAll( узел.библиотеки() );
        потомки.addAll( узел.фрагменты() );
        потомки.addAll( узел.соединения() );
        потомки.addAll( узел.сигналы() );
        потомки.addAll( узел.процессоры() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.библиотеки() );
        мониторы.add( узел.фрагменты() );
        мониторы.add( узел.соединения() );
        мониторы.add( узел.сигналы() );
        мониторы.add( узел.процессоры() );
        мониторы.add( узел.заметки() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( Графика узел )
    {
        картинка = null;//картинка( узел );
        название = LOGGER.text( "cell.graphic" );
        подсказка = LOGGER.text( "cell.graphic" );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( Конвертер узел )
    {
        картинка = icon( "icons16x16/JavaIcon.gif" );
        название = LOGGER.text( "cell.converter" );
        подсказка = LOGGER.text( "cell.converter" );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( Неизвестный узел )
    {
        картинка = null;//картинка( узел );
        название = LOGGER.text( "cell.unknown" );
        подсказка = LOGGER.text( "cell.unknown" );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( Пакет узел )
    {
        картинка = icon( "icons16x16/file-xml.png" );
        название = LOGGER.text( "cell.package" );
        подсказка = LOGGER.text( "cell.package" );
        потомки.addAll( узел.библиотеки() );
        потомки.addAll( узел.проекты() );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.библиотеки() );
        мониторы.add( узел.проекты() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( DbПоле узел )
    {
        картинка = icon( "icons16x16/field2.png" );
        название = замена( узел.название(), "cell.field" );
        подсказка = LOGGER.text( "cell.field" );
        потомки.addAll( узел.соединения() );
        потомки.addAll( узел.сигналы() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.соединения() );
        мониторы.add( узел.сигналы() );
        мониторы.add( узел.заметки() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( DbЛента узел )
    {
        картинка = null;//TODO icon( "icons16x16/field2.png" );
        название = замена( узел.название(), "cell.timeline" );
        подсказка = LOGGER.text( "cell.timeline" );
        потомки.addAll( узел.соединения() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.соединения() );
        мониторы.add( узел.заметки() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( DbПроект узел )
    {
        картинка = icon( "icons16x16/new-project.png" );
        название = замена( узел.название(), "cell.project" );
        подсказка = LOGGER.text( "cell.project" );
        потомки.addAll( узел.библиотеки() );
        потомки.addAll( узел.фрагменты() );
        потомки.addAll( узел.сигналы() );
        потомки.addAll( узел.процессоры() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.библиотеки() );
        мониторы.add( узел.фрагменты() );
        мониторы.add( узел.сигналы() );
        мониторы.add( узел.процессоры() );
        мониторы.add( узел.заметки() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( DbПроцессор узел )
    {
        картинка = icon( "icons16x16/processor2.png" );
        название = замена( узел.название(), "cell.processor" );
        подсказка = LOGGER.text( "cell.processor" );
        потомки.addAll( узел.классы() );
        потомки.addAll( узел.параметры() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.классы() );
        мониторы.add( узел.параметры() );
        мониторы.add( узел.заметки() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( DbРасчет узел )
    {
        картинка = icon( "icons16x16/function.png" );
        название = замена( узел.название(), "cell.compute" );
        подсказка = LOGGER.text( "cell.compute" );
        потомки.addAll( узел.соединения() );
        потомки.addAll( узел.точки() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.соединения() );
        мониторы.add( узел.точки() );
        мониторы.add( узел.заметки() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( DbСигнал узел )
    {
        картинка = icon( "icons16x16/signal.png" );
        название = замена( узел.название(), "cell.signal" );
        подсказка = LOGGER.text( "cell.signal" );
        потомки.addAll( узел.классы() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.классы() );
        мониторы.add( узел.заметки() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( DbСоединение узел )
    {
        картинка = icon( "icons16x16/connector.png" );
        название = замена( узел.название(), "cell.connector" );
        подсказка = LOGGER.text( "cell.connector" );
        потомки.addAll( узел.контакты() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.контакты() );
        мониторы.add( узел.заметки() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( ТекстовыйБлок узел )
    {
        картинка = icon( "icons16x16/file.png" );
        название = LOGGER.text( "cell.text" );
        подсказка = LOGGER.text( "cell.text" );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( DbТочка узел )
    {
        картинка = icon( "icons16x16/point.png" );
        название = замена( узел.название(), "cell.point" );
        подсказка = LOGGER.text( "cell.point" );
        потомки.addAll( узел.точки() );
        потомки.addAll( узел.классы() );
        потомки.addAll( узел.параметры() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.точки() );
        мониторы.add( узел.классы() );
        мониторы.add( узел.параметры() );
        мониторы.add( узел.заметки() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( DbФрагмент узел )
    {
        картинка = icon( "icons16x16/fragment.png" );
        название = замена( узел.название(), "cell.fragment" );
        подсказка = LOGGER.text( "cell.fragment" );
        потомки.addAll( узел.соединения() );
        потомки.addAll( узел.параметры() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.соединения() );
        мониторы.add( узел.параметры() );
        мониторы.add( узел.заметки() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( Мусор узел )
    {
        картинка = icon( "icons16x16/remove.png" );
        название = LOGGER.text( "cell.basket" );
        подсказка = LOGGER.text( "cell.basket" );
        потомки.addAll( узел.мусор() );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.мусор() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( XmlNameSpace узел )
    {
        картинка = null;//icon( "icons16x16/load.png" );
        название = узел.название();
        if( название == null || название.trim().isEmpty() )
        {
            название = узел.uri();
            if( название == null || название.trim().isEmpty() )
                название = LOGGER.text( "cell.namespace" );
            else if( название.length() > 20 )
                название = "...".concat( название.substring( название.length() - 20 + 3 ) );
        }
        подсказка = LOGGER.text( "cell.namespace" );
        потомки.addAll( узел.прочее() );
        мониторы.add( узел.прочее() );
    }
    
    private void загрузить( DbЭлемент узел )
    {
        if(      узел instanceof DbБиблиотека ) загрузить( (DbБиблиотека)узел );
        else if( узел instanceof DbКонтакт    ) загрузить( (DbКонтакт)узел );
        else if( узел instanceof DbЛента      ) загрузить( (DbЛента)узел );
        else if( узел instanceof DbМодуль     ) загрузить( (DbМодуль)узел );
        else if( узел instanceof DbПоле       ) загрузить( (DbПоле)узел );
        else if( узел instanceof DbПараметр   ) загрузить( (DbПараметр)узел );
        else if( узел instanceof DbПроект     ) загрузить( (DbПроект)узел );
        else if( узел instanceof DbПроцессор  ) загрузить( (DbПроцессор)узел );
        else if( узел instanceof DbРасчет     ) загрузить( (DbРасчет)узел );
        else if( узел instanceof DbСигнал     ) загрузить( (DbСигнал)узел );
        else if( узел instanceof DbСоединение ) загрузить( (DbСоединение)узел );
        else if( узел instanceof DbТочка      ) загрузить( (DbТочка)узел );
        else if( узел instanceof DbФрагмент   ) загрузить( (DbФрагмент)узел );
        else
        {
            картинка = null;//картинка( узел );
            название = замена( узел.название(), "cell.element" );
            потомки.addAll( узел.заметки() );
            потомки.addAll( узел.прочее() );
            мониторы.add( узел.заметки() );
            мониторы.add( узел.прочее() );
        }
    }
    
    private void загрузить( Атрибутный узел )
    {
        if(      узел instanceof DbЭлемент     ) загрузить( (DbЭлемент)узел );
        else if( узел instanceof DbПараметр    ) загрузить( (DbПараметр)узел );
        else if( узел instanceof Архив         ) загрузить( (Архив)узел );
        else if( узел instanceof КлассJava     ) загрузить( (КлассJava)узел );
        else if( узел instanceof Пакет         ) загрузить( (Пакет)узел );
        else if( узел instanceof XmlNameSpace  ) загрузить( (XmlNameSpace)узел );
        else if( узел instanceof Заметка       ) загрузить( (Заметка)узел );
        else if( узел instanceof Инструкция    ) загрузить( (Инструкция)узел );
        else if( узел instanceof ТекстовыйБлок ) загрузить( (ТекстовыйБлок)узел );
        else if( узел instanceof Графика       ) загрузить( (Графика)узел );
        else if( узел instanceof Конвертер     ) загрузить( (Конвертер)узел );
        else if( узел instanceof Неизвестный   ) загрузить( (Неизвестный)узел );
        else if( узел instanceof Мусор         ) загрузить( (Мусор)узел );
        else
        {
            картинка = null;//картинка( узел );
            название = LOGGER.text( "cell.attributive" );
            потомки.addAll( узел.прочее() );
            мониторы.add( узел.прочее() );
        }
    }
    
    private String замена( String оригинал, String ключ )
    {
        return оригинал == null || оригинал.trim().isEmpty() ?
            LOGGER.text( ключ ) : оригинал;
    }
    
    private ImageView icon( String path )
    {
        // вызывается очень часто, а делается долго
        return loadGraphic ? JavaFX.icon( path ) : null;
    }
    
}
