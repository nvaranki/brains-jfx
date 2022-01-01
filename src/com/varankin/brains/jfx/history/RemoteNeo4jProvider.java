package com.varankin.brains.jfx.history;

import com.varankin.biz.action.Результат;
import com.varankin.biz.action.РезультатТипа;
import com.varankin.brains.appl.ОткрытьУдаленныйАрхивNeo4j;
import com.varankin.brains.db.type.DbАрхив;
import com.varankin.brains.Контекст;

import java.net.URL;
import java.util.Objects;

/**
 * Поставщик удаленных {@linkplain DbАрхив баз данных} типа Neo4j, 
 * пригодный для использования в 
 * {@linkplain com.varankin.history.HistoryList списке хранения истории}
 *
 * @author &copy; 2021 Николай Варанкин
 */
public final class RemoteNeo4jProvider implements SerializableProvider<DbАрхив>
{
    private final URL url;

    public RemoteNeo4jProvider( URL url )
    {
        this.url = url;
    }

    @Override
    public DbАрхив newInstance()
    {
        РезультатТипа<DbАрхив> результат = new ОткрытьУдаленныйАрхивNeo4j().выполнить( url );
        if( результат.код() == Результат.НОРМА )
        {
            return результат.значение();
        }
        else
        {
            Контекст.getInstance().репортер.log( результат );
            return null;
        }
    }

    @Override
    public String toString()
    {
        return url != null ? url.toExternalForm() : "";
    }

    @Override
    public boolean equals( Object o )
    {
        return o instanceof RemoteNeo4jProvider &&
            Objects.equals( url, ((RemoteNeo4jProvider)o).url );
    }

    @Override
    public int hashCode()
    {
        return RemoteNeo4jProvider.class.hashCode() ^ Objects.hashCode( url );
    }
    
}
