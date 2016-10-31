package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.*;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.characteristic.Именованный;
import java.util.*;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;

import static com.varankin.brains.io.xml.Xml.*;
import static com.varankin.brains.io.xml.XmlBrains.*;

/**
 * Фабрика ресурсов для навигатора по проектам.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
final class ArchiveResourceFactory
{
    private static final String RB_BASE_NAME = ArchiveResourceFactory.class.getPackage().getName() + ".text";
    private static final Logger LOGGER = Logger.getLogger( ArchiveResourceFactory.class.getName(), RB_BASE_NAME );
    private static final ResourceBundle RB = LOGGER.getResourceBundle();
    private static final String XML_NS_TEMP = "#NS";
    private static final String XML_WB_TEMP = "#WB";
    private static final String XML_TB_TEMP = "#TB";
    private static final String XML_UN_TEMP = "#OTHER";
    private static final String XML_GRAPHIC = "#GRAPHIC";
    private static final Map<String,String> ключМаркиКоллекции, ключМеткиКоллекции, 
            ключМаркиЭлемента, ключМеткиЭлемента, ключПодсказки;

    private ArchiveResourceFactory() {}

    static Node марка( DbАтрибутный элемент )
    {
        return JavaFX.icon( "icons16x16/" + ключМаркиЭлемента.get( тип( элемент, ключМаркиЭлемента ) ) );
    }

    static Node маркаКоллекции( String тип )
    {
        String файл = ключМаркиКоллекции.containsKey( тип ) ? ключМаркиКоллекции.get( тип ) : "fragment.png";
        return JavaFX.icon( "icons16x16/" + файл );
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

    static String меткаКоллекции( String тип )
    {
        String ключ = ключМеткиКоллекции.get( тип );
        return RB.containsKey( ключ ) ? RB.getString( ключ ) : тип;
    }

    static Tooltip подсказка( DbАтрибутный элемент )
    {
        String ключ = ключПодсказки.get( тип( элемент, ключПодсказки ) );
        return RB.containsKey( ключ ) ? new Tooltip( RB.getString( ключ ) ) : null;
    }
    
    private static String тип( DbАтрибутный элемент, Map<String,String> карта )
    {
        String тип = элемент.тип().название();
        if( элемент instanceof DbNameSpace )
            тип = XML_NS_TEMP;
        else if( элемент instanceof DbМусор )
            тип = XML_WB_TEMP;
        else if( элемент instanceof DbТекстовыйБлок )
            тип = XML_TB_TEMP;
        else if( элемент instanceof DbГрафика )
            тип = XML_GRAPHIC;
        return карта.containsKey( тип ) ? тип : XML_UN_TEMP;
    }
    
    static
    {
        String[][] ресурсы =
        {
            // тип           RB-название   марка              коллекция
            { XML_ARHIVE,    "archive",    "archive",         "архивы",     }, // нет такой коллекции
            { XML_BRAINS,    "package",    "package",         "пакеты",     },
            { XML_LIBRARY,   "library",    "new-library.png", "библиотеки", },
            { XML_PROJECT,   "project",    "new-project.png", "проекты",    },
            { XML_SIGNAL,    "signal",     "signal.png",      "сигналы",    },
            { XML_FRAGMENT,  "fragment",   "fragment.png",    "фрагменты",  },
            { XML_NOTE,      "note",       "properties.png",  "заметки",    },
            { XML_PROCESSOR, "processor",  "processor2.png",  "процессоры", },
            { XML_PARAMETER, "parameter",  "parameter",       "параметры",  },
            { XML_JAVA,      "class.java", "JavaIcon.gif",    "классы",     },
            { XML_JOINT,     "connector",  "connector.png",   "соединения", },
            { XML_PIN,       "pin",        "pin.png",         "контакты",   },
            { XML_CONVERTER, "converter",  "converter",       "конвертеры", },
            { XML_COMPUTE,   "compute",    "function.png",    "расчеты",    },
            { XML_MODULE,    "module",     "module.png",      "модули",     },
            { XML_TIMELINE,  "timeline",   "timeline",        "ленты",      },
            { XML_FIELD,     "field",      "field2.png",      "поля",       },
            { XML_SENSOR,    "sensor",     "sensor.png",      "сенсоры",    },
            { XML_POINT,     "point",      "point.png",       "точки",      },
            { PI_ELEMENT,    "instruction","instruction",     "инструкции", },
            { XML_NS_TEMP,   "namespace",  "namespace",       "namespaces", }, // нет такого типа
            { XML_WB_TEMP,   "waste",      "remove.png",      "мусор",      }, // нет такого типа
            { XML_TB_TEMP,   "text",       "text",            "тексты",     }, // нет такого типа
            { XML_UN_TEMP,   "other",      "other",           "прочее",     }, // нет такого типа
            { XML_GRAPHIC,   "graphic",    "preview.png",     "графики",    }, // нет такого типа
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