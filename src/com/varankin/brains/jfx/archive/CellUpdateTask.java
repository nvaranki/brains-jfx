package com.varankin.brains.jfx.archive;

import com.varankin.brains.appl.ФабрикаНазваний;
import com.varankin.brains.db.*;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
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
 * @author &copy; 2014 Николай Варанкин
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
    
    private volatile String название, подсказка;
    private volatile Node картинка;

    CellUpdateTask( TreeCell<Атрибутный> cell )
    {
        treeCell = cell;
        treeItem = cell.getTreeItem();
        loadGraphic = treeItem.getGraphic() == null;
        потомки = new ArrayList<>();
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
            
            // обновить потомки ячейки по indb, иначе ячейку не раскрыть!
            List<Атрибутный> показать = new LinkedList<>( потомки );
            List<TreeItem<Атрибутный>> показано = treeItem.getChildren();
            List<TreeItem<Атрибутный>> исчезло = new ArrayList<>();
            for( TreeItem<Атрибутный> i : показано )
                if( !показать.remove( i.getValue() ) ) // вычеркнуть, если уже показан
                    исчезло.add( i ); // показан, но уже не существует
            показано.removeAll( исчезло );
            for( Атрибутный v : показать )
                вставить( new TreeItem<>( v ), показано ); // в правильную позицию
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
    
    private void вставить( TreeItem<Атрибутный> item, List<TreeItem<Атрибутный>> items )
    {
        Атрибутный value = item.getValue();
        int b = 0, e = items.size(), pos = ( b + e )/2;
        while( b != e )
        {
            if( b == pos )
                b++;
            else if( CMP.compare( value, items.get( pos ).getValue() ) < 0 )
                e = pos;
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
    }
    
    private void загрузить( Библиотека узел )
    {
        картинка = icon( "icons16x16/new-library.png" );
        название = замена( узел.название(), "cell.library" );
        подсказка = LOGGER.text( "cell.library" );
        потомки.addAll( узел.модули() );
        потомки.addAll( узел.поля() );
        потомки.addAll( узел.процессоры() );
        потомки.addAll( узел.расчеты() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
    }
    
    private void загрузить( Заметка узел )
    {
        картинка = icon( "icons16x16/properties.png" );
        название = LOGGER.text( "cell.note" );
        подсказка = LOGGER.text( "cell.note" );
        потомки.addAll( узел.тексты() );
        потомки.addAll( узел.прочее() );
    }
    
    private void загрузить( Инструкция узел )
    {
        картинка = null;//картинка( узел );
        название = LOGGER.text( "cell.instruction" );
        подсказка = LOGGER.text( "cell.instruction" );
        потомки.addAll( узел.прочее() );
    }
    
    private void загрузить( КлассJava узел )
    {
        картинка = null;//картинка( узел );
        название = замена( узел.название(), "cell.class.java" );
        подсказка = LOGGER.text( "cell.class.java" );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
    }
    
    private void загрузить( Контакт узел )
    {
        картинка = icon( "icons16x16/pin.png" );
        название = замена( узел.название(), "cell.pin" );
        подсказка = LOGGER.text( "cell.pin" );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
    }
    
    private void загрузить( Модуль узел )
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
    }
    
    private void загрузить( Неизвестный узел )
    {
        картинка = null;//картинка( узел );
        название = LOGGER.text( "cell.unknown" );
        подсказка = LOGGER.text( "cell.unknown" );
        потомки.addAll( узел.прочее() );
    }
    
    private void загрузить( Пакет узел )
    {
        картинка = icon( "icons16x16/file-xml.png" );
        название = LOGGER.text( "cell.package" );
        подсказка = LOGGER.text( "cell.package" );
        потомки.addAll( узел.библиотеки() );
        потомки.addAll( узел.проекты() );
        потомки.addAll( узел.прочее() );
    }
    
    private void загрузить( Поле узел )
    {
        картинка = icon( "icons16x16/field2.png" );
        название = замена( узел.название(), "cell.field" );
        подсказка = LOGGER.text( "cell.field" );
        потомки.addAll( узел.соединения() );
        потомки.addAll( узел.сигналы() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
    }
    
    private void загрузить( Проект узел )
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
    }
    
    private void загрузить( Процессор узел )
    {
        картинка = icon( "icons16x16/processor2.png" );
        название = замена( узел.название(), "cell.processor" );
        подсказка = LOGGER.text( "cell.processor" );
        потомки.addAll( узел.классы() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
    }
    
    private void загрузить( Расчет узел )
    {
        картинка = icon( "icons16x16/start.png" );
        название = замена( узел.название(), "cell.compute" );
        подсказка = LOGGER.text( "cell.compute" );
        потомки.addAll( узел.соединения() );
        потомки.addAll( узел.точки() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
    }
    
    private void загрузить( Сигнал узел )
    {
        картинка = icon( "icons16x16/signal.png" );
        название = замена( узел.название(), "cell.signal" );
        подсказка = LOGGER.text( "cell.signal" );
        потомки.addAll( узел.классы() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
    }
    
    private void загрузить( Соединение узел )
    {
        картинка = icon( "icons16x16/connector.png" );
        название = замена( узел.название(), "cell.connector" );
        подсказка = LOGGER.text( "cell.connector" );
        потомки.addAll( узел.контакты() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
    }
    
    private void загрузить( ТекстовыйБлок узел )
    {
        картинка = icon( "icons16x16/file.png" );
        название = LOGGER.text( "cell.text" );
        подсказка = LOGGER.text( "cell.text" );
        потомки.addAll( узел.прочее() );
    }
    
    private void загрузить( Точка узел )
    {
        картинка = icon( "icons16x16/point.png" );
        название = замена( узел.название(), "cell.point" );
        подсказка = LOGGER.text( "cell.point" );
        потомки.addAll( узел.точки() );
        потомки.addAll( узел.классы() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
    }
    
    private void загрузить( Фрагмент узел )
    {
        картинка = icon( "icons16x16/fragment.png" );
        название = замена( узел.название(), "cell.fragment" );
        подсказка = LOGGER.text( "cell.fragment" );
        потомки.addAll( узел.соединения() );
        потомки.addAll( узел.заметки() );
        потомки.addAll( узел.прочее() );
    }
    
    private void загрузить( Мусор узел )
    {
        картинка = icon( "icons16x16/remove.png" );
        название = LOGGER.text( "cell.basket" );
        подсказка = LOGGER.text( "cell.basket" );
        потомки.addAll( узел.мусор() );
        потомки.addAll( узел.прочее() );
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
    }
    
    private void загрузить( Элемент узел )
    {
        if(      узел instanceof Библиотека ) загрузить( (Библиотека)узел );
        else if( узел instanceof КлассJava  ) загрузить( (КлассJava)узел );
        else if( узел instanceof Контакт    ) загрузить( (Контакт)узел );
        else if( узел instanceof Модуль     ) загрузить( (Модуль)узел );
        else if( узел instanceof Поле       ) загрузить( (Поле)узел );
        else if( узел instanceof Проект     ) загрузить( (Проект)узел );
        else if( узел instanceof Процессор  ) загрузить( (Процессор)узел );
        else if( узел instanceof Расчет     ) загрузить( (Расчет)узел );
        else if( узел instanceof Сигнал     ) загрузить( (Сигнал)узел );
        else if( узел instanceof Соединение ) загрузить( (Соединение)узел );
        else if( узел instanceof Точка      ) загрузить( (Точка)узел );
        else if( узел instanceof Фрагмент   ) загрузить( (Фрагмент)узел );
        else
        {
            картинка = null;//картинка( узел );
            название = замена( узел.название(), "cell.element" );
            потомки.addAll( узел.заметки() );
            потомки.addAll( узел.прочее() );
        }
    }
    
    private void загрузить( Атрибутный узел )
    {
        if(      узел instanceof Элемент       ) загрузить( (Элемент)узел );
        else if( узел instanceof Архив         ) загрузить( (Архив)узел );
        else if( узел instanceof Пакет         ) загрузить( (Пакет)узел );
        else if( узел instanceof XmlNameSpace  ) загрузить( (XmlNameSpace)узел );
        else if( узел instanceof Заметка       ) загрузить( (Заметка)узел );
        else if( узел instanceof Инструкция    ) загрузить( (Инструкция)узел );
        else if( узел instanceof ТекстовыйБлок ) загрузить( (ТекстовыйБлок)узел );
        else if( узел instanceof Неизвестный   ) загрузить( (Неизвестный)узел );
        else if( узел instanceof Мусор         ) загрузить( (Мусор)узел );
        else
        {
            картинка = null;//картинка( узел );
            название = LOGGER.text( "cell.attributive" );
            потомки.addAll( узел.прочее() );
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
