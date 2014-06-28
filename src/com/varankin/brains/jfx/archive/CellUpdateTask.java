package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.*;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.ArrayList;
import java.util.Collection;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;

import static com.varankin.brains.jfx.JavaFX.icon;

/**
 *
 * @author Николай
 */
class CellUpdateTask extends Task<Void>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( CellUpdateTask.class );

    private final TreeCell<Атрибутный> cell;
    private final Collection<Атрибутный> indb;
    private final Атрибутный item;
    
    private volatile String название;
    private volatile Node картинка;

    CellUpdateTask( TreeCell<Атрибутный> cell )
    {
        this.cell = cell;
        this.item = cell.getItem();
        this.indb = new ArrayList<>();
    }

    @Override
    protected Void call() throws Exception
    {
        if( item instanceof Элемент )
        {
            Элемент элемент = (Элемент)item;
            try( Транзакция т = элемент.пакет().архив().транзакция() )
            {
                загрузить( элемент );
                т.завершить( true );
            }
        }
        else
        {
            try( Транзакция т = JavaFX.getInstance().контекст.архив.транзакция() )
            {
                загрузить( item );
                т.завершить( true );
            }
        }
        return null;
    }

    @Override
    protected void succeeded()
    {
        TreeItem<Атрибутный> treeItem = cell.getTreeItem();
        if( treeItem != null && item.equals( treeItem.getValue() ) )
        {
            cell.setGraphic( картинка );
            cell.setText( название );
            // use indb to verify current contents
            ObservableList<TreeItem<Атрибутный>> children = treeItem.getChildren();
            for( TreeItem<Атрибутный> i : children )
            {
                indb.remove( i.getValue() );
            }
            for( Атрибутный v : indb )
            {
                children.add( new TreeItem<>( v ) ); //TODO position
            }
        }
    }

    private void загрузить( Архив узел )
    {
        картинка = null;//картинка( узел );
        название = LOGGER.text( "cell.archive" ); //TODO distinguish local and remote архив
        indb.addAll( узел.пакеты() );
        indb.addAll( узел.namespaces() );
    }
    
    private void загрузить( Библиотека узел )
    {
        картинка = icon( "icons16x16/new-library.png" );
        название = узел.название();
        indb.addAll( узел.модули() );
        indb.addAll( узел.поля() );
        indb.addAll( узел.процессоры() );
        indb.addAll( узел.расчеты() );
        indb.addAll( узел.заметки() );
        indb.addAll( узел.прочее() );
    }
    
    private void загрузить( Заметка узел )
    {
        картинка = icon( "icons16x16/properties.png" );
        название = LOGGER.text( "cell.note" );
        indb.addAll( узел.тексты() );
        indb.addAll( узел.прочее() );
    }
    
    private void загрузить( Инструкция узел )
    {
        картинка = null;//картинка( узел );
        название = LOGGER.text( "cell.instruction" );
        indb.addAll( узел.прочее() );
    }
    
    private void загрузить( КлассJava узел )
    {
        картинка = null;//картинка( узел );
        название = узел.название();
        indb.addAll( узел.заметки() );
        indb.addAll( узел.прочее() );
    }
    
    private void загрузить( Контакт узел )
    {
        картинка = icon( "icons16x16/pin.png" );
        название = узел.название();
        indb.addAll( узел.заметки() );
        indb.addAll( узел.прочее() );
    }
    
    private void загрузить( Модуль узел )
    {
        картинка = icon( "icons16x16/module.png" );
        название = узел.название();
        indb.addAll( узел.библиотеки() );
        indb.addAll( узел.фрагменты() );
        indb.addAll( узел.соединения() );
        indb.addAll( узел.сигналы() );
        indb.addAll( узел.процессоры() );
        indb.addAll( узел.заметки() );
        indb.addAll( узел.прочее() );
    }
    
    private void загрузить( Неизвестный узел )
    {
        картинка = null;//картинка( узел );
        название = LOGGER.text( "cell.unknown" );
        indb.addAll( узел.прочее() );
    }
    
    private void загрузить( Пакет узел )
    {
        картинка = icon( "icons16x16/file-xml.png" );
        название = LOGGER.text( "cell.package" );
        indb.addAll( узел.библиотеки() );
        indb.addAll( узел.проекты() );
        indb.addAll( узел.прочее() );
    }
    
    private void загрузить( Поле узел )
    {
        картинка = icon( "icons16x16/field2.png" );
        название = узел.название();
        indb.addAll( узел.соединения() );
        indb.addAll( узел.сигналы() );
        indb.addAll( узел.заметки() );
        indb.addAll( узел.прочее() );
    }
    
    private void загрузить( Проект узел )
    {
        картинка = icon( "icons16x16/new-project.png" );
        название = узел.название();
        indb.addAll( узел.библиотеки() );
        indb.addAll( узел.фрагменты() );
        indb.addAll( узел.сигналы() );
        indb.addAll( узел.процессоры() );
        indb.addAll( узел.заметки() );
        indb.addAll( узел.прочее() );
    }
    
    private void загрузить( Процессор узел )
    {
        картинка = icon( "icons16x16/processor2.png" );
        название = узел.название();
        indb.addAll( узел.классы() );
        indb.addAll( узел.заметки() );
        indb.addAll( узел.прочее() );
    }
    
    private void загрузить( Расчет узел )
    {
        картинка = icon( "icons16x16/start.png" );
        название = узел.название();
        indb.addAll( узел.соединения() );
        indb.addAll( узел.точки() );
        indb.addAll( узел.заметки() );
        indb.addAll( узел.прочее() );
    }
    
    private void загрузить( Сигнал узел )
    {
        картинка = icon( "icons16x16/signal.png" );
        название = узел.название();
        indb.addAll( узел.классы() );
        indb.addAll( узел.заметки() );
        indb.addAll( узел.прочее() );
    }
    
    private void загрузить( Соединение узел )
    {
        картинка = icon( "icons16x16/connector.png" );
        название = узел.название();
        indb.addAll( узел.контакты() );
        indb.addAll( узел.заметки() );
        indb.addAll( узел.прочее() );
    }
    
    private void загрузить( ТекстовыйБлок узел )
    {
        картинка = icon( "icons16x16/file.png" );
        название = LOGGER.text( "cell.text" );
        indb.addAll( узел.прочее() );
    }
    
    private void загрузить( Точка узел )
    {
        картинка = icon( "icons16x16/point.png" );
        название = узел.название();
        indb.addAll( узел.точки() );
        indb.addAll( узел.классы() );
        indb.addAll( узел.заметки() );
        indb.addAll( узел.прочее() );
    }
    
    private void загрузить( Фрагмент узел )
    {
        картинка = icon( "icons16x16/fragment.png" );
        название = узел.название();
        indb.addAll( узел.соединения() );
        indb.addAll( узел.заметки() );
        indb.addAll( узел.прочее() );
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
        indb.addAll( узел.прочее() );
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
            название = узел.название();
            indb.addAll( узел.заметки() );
            indb.addAll( узел.прочее() );
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
        else
        {
            картинка = null;//картинка( узел );
            название = LOGGER.text( "cell.attributive" );
            indb.addAll( узел.прочее() );
        }
    }
    
}
