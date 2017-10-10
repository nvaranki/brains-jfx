package com.varankin.brains.jfx.archive;

import com.varankin.biz.action.Действие;
import com.varankin.brains.jfx.history.LocalNeo4jProvider;
import com.varankin.brains.jfx.history.RemoteNeo4jProvider;
import com.varankin.brains.appl.ДействияПоПорядку;
import com.varankin.brains.appl.ЗагрузитьАрхивныйПроект;
import com.varankin.brains.appl.ЭкспортироватьSvg;
import com.varankin.brains.appl.ЭкспортироватьXml;
import com.varankin.brains.artificial.io.Фабрика;
import com.varankin.brains.db.*;
import com.varankin.brains.io.xml.Xml;
import com.varankin.brains.io.xml.XmlBrains;
import com.varankin.brains.jfx.*;
import com.varankin.brains.jfx.db.*;
import com.varankin.brains.jfx.editor.EditorController;
import com.varankin.io.container.Provider;
import com.varankin.brains.jfx.history.LocalInputStreamProvider;
import com.varankin.brains.jfx.history.RemoteInputStreamProvider;
import com.varankin.brains.jfx.history.SerializableProvider;
import com.varankin.brains.jfx.selector.LocalFileSelector;
import com.varankin.brains.jfx.selector.UrlSelector;
import com.varankin.util.LoggerX;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.*;
import java.util.stream.Collectors;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ListExpression;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.web.WebView;
import javafx.stage.*;

import static javafx.beans.binding.Bindings.createBooleanBinding;

/**
 * Действия над элементами архива.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
final class ActionProcessor //TODO RT-37820
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ActionProcessor.class );
    private static final Действие<DbПроект> действиеЗагрузитьПроект 
        = new ЗагрузитьАрхивныйПроект();

    //<editor-fold defaultstate="collapsed" desc="pXxx">
    
    // предикаты для блокировки строк в контекстном меню
    private static final Predicate<FxАтрибутный> pClose = i -> i instanceof FxАрхив;
    private static final Predicate<FxАтрибутный> pEdit = i -> i instanceof FxЭлемент;
    private static final Predicate<FxАтрибутный> pExportPic = i -> i instanceof FxЭлемент;
    private static final Predicate<FxАтрибутный> pExportXml = i -> i instanceof FxЭлемент;
    private static final Predicate<FxАтрибутный> pImport = i -> i instanceof FxАрхив;
    private static final Predicate<FxАтрибутный> pLoad = i -> i instanceof FxПроект;
    private static final Predicate<FxАтрибутный> pMultiply = i -> i instanceof FxАтрибутный && !( i instanceof FxАрхив );
    private static final Predicate<FxАтрибутный> pNewБиблиотека = i -> i instanceof FxПакет || i instanceof FxПроект || i instanceof FxМодуль;
    private static final Predicate<FxАтрибутный> pNewЗаметка = i -> i instanceof FxЭлемент;
    private static final Predicate<FxАтрибутный> pNewИнструкция = i -> i instanceof FxГрафика;
    private static final Predicate<FxАтрибутный> pNewКлассJava = i -> i instanceof FxБиблиотека || i instanceof FxПроцессор || i instanceof FxПараметр
            || i instanceof FxКонтакт || i instanceof FxСигнал || i instanceof FxТочка;
    private static final Predicate<FxАтрибутный> pNewКонтакт = i -> i instanceof FxСоединение;
    private static final Predicate<FxАтрибутный> pNewЛента = i -> i instanceof FxБиблиотека;
    private static final Predicate<FxАтрибутный> pNewМодуль = i -> i instanceof FxБиблиотека;
    private static final Predicate<FxАтрибутный> pNewПакет = i -> i instanceof FxАрхив;
    private static final Predicate<FxАтрибутный> pNewПараметр = i -> i instanceof FxПроцессор || i instanceof FxФрагмент || i instanceof FxКонтакт
            || i instanceof FxСигнал || i instanceof FxПараметр || i instanceof FxТочка;
    private static final Predicate<FxАтрибутный> pNewПоле = i -> i instanceof FxБиблиотека;
    private static final Predicate<FxАтрибутный> pNewПроект = i -> i instanceof FxПакет;
    private static final Predicate<FxАтрибутный> pNewПроцессор = i -> i instanceof FxБиблиотека || i instanceof FxПроект || i instanceof FxМодуль;
    private static final Predicate<FxАтрибутный> pNewРасчет = i -> i instanceof FxБиблиотека;
    private static final Predicate<FxАтрибутный> pNewСенсор = i -> i instanceof FxПоле;
    private static final Predicate<FxАтрибутный> pNewСигнал = i -> i instanceof FxПроект || i instanceof FxМодуль;
    private static final Predicate<FxАтрибутный> pNewСоединение = i -> i instanceof FxФрагмент || i instanceof FxПоле || i instanceof FxМодуль
            || i instanceof FxРасчет || i instanceof FxЛента;
    private static final Predicate<FxАтрибутный> pNewТекстовыйБлок = i -> i instanceof FxЗаметка || i instanceof FxГрафика || i instanceof FxПараметр;
    private static final Predicate<FxАтрибутный> pNewТочка = i -> i instanceof FxРасчет || i instanceof FxТочка;
    private static final Predicate<FxАтрибутный> pNewФрагмент = i -> i instanceof FxПроект || i instanceof FxМодуль;
    private static final Predicate<FxАтрибутный> pNewXmlNameSpace = i -> i instanceof FxАрхив;
    private static final Predicate<FxАтрибутный> pPreview = i -> i instanceof FxЭлемент;
    private static final Predicate<FxАтрибутный> pProperties = i -> i instanceof FxАтрибутный;
    private static final Predicate<FxАтрибутный> pRemove = i -> i instanceof FxАтрибутный && !( i instanceof FxАрхив );
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="фабрикаXxxYyy">
    
    // фабрики создания дочерних элементов
    private static final Фабрика<FxАрхив,FxПакет> фабрикаАрхивПакет
            = архив -> (FxПакет)архив.создатьНовыйЭлемент( XmlBrains.XML_BRAINS, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxАрхив,FxNameSpace> фабрикаАрхивNameSpace
            = архив -> архив.определитьПространствоИмен( "протокол://сервер/путь", "префикс" );
    private static final Фабрика<FxМодуль,FxБиблиотека> фабрикаМодульБиблиотека
            = e -> (FxБиблиотека)e.архив().создатьНовыйЭлемент( XmlBrains.XML_LIBRARY, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxПроект,FxБиблиотека> фабрикаПроектБиблиотека
            = e -> (FxБиблиотека)e.архив().создатьНовыйЭлемент( XmlBrains.XML_LIBRARY, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxЭлемент,FxЗаметка> фабрикаЭлементЗаметка
            = e -> (FxЗаметка)e.архив().создатьНовыйЭлемент( XmlBrains.XML_NOTE, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxКонтакт,FxКлассJava> фабрикаКонтактКлассJava
            = e -> (FxКлассJava)e.архив().создатьНовыйЭлемент( XmlBrains.XML_JAVA, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxБиблиотека,FxКлассJava> фабрикаБиблиотекаКлассJava
            = e -> (FxКлассJava)e.архив().создатьНовыйЭлемент( XmlBrains.XML_JAVA, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxПараметр,FxКлассJava> фабрикаПараметрКлассJava
            = e -> (FxКлассJava)e.архив().создатьНовыйЭлемент( XmlBrains.XML_JAVA, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxПроцессор,FxКлассJava> фабрикаПроцессорКлассJava
            = e -> (FxКлассJava)e.архив().создатьНовыйЭлемент( XmlBrains.XML_JAVA, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxСигнал,FxКлассJava> фабрикаСигналКлассJava
            = e -> (FxКлассJava)e.архив().создатьНовыйЭлемент( XmlBrains.XML_JAVA, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxТочка,FxКлассJava> фабрикаТочкаКлассJava
            = e -> (FxКлассJava)e.архив().создатьНовыйЭлемент( XmlBrains.XML_JAVA, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxСоединение,FxКонтакт> фабрикаСоединениеКонтакт
            = e -> (FxКонтакт)e.архив().создатьНовыйЭлемент( XmlBrains.XML_PIN, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxБиблиотека,FxЛента> фабрикаБиблиотекаЛента
            = e -> (FxЛента)e.архив().создатьНовыйЭлемент( XmlBrains.XML_TIMELINE, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxБиблиотека,FxМодуль> фабрикаБиблиотекаМодуль
            = e -> (FxМодуль)e.архив().создатьНовыйЭлемент( XmlBrains.XML_MODULE, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxБиблиотека,FxРасчет> фабрикаБиблиотекаРасчет
            = e -> (FxРасчет)e.архив().создатьНовыйЭлемент( XmlBrains.XML_COMPUTE, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxКонтакт,FxПараметр> фабрикаКонтактПараметр
            = e -> (FxПараметр)e.архив().создатьНовыйЭлемент( XmlBrains.XML_PARAMETER, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxПроцессор,FxПараметр> фабрикаПроцессорПараметр
            = e -> (FxПараметр)e.архив().создатьНовыйЭлемент( XmlBrains.XML_PARAMETER, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxТочка,FxПараметр> фабрикаТочкаПараметр
            = e -> (FxПараметр)e.архив().создатьНовыйЭлемент( XmlBrains.XML_PARAMETER, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxФрагмент,FxПараметр> фабрикаФрагментПараметр
            = e -> (FxПараметр)e.архив().создатьНовыйЭлемент( XmlBrains.XML_PARAMETER, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxСигнал,FxПараметр> фабрикаСигналПараметр
            = e -> (FxПараметр)e.архив().создатьНовыйЭлемент( XmlBrains.XML_PARAMETER, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxПараметр,FxПараметр> фабрикаПараметрПараметр
            = e -> (FxПараметр)e.архив().создатьНовыйЭлемент( XmlBrains.XML_PARAMETER, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxБиблиотека,FxПоле> фабрикаБиблиотекаПоле
            = e -> (FxПоле)e.архив().создатьНовыйЭлемент( XmlBrains.XML_FIELD, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxПакет,FxПроект> фабрикаПакетПроект
            = e -> (FxПроект)e.архив().создатьНовыйЭлемент( XmlBrains.XML_PROJECT, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxМодуль,FxПроцессор> фабрикаМодульПроцессор
            = e -> (FxПроцессор)e.архив().создатьНовыйЭлемент( XmlBrains.XML_PROCESSOR, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxБиблиотека,FxПроцессор> фабрикаБиблиотекаПроцессор
            = e -> (FxПроцессор)e.архив().создатьНовыйЭлемент( XmlBrains.XML_PROCESSOR, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxПроект,FxПроцессор> фабрикаПроектПроцессор
            = e -> (FxПроцессор)e.архив().создатьНовыйЭлемент( XmlBrains.XML_PROCESSOR, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxПоле,FxСенсор> фабрикаПолеСенсор
            = e -> (FxСенсор)e.архив().создатьНовыйЭлемент( XmlBrains.XML_SENSOR, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxМодуль,FxСигнал> фабрикаМодульСигнал
            = e -> (FxСигнал)e.архив().создатьНовыйЭлемент( XmlBrains.XML_SIGNAL, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxПроект,FxСигнал> фабрикаПроектСигнал
            = e -> (FxСигнал)e.архив().создатьНовыйЭлемент( XmlBrains.XML_SIGNAL, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxЛента,FxСоединение> фабрикаЛентаСоединение
            = e -> (FxСоединение)e.архив().создатьНовыйЭлемент( XmlBrains.XML_JOINT, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxМодуль,FxСоединение> фабрикаМодульСоединение
            = e -> (FxСоединение)e.архив().создатьНовыйЭлемент( XmlBrains.XML_JOINT, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxПоле,FxСоединение> фабрикаПолеСоединение
            = e -> (FxСоединение)e.архив().создатьНовыйЭлемент( XmlBrains.XML_JOINT, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxРасчет,FxСоединение> фабрикаРасчетСоединение
            = e -> (FxСоединение)e.архив().создатьНовыйЭлемент( XmlBrains.XML_JOINT, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxФрагмент,FxСоединение> фабрикаФрагментСоединение
            = e -> (FxСоединение)e.архив().создатьНовыйЭлемент( XmlBrains.XML_JOINT, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxРасчет,FxТочка> фабрикаРасчетТочка
            = e -> (FxТочка)e.архив().создатьНовыйЭлемент( XmlBrains.XML_POINT, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxТочка,FxТочка> фабрикаТочкаТочка
            = e -> (FxТочка)e.архив().создатьНовыйЭлемент( XmlBrains.XML_POINT, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxМодуль,FxФрагмент> фабрикаМодульФрагмент
            = e -> (FxФрагмент)e.архив().создатьНовыйЭлемент( XmlBrains.XML_FRAGMENT, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxПроект,FxФрагмент> фабрикаПроектФрагмент
            = e -> (FxФрагмент)e.архив().создатьНовыйЭлемент( XmlBrains.XML_FRAGMENT, XmlBrains.XMLNS_BRAINS );
    private static final Фабрика<FxЗаметка,FxТекстовыйБлок> фабрикаЗаметкаТекст
            = e -> (FxТекстовыйБлок)e.архив().создатьНовыйЭлемент( Xml.XML_CDATA, null );
    private static final Фабрика<FxГрафика,FxТекстовыйБлок> фабрикаГрафикаТекст
            = e -> (FxТекстовыйБлок)e.архив().создатьНовыйЭлемент( Xml.XML_CDATA, null );
    private static final Фабрика<FxПараметр,FxТекстовыйБлок> фабрикаПараметрТекст
            = e -> (FxТекстовыйБлок)e.архив().создатьНовыйЭлемент( Xml.XML_CDATA, null );
    private static final Фабрика<FxГрафика,FxИнструкция> фабрикаГрафикаИнструкция
            = e -> (FxИнструкция)e.архив().создатьНовыйЭлемент( Xml.PI_ELEMENT, null );

    //</editor-fold>
    
    private final ObservableList<TreeItem<FxАтрибутный>> SELECTION;

    private Provider<File> fileProviderExportXml, fileProviderExportSvg;
    private MultiplyStage multiplier;
    
    private final BooleanBinding
        disableNewАрхив, disableNewБиблиотека, disableNewЗаметка, 
        disableNewИнструкция, disableNewКонтакт, disableNewКлассJava, 
        disableNewЛента, disableNewМодуль, disableNewРасчет, disableNewПакет, disableNewПараметр, disableNewПроект, 
        disableNewПоле, disableNewПроцессор, disableNewСенсор, disableNewСигнал, 
        disableNewСоединение, disableNewТекстовыйБлок, disableNewТочка, 
        disableNewФрагмент, disableNewXmlNameSpace,             
        disableLoad, disablePreview, disableEdit, disableMultiply, 
        disableRemove, disableClose, disableProperties, 
        disableImport, disableExportXml, disableExportPic;
    
    ActionProcessor( ObservableList<TreeItem<FxАтрибутный>> selection )
    {
        SELECTION = selection;

        disableNewАрхив         = createBooleanBinding( () -> !selection.isEmpty(), selection );
        disableNewПакет         = createBooleanBinding( () -> disableAction( pNewПакет ), selection );
        disableNewПараметр      = createBooleanBinding( () -> disableAction( pNewПараметр ), selection );
        disableNewПроект        = createBooleanBinding( () -> disableAction( pNewПроект ), selection );
        disableNewБиблиотека    = createBooleanBinding( () -> disableAction( pNewБиблиотека ), selection );
        disableNewФрагмент      = createBooleanBinding( () -> disableAction( pNewФрагмент ), selection );
        disableNewСенсор        = createBooleanBinding( () -> disableAction( pNewСенсор ), selection );
        disableNewСигнал        = createBooleanBinding( () -> disableAction( pNewСигнал ), selection );
        disableNewСоединение    = createBooleanBinding( () -> disableAction( pNewСоединение ), selection );
        disableNewКонтакт       = createBooleanBinding( () -> disableAction( pNewКонтакт ), selection );
        disableNewЛента         = createBooleanBinding( () -> disableAction( pNewЛента ), selection );
        disableNewМодуль        = createBooleanBinding( () -> disableAction( pNewМодуль ), selection );
        disableNewРасчет        = createBooleanBinding( () -> disableAction( pNewРасчет ), selection );
        disableNewПоле          = createBooleanBinding( () -> disableAction( pNewПоле ), selection );
        disableNewПроцессор     = createBooleanBinding( () -> disableAction( pNewПроцессор ), selection );
        disableNewТочка         = createBooleanBinding( () -> disableAction( pNewТочка ), selection );
        disableNewЗаметка       = createBooleanBinding( () -> disableAction( pNewЗаметка ), selection );
        disableNewИнструкция    = createBooleanBinding( () -> disableAction( pNewИнструкция ), selection );
        disableNewКлассJava     = createBooleanBinding( () -> disableAction( pNewКлассJava ), selection );
        disableNewТекстовыйБлок = createBooleanBinding( () -> disableAction( pNewТекстовыйБлок ), selection );
        disableNewXmlNameSpace  = createBooleanBinding( () -> disableAction( pNewXmlNameSpace ), selection );
        disableLoad             = createBooleanBinding( () -> disableAction( pLoad ), selection );
        disableMultiply         = createBooleanBinding( () -> disableAction( pMultiply ), selection );
        disablePreview          = createBooleanBinding( () -> disableAction( pPreview ), selection );
        disableEdit             = createBooleanBinding( () -> disableAction( pEdit ), selection );
        disableRemove           = createBooleanBinding( () -> disableAction( pRemove ), selection );
        disableClose            = createBooleanBinding( () -> disableAction( pClose ), selection );
        disableProperties       = createBooleanBinding( () -> disableAction( pProperties ), selection );
        disableImport           = createBooleanBinding( () -> disableAction( pImport ), selection );
        disableExportXml        = createBooleanBinding( () -> disableAction( pExportXml ), selection );
        disableExportPic        = createBooleanBinding( () -> disableAction( pExportPic ), selection );
    }

    BooleanBinding disableNewАрхивProperty() { return disableNewАрхив; }
    BooleanBinding disableNewПакетProperty() { return disableNewПакет; }
    BooleanBinding disableNewПараметрProperty() { return disableNewПараметр; }
    BooleanBinding disableNewПроектProperty() { return disableNewПроект; }
    BooleanBinding disableNewБиблиотекаProperty() { return disableNewБиблиотека; }
    BooleanBinding disableNewФрагментProperty() { return disableNewФрагмент; }
    BooleanBinding disableNewСенсорProperty() { return disableNewСенсор; }
    BooleanBinding disableNewСигналProperty() { return disableNewСигнал; }
    BooleanBinding disableNewСоединениеProperty() { return disableNewСоединение; }
    BooleanBinding disableNewКонтактProperty() { return disableNewКонтакт; }
    BooleanBinding disableNewЛентаProperty() { return disableNewЛента; }
    BooleanBinding disableNewМодульProperty() { return disableNewМодуль; }
    BooleanBinding disableNewРасчетProperty() { return disableNewРасчет; }
    BooleanBinding disableNewПолеProperty() { return disableNewПоле; }
    BooleanBinding disableNewПроцессорProperty() { return disableNewПроцессор; }
    BooleanBinding disableNewТочкаProperty() { return disableNewТочка; }
    BooleanBinding disableNewЗаметкаProperty() { return disableNewЗаметка; }
    BooleanBinding disableNewИнструкцияProperty() { return disableNewИнструкция; }
    BooleanBinding disableNewКлассJavaProperty() { return disableNewКлассJava; }
    BooleanBinding disableNewТекстовыйБлокProperty() { return disableNewТекстовыйБлок; }
    BooleanBinding disableNewXmlNameSpaceProperty() { return disableNewXmlNameSpace; }
    BooleanBinding disableLoadProperty() { return disableLoad; }
    BooleanBinding disablePreviewProperty() { return disablePreview; }
    BooleanBinding disableEditProperty() { return disableEdit; }
    BooleanBinding disableMultiplyProperty() { return disableMultiply; }
    BooleanBinding disableRemoveProperty() { return disableRemove; }
    BooleanBinding disableCloseProperty() { return disableClose; }
    BooleanBinding disablePropertiesProperty() { return disableProperties; }
    BooleanBinding disableImportProperty() { return disableImport; }
    BooleanBinding disableExportXmlProperty() { return disableExportXml; }
    BooleanBinding disableExportPicProperty() { return disableExportPic; }
    
    //<editor-fold defaultstate="collapsed" desc="onActionXxx()">

    void onActionNewАрхив()
    {
        if( SELECTION.isEmpty() ) 
            onArchiveFromFile( );
        else
            LOGGER.log( Level.WARNING, "Unable to create {0} embedded into selection", "Архив" );
    }

    private <S extends FxАтрибутный,R extends FxАтрибутный> 
    void onActionNew( Фабрика<S,R> ф, Class<S> c, Function<S,ListExpression<R>> f ) 
    {
        SELECTION.stream()
            .map( ti -> ti.getValue() )
            .filter( e -> c.isInstance( e ) )
            .map( e -> c.cast( e ) )
            .forEach( e -> JavaFX.getInstance().execute( 
                    new TaskCreateАтрибутный<>( ф, e, f.apply( e ) ) ) );
    }

    void onActionNewБиблиотека() 
    {
        onActionNew( фабрикаМодульБиблиотека, FxМодуль.class, e -> e.библиотеки() );
        onActionNew( фабрикаПроектБиблиотека, FxПроект.class, e -> e.библиотеки() );
    }

    void onActionNewЗаметка() 
    {
        onActionNew( фабрикаЭлементЗаметка, FxЭлемент.class, e -> e.заметки() );
    }

    void onActionNewИнструкция() 
    {
        onActionNew( фабрикаГрафикаИнструкция, FxГрафика.class, e -> e.инструкции() );
    }

    void onActionNewКлассJava() 
    {
        onActionNew( фабрикаКонтактКлассJava, FxКонтакт.class, e -> e.классы() );
        onActionNew( фабрикаБиблиотекаКлассJava, FxБиблиотека.class, e -> e.классы() );
        onActionNew( фабрикаПараметрКлассJava, FxПараметр.class, e -> e.классы() );
        onActionNew( фабрикаПроцессорКлассJava, FxПроцессор.class, e -> e.классы() );
        onActionNew( фабрикаСигналКлассJava, FxСигнал.class, e -> e.классы() );
        onActionNew( фабрикаТочкаКлассJava, FxТочка.class, e -> e.классы() );
    }

    void onActionNewКонтакт() 
    {
        onActionNew( фабрикаСоединениеКонтакт, FxСоединение.class, e -> e.контакты() );
    }

    void onActionNewЛента() 
    {
        onActionNew( фабрикаБиблиотекаЛента, FxБиблиотека.class, e -> e.ленты() );
    }

    void onActionNewМодуль() 
    {
        onActionNew( фабрикаБиблиотекаМодуль, FxБиблиотека.class, e -> e.модули() );
    }

    void onActionNewРасчет() 
    {
        onActionNew( фабрикаБиблиотекаРасчет, FxБиблиотека.class, e -> e.расчеты() );
    }

    void onActionNewПакет() 
    {
        onActionNew( фабрикаАрхивПакет, FxАрхив.class, e -> e.пакеты() );
    }

    void onActionNewПараметр() 
    {
        onActionNew( фабрикаКонтактПараметр, FxКонтакт.class, e -> e.параметры() );
        onActionNew( фабрикаПроцессорПараметр, FxПроцессор.class, e -> e.параметры() );
        onActionNew( фабрикаТочкаПараметр, FxТочка.class, e -> e.параметры() );
        onActionNew( фабрикаФрагментПараметр, FxФрагмент.class, e -> e.параметры() );
        onActionNew( фабрикаСигналПараметр, FxСигнал.class, e -> e.параметры() );
        onActionNew( фабрикаПараметрПараметр, FxПараметр.class, e -> e.параметры() );
    }

    void onActionNewПоле() 
    {
        onActionNew( фабрикаБиблиотекаПоле, FxБиблиотека.class, e -> e.поля() );
    }

    void onActionNewПроект() 
    {
        onActionNew( фабрикаПакетПроект, FxПакет.class, e -> e.проекты() );
    }

    void onActionNewПроцессор() 
    {
        onActionNew( фабрикаМодульПроцессор, FxМодуль.class, e -> e.процессоры() );
        onActionNew( фабрикаБиблиотекаПроцессор, FxБиблиотека.class, e -> e.процессоры() );
        onActionNew( фабрикаПроектПроцессор, FxПроект.class, e -> e.процессоры() );
    }

    void onActionNewСенсор() 
    {
        onActionNew( фабрикаПолеСенсор, FxПоле.class, e -> e.сенсоры() );
    }

    void onActionNewСигнал() 
    {
        onActionNew( фабрикаМодульСигнал, FxМодуль.class, e -> e.сигналы() );
        onActionNew( фабрикаПроектСигнал, FxПроект.class, e -> e.сигналы() );
    }

    void onActionNewСоединение() 
    {
        onActionNew( фабрикаЛентаСоединение, FxЛента.class, e -> e.соединения() );
        onActionNew( фабрикаМодульСоединение, FxМодуль.class, e -> e.соединения() );
        onActionNew( фабрикаПолеСоединение, FxПоле.class, e -> e.соединения() );
        onActionNew( фабрикаРасчетСоединение, FxРасчет.class, e -> e.соединения() );
        onActionNew( фабрикаФрагментСоединение, FxФрагмент.class, e -> e.соединения() );
    }

    void onActionNewТекстовыйБлок() 
    {
        onActionNew( фабрикаЗаметкаТекст, FxЗаметка.class, e -> e.тексты() );
        onActionNew( фабрикаГрафикаТекст, FxГрафика.class, e -> e.тексты() );
        onActionNew( фабрикаПараметрТекст, FxПараметр.class, e -> e.тексты() );
    }

    void onActionNewТочка() 
    {
        onActionNew( фабрикаРасчетТочка, FxРасчет.class, e -> e.точки() );
        onActionNew( фабрикаТочкаТочка, FxТочка.class, e -> e.точки() );
    }

    void onActionNewФрагмент() 
    {
        onActionNew( фабрикаПроектФрагмент, FxПроект.class, e -> e.фрагменты() );
        onActionNew( фабрикаМодульФрагмент, FxМодуль.class, e -> e.фрагменты() );
    }

    void onActionNewXmlNameSpace() 
    {
        onActionNew( фабрикаАрхивNameSpace, FxАрхив.class, e -> e.namespaces() );
        //TODO two-->three instances created
    }

    void onActionLoad( ActionEvent event )
    {
        List<DbПроект> ceлектор = SELECTION.stream()
            .map( ti -> ti.getValue() )
            .filter( i -> i instanceof FxПроект )
            .map( i -> ((FxПроект)i).getSource() )
            .collect( Collectors.toList() );
        if( ceлектор.isEmpty() )
            LOGGER.log( Level.INFO, "002005002I" );
        else
            JavaFX.getInstance().execute( new ДействияПоПорядку<>( 
                ДействияПоПорядку.Приоритет.КОНТЕКСТ, действиеЗагрузитьПроект ), ceлектор );
    }
    
    void onActionPreview( ActionEvent event )
    {
        Predicate<TitledSceneGraph> inBrowser = ( TitledSceneGraph tsg ) -> tsg.node instanceof WebView;
        for( TreeItem<FxАтрибутный> item : SELECTION )
        {
            DbАтрибутный value = item.getValue().getSource();
            if( value instanceof DbЭлемент )
            {
                DbЭлемент элемент = (DbЭлемент)value;
                JavaFX jfx = JavaFX.getInstance();
                if( jfx.isShown( элемент, inBrowser ) )
                    LOGGER.log( Level.INFO, "002005008I", item instanceof TitledTreeItem ? 
                            ((TitledTreeItem)item).getTitle() : item.toString() );
                else
                {
                    // Создать, разместить и показатеть пустой навигатор
                    WebView view = new WebView();
                    view.setUserData( элемент );
                    SimpleStringProperty название = new SimpleStringProperty();
                    Image icon = JavaFX.icon( "icons16x16/preview.png" ).getImage();
                    jfx.show( элемент, inBrowser, ( DbЭлемент э ) -> new TitledSceneGraph( view, icon, название ) );
                    // загрузить элемент для просмотра
                    jfx.execute( new WebViewLoaderTask( элемент, название, view.getEngine() ) );
                }
            }
            else
                LOGGER.log( Level.WARNING, "002005010W", item instanceof TitledTreeItem ? 
                            ((TitledTreeItem)item).getTitle() : item.toString() );
        }
    }
    
    void onActionEdit( ActionEvent event )
    {
        Predicate<TitledSceneGraph> inEditor = ( TitledSceneGraph tsg ) -> tsg!=null;//tsg.node instanceof Pane; //TODO identification;
        for( TreeItem<FxАтрибутный> item : SELECTION )
        {
            FxАтрибутный fxValue = item.getValue();
            if( fxValue instanceof FxЭлемент )
            {
                DbАтрибутный value = fxValue.getSource();
                DbЭлемент элемент = (DbЭлемент)value;
                JavaFX jfx = JavaFX.getInstance();
                if( jfx.isShown( элемент, inEditor ) )
                    LOGGER.log( Level.INFO, "002005009I", item instanceof TitledTreeItem ? 
                            ((TitledTreeItem)item).getTitle() : item.toString() );
                else
                {
                    // Создать, разместить и показатеть пустой редактор
                    BuilderFX<Parent,EditorController> builder = new BuilderFX<>();
                    builder.init( EditorController.class, EditorController.RESOURCE_FXML, EditorController.RESOURCE_BUNDLE );
                    EditorController controller = builder.getController();
                    Parent view = controller.build();
                    Image icon = JavaFX.icon( "icons16x16/edit.png" ).getImage();
                    jfx.show( элемент, inEditor, ( DbЭлемент э ) -> new TitledSceneGraph( view, icon, ((FxЭлемент)fxValue).название() ) );
                    // загрузить элемент для редактирования
                    jfx.execute( new EditLoaderTask( (FxЭлемент)fxValue, controller ) );
                }
            }
            else
                LOGGER.log( Level.WARNING, "002005011W", item instanceof TitledTreeItem ? 
                            ((TitledTreeItem)item).getTitle() : item.toString() );
        }
    }
    
    void onActionMultiply( ActionEvent event )
    {
        List<FxАтрибутный[]> selection = SELECTION.stream()
            .filter( ti -> ti.getParent() != null && !(ti.getValue() instanceof FxАрхив) )
            .map( ti -> new FxАтрибутный[] { ti.getParent().getValue(), ti.getValue() } )
            .collect( Collectors.toList() );
        if( selection.isEmpty() )
            LOGGER.log( Level.INFO, "multiply.nothing" );
        else
        {
            if( multiplier == null )
            {
                multiplier = new MultiplyStage();
                multiplier.initOwner( JavaFX.getInstance().платформа );
                multiplier.initStyle( StageStyle.DECORATED );
                multiplier.initModality( Modality.APPLICATION_MODAL );
                multiplier.setTitle( LOGGER.text( "multiply.title" ) );
                final EventHandler<WindowEvent> h = multiplier.getOnCloseRequest();
                multiplier.setOnCloseRequest( h != null ?
                        e -> { multiplier = null; h.handle( e ); } :
                        e -> { multiplier = null; } );
            }
            multiplier.showAndWait();
            TaskMultiplyАтрибутный.Схема схема = multiplier.getSchema();
            if( схема != null )
            {
                LOGGER.log( Level.INFO, "multiply.started", selection.size() );
                selection.forEach( e -> JavaFX.getInstance().execute( 
                        new TaskMultiplyАтрибутный( e[0], e[1], схема ) ) );
            }
        }
    }
    
    void onActionRemove( ActionEvent event )
    {
        //TODO confirmation dialog
        SELECTION.stream().forEach( 
            i -> JavaFX.getInstance().execute( new DeleteTreeItemTask( i ) ) );
    }
    
    void onActionClose( ActionEvent event )
    {
        SELECTION.stream()
                .map( i -> i.getValue() )
                .filter( e -> e instanceof FxАрхив )
                .map( e -> (FxАрхив)e )
                .forEach( a -> 
                {
                    JavaFX jfx = JavaFX.getInstance();
                    // убрать из визуальных форм
                    jfx.архивы.remove( a );
                    // закрыть доступ и освободить ресурс
                    jfx.execute( new TaskCloseArchive( a ) );
                } );
    }
    
    static void onArchiveFromFile()
    {
        JavaFX jfx = JavaFX.getInstance();
        File file = jfx.getLocalFolderSelector().newInstance();
        if( file != null && file.isDirectory() )
            jfx.execute( new ArchiveTask( new LocalNeo4jProvider( file ) ) );
    }
    
    static void onArchiveFromNet( ActionEvent event )
    {
        JavaFX jfx = JavaFX.getInstance();
        URL url = jfx.getUrlSelector().newInstance();
        if( url != null )
            jfx.execute( new ArchiveTask( new RemoteNeo4jProvider( url ) ) );
    }
    
    static void onArchiveFromHistory( int позиция, ActionEvent event )
    {
        JavaFX jfx = JavaFX.getInstance();
        SerializableProvider<DbАрхив> provider = jfx.history.archive.get( позиция );
        if( provider != null )
            jfx.execute( new ArchiveTask( provider ) );
    }
    
    void onPackageFromFile( ActionEvent event )
    {
        JavaFX jfx = JavaFX.getInstance();
        LocalFileSelector selector = jfx.getLocalFileSelector();
        selector.setFilters( jfx.filtersXml );
        selector.setTitle( LOGGER.text( "import.xml.title" ) );
        File file = selector.newInstance();
        if( file != null )
            selectiveImport( new LocalInputStreamProvider( file ) );
    }
    
    void onPackageFromNet( ActionEvent event )
    {
        UrlSelector selector = JavaFX.getInstance().getUrlSelector();
        selector.setTitle( LOGGER.text( "import.xml.title" ) );
        URL url = selector.newInstance();
        if( url != null )
            selectiveImport( new RemoteInputStreamProvider( url ) );
    }
    
    void onPackageFromHistory( int позиция, ActionEvent event )
    {
        SerializableProvider<InputStream> provider = JavaFX.getInstance().history.xml.get( позиция );
        if( provider != null )
            selectiveImport( provider );
    }
    
    private void selectiveImport( SerializableProvider<InputStream> provider )
    {
        SELECTION.stream()
            .map( ti -> ti.getValue() )
            .filter( i -> i instanceof FxАрхив ).map( i -> (FxАрхив)i )
            .forEach( архив -> JavaFX.getInstance().execute( new ImportTask( provider, архив ) ) );
    }
    
    void onActionExportXml( ActionEvent event )
    {
        List<DbЭлемент> list = SELECTION.stream()
                .map( i -> i.getValue() )
                .filter( i -> i instanceof FxЭлемент )
                .map( i -> (DbЭлемент)((FxЭлемент)i).getSource() )
                .collect( Collectors.toList() );
        if( list.size() != 1 )
            LOGGER.log( Level.SEVERE, "Cannot save {0} elements into single file.", list.size() );
        else
        {
            DbЭлемент элемент = list.get( 0 );
            JavaFX jfx = JavaFX.getInstance();
            if( fileProviderExportXml == null ) 
            {
                FileChooser.ExtensionFilter фильтр = new FileChooser.ExtensionFilter( LOGGER.text( "ext.xml" ), "*.xml" );
                fileProviderExportXml = new ExportFileSelector( фильтр );
            }
            File file = fileProviderExportXml.newInstance();
            if( file != null )
                jfx.execute( new ЭкспортироватьXml(), new ЭкспортироватьXml.Контекст( элемент, file ) );
        }
        event.consume();
    }
    
    void onActionExportPic( ActionEvent event )
    {
        if( SELECTION.size() != 1 )
            LOGGER.log( Level.SEVERE, "Cannot save multiple {0} elements into single file.", SELECTION.size() );
        else
        {
            DbАтрибутный элемент = SELECTION.get( 0 ).getValue().getSource();
            if( элемент instanceof DbЭлемент )
            {
                JavaFX jfx = JavaFX.getInstance();
                if( fileProviderExportSvg == null ) 
                {
                    FileChooser.ExtensionFilter фильтр = new FileChooser.ExtensionFilter( LOGGER.text( "ext.svg" ), "*.svg" );
                    fileProviderExportSvg = new ExportFileSelector( фильтр );
                }
                File file = fileProviderExportSvg.newInstance();
                if( file != null )
                    jfx.execute( new ЭкспортироватьSvg(), new ЭкспортироватьSvg.Контекст( 
                            (DbЭлемент)элемент, file ) );
            }
            else
                LOGGER.getLogger().log( Level.WARNING, "Unnamed item cannot be exported: {0}", элемент.getClass().getName());
        }
        event.consume();
    }
    
    void onActionProperties( ActionEvent event )
    {
        if( SELECTION.isEmpty() )
            LOGGER.log( "002005005I" );
        else if( SELECTION.size() > 1 )
            LOGGER.log( "002005006I", SELECTION.size() );
        else
        {
            Stage stage = buildProperties( SELECTION.get( 0 ).getValue() );
            stage.show();
            stage.toFront();
        }
        event.consume();
    }

    //</editor-fold>
    
    private boolean disableAction( Predicate<FxАтрибутный> p )
    {
        return SELECTION.isEmpty() || !SELECTION.stream().map( ti -> ti.getValue() ).allMatch( p );
    }

    static Stage buildProperties( FxАтрибутный<?> атрибутный )
    {
        BuilderFX<Parent,PropertiesController> builder = new BuilderFX<>();
        builder.init( PropertiesController.class,
                PropertiesController.RESOURCE_FXML, PropertiesController.RESOURCE_BUNDLE );
        PropertiesController controller = builder.getController();
        controller.reset( атрибутный );

        Stage stage = new Stage();
        stage.initStyle( StageStyle.DECORATED );
        stage.initModality( Modality.NONE );
        stage.initOwner( JavaFX.getInstance().платформа );
        stage.getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );
        stage.titleProperty().bind( controller.titleProperty() );
        stage.setOnCloseRequest( e -> controller.reset( null ) );

        stage.setResizable( true );
        stage.setMinHeight( 350d );
        stage.setMinWidth( 400d );
        stage.setHeight( 350d ); //TODO save/restore size&pos
        stage.setWidth( 400d );
        stage.setScene( new Scene( builder.getNode() ) );

        return stage;
    }

}
