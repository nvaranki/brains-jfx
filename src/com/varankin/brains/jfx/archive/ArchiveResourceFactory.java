package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.Транзакция;
import com.varankin.brains.db.type.DbЗона;
import com.varankin.brains.db.type.DbГрафика;
import com.varankin.brains.db.type.DbАтрибутный;
import com.varankin.brains.db.xml.ЗонныйКлюч;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.db.*;
import com.varankin.characteristic.Именованный;

import java.util.*;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;

import static com.varankin.brains.db.xml.Xml.*;
import static com.varankin.brains.db.xml.XmlBrains.*;
import static com.varankin.io.xml.svg.XmlSvg.*;

/**
 * Фабрика ресурсов для навигатора по проектам.
 * 
 * @author &copy; 2022 Николай Варанкин
 */
public final class ArchiveResourceFactory
{
    private static final String RB_BASE_NAME = ArchiveResourceFactory.class.getPackage().getName() + ".text";
    private static final Logger LOGGER = Logger.getLogger( ArchiveResourceFactory.class.getName(), RB_BASE_NAME );
    private static final ResourceBundle RB = LOGGER.getResourceBundle();
    private static final String XML_NS_TEMP = "#NS";
    private static final String XML_UN_TEMP = "#OTHER";
    private static final String XML_GRAPHIC = "#GRAPHIC";
    private static final Map<String,String> ключМаркиКоллекции, ключМеткиКоллекции, 
            ключМаркиЭлемента, ключМеткиЭлемента, ключПодсказки;

    private ArchiveResourceFactory() {}

    public static Node марка( DbАтрибутный элемент )
    {
        return JavaFX.icon( "icons16x16/" + ключМаркиЭлемента.get( тип( элемент, ключМаркиЭлемента ) ) );
    }

    public static Node марка( FxАтрибутный элемент )
    {
        return JavaFX.icon( "icons16x16/" + ключМаркиЭлемента.get( тип( элемент, ключМаркиЭлемента ) ) );
    }

    static Node маркаКоллекции( String тип )
    {
        String файл = ключМаркиКоллекции.containsKey( тип ) ? ключМаркиКоллекции.get( тип ) : "fragment.png";
        return JavaFX.icon( "icons16x16/" + файл );
    }

    public static String метка( ЗонныйКлюч ключ )
    {
        String тип = ключ.НАЗВАНИЕ;
        if( !ключМеткиЭлемента.containsKey( тип ) ) тип = XML_UN_TEMP;
        return RB.getString( ключМеткиЭлемента.get( тип ) );
    }

    static String метка( DbАтрибутный элемент )
    {
        if( элемент instanceof Именованный )
        {
            String текст = ((Именованный)элемент).название();
            if( текст != null && ! текст.trim().isEmpty() )
                return текст;
        }
        return RB.getString( ключМеткиЭлемента.get( тип( элемент, ключМеткиЭлемента ) ) );
    }

    static String метка( FxАтрибутный элемент )
    {
        if( элемент.getSource() instanceof Именованный )
            try( final Транзакция т = элемент.getSource().транзакция() )
            {
                String текст = ((Именованный)элемент.getSource()).название();
                т.завершить( true );
                if( текст != null && ! текст.trim().isEmpty() )
                    return текст;
            }
            catch( Exception e )
            {
                throw new RuntimeException( e );
            }
        return RB.getString( ключМеткиЭлемента.get( тип( элемент, ключМеткиЭлемента ) ) );
    }

    static String меткаКоллекции( String тип )
    {
        String ключ = ключМеткиКоллекции.get( тип );
        return RB.containsKey( ключ ) ? RB.getString( ключ ) : тип;
    }
    
    static StringBinding меткаУниверсальная( FxАтрибутный<?> элемент )
    {
        String метка = метка( элемент.тип().getValue() ); // нельзя в StringBinding!
        Property<String> fxp = название( элемент, метка );
        return Bindings.createStringBinding( () -> 
            {
                // замена пустого названия на название типа
                String название = fxp.getValue();
                return название == null || название.trim().isEmpty() ? метка : название;
            }, fxp );
    }
    
    public static Property<String> название( FxАтрибутный<?> элемент, String замена )
    {
        return 
                элемент instanceof FxЭлемент ? 
                ((FxЭлемент<?>)элемент).название() : 
                 элемент instanceof FxЗона ? 
                ((FxЗона)элемент).название() : 
                 элемент instanceof FxПакет ? 
                ((FxПакет)элемент).название() : 
                элемент instanceof FxАрхив ? 
                ((FxАрхив)элемент).название() : 
                new SimpleStringProperty( замена );
    }

    static Tooltip подсказка( DbАтрибутный элемент )
    {
        String ключ = ключПодсказки.get( тип( элемент, ключПодсказки ) );
        return RB.containsKey( ключ ) ? new Tooltip( RB.getString( ключ ) ) : null;
    }
    
    static Tooltip подсказка( FxАтрибутный элемент )
    {
        String ключ = ключПодсказки.get( тип( элемент, ключПодсказки ) );
        return RB.containsKey( ключ ) ? new Tooltip( RB.getString( ключ ) ) : null;
    }
    
    private static String тип( DbАтрибутный элемент, Map<String,String> карта )
    {
        String тип = элемент.тип().НАЗВАНИЕ;
        if( элемент instanceof DbЗона )
            тип = XML_NS_TEMP;
        else if( элемент instanceof DbГрафика )
            тип = XML_GRAPHIC;
        return карта.containsKey( тип ) ? тип : XML_UN_TEMP;
    }
    
    private static String тип( FxАтрибутный<?> элемент, Map<String,String> карта )
    {
        String тип = элемент.тип().getValue().НАЗВАНИЕ;
        if( элемент instanceof FxЗона )
            тип = XML_NS_TEMP;
        else if( элемент instanceof FxГрафика )
            тип = XML_GRAPHIC;
        return карта.containsKey( тип ) ? тип : XML_UN_TEMP;
    }
    
    static
    {
        String[][] ресурсы =
        {
            // тип           RB-название   марка              коллекция
            { XML_ARHIVE,    "archive",    "archive.png",     "архивы",     }, // нет такой коллекции
            { XML_BRAINS,    "package",    "package.png",     "пакеты",     },
            { XML_LIBRARY,   "library",    "new-library.png", "библиотеки", },
            { XML_PROJECT,   "project",    "new-project.png", "проекты",    },
            { XML_SIGNAL,    "signal",     "signal.png",      "сигналы",    },
            { XML_FRAGMENT,  "fragment",   "fragment.png",    "фрагменты",  },
            { XML_NOTE,      "note",       "properties.png",  "заметки",    },
            { XML_PROCESSOR, "processor",  "processor2.png",  "процессоры", },
            { XML_PARAMETER, "parameter",  "parameter.png",   "параметры",  },
            { XML_JAVA,      "class.java", "java.png",        "классы",     },
            { XML_JOINT,     "connector",  "connector.png",   "соединения", },
            { XML_PIN,       "pin",        "pin.png",         "контакты",   },
            { XML_COMPUTE,   "compute",    "function.png",    "расчеты",    },
            { XML_MODULE,    "module",     "module.png",      "модули",     },
            { XML_TIMELINE,  "timeline",   "timeline.png",    "ленты",      },
            { XML_FIELD,     "field",      "field2.png",      "поля",       },
            { XML_SENSOR,    "sensor",     "sensor.png",      "сенсоры",    },
            { XML_POINT,     "point",      "point.png",       "точки",      },
            { PI_ELEMENT,    "instruction","instruction.png", "инструкции", },
            { XML_NS_TEMP,   "namespace",  "namespace.png",   "namespaces", }, // нет такого типа
            { XML_BASKET,    "waste",      "remove.png",      "мусор",      },
            { XML_CDATA,     "text",       "text.png",        "тексты",     },
            { XML_UN_TEMP,   "other",      "other",           "прочее",     }, // нет такого типа
            { XML_GRAPHIC,   "graphic",    "preview.png",     "графики",    }, // нет такого типа
            { SVG_ELEMENT_CIRCLE,   "svg."+SVG_ELEMENT_CIRCLE,   "preview.png", "графики", },
            { SVG_ELEMENT_ELLIPSE,  "svg."+SVG_ELEMENT_ELLIPSE,  "preview.png", "графики", },
            { SVG_ELEMENT_A,        "svg."+SVG_ELEMENT_A,        "preview.png", "графики", },
            { SVG_ELEMENT_G,        "svg."+SVG_ELEMENT_G,        "preview.png", "графики", },
            { SVG_ELEMENT_LINE,     "svg."+SVG_ELEMENT_LINE,     "preview.png", "графики", },
            { SVG_ELEMENT_POLYGON,  "svg."+SVG_ELEMENT_POLYGON,  "preview.png", "графики", },
            { SVG_ELEMENT_POLYLINE, "svg."+SVG_ELEMENT_POLYLINE, "preview.png", "графики", },
            { SVG_ELEMENT_RECT,     "svg."+SVG_ELEMENT_RECT,     "preview.png", "графики", },
            { SVG_ELEMENT_SYMBOL,   "svg."+SVG_ELEMENT_SYMBOL,   "preview.png", "графики", },
            { SVG_ELEMENT_TEXT,     "svg."+SVG_ELEMENT_TEXT,     "preview.png", "графики", },
            { SVG_ELEMENT_USE,      "svg."+SVG_ELEMENT_USE,      "preview.png", "графики", },
        };

        ключМеткиЭлемента = new HashMap<>();
        for( String[] sa : ресурсы )
            ключМеткиЭлемента.put( sa[0], "collection." + sa[1] + ".1" );
        
        ключМеткиКоллекции = new HashMap<>();
        for( String[] sa : ресурсы )
            ключМеткиКоллекции.put( sa[3], "collection." + sa[1] + ".n" );
        
        ключМаркиЭлемента = new HashMap<>();
        for( String[] sa : ресурсы )
            ключМаркиЭлемента.put( sa[0], sa[2] );
        
        ключМаркиКоллекции = new HashMap<>();
        for( String[] sa : ресурсы )
            ключМаркиКоллекции.put( sa[3], sa[2] );
        
        ключПодсказки = new HashMap<>();
        for( String[] sa : ресурсы )
            ключПодсказки.put( sa[0], "cell." + sa[1] );
    }
    
}
