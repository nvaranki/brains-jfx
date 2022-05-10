package com.varankin.brains.jfx.history;

import com.varankin.biz.action.Результат;
import com.varankin.biz.action.РезультатТипа;
import com.varankin.brains.appl.ОткрытьЛокальныйАрхивNeo4j;
import com.varankin.brains.db.type.DbАрхив;
import com.varankin.brains.Контекст;

import java.io.*;
import java.util.Objects;

/**
 * Поставщик локальных {@linkplain DbАрхив баз данных} типа Neo4j, 
 * пригодный для использования в 
 * {@linkplain com.varankin.history.HistoryList списке хранения истории}
 *
 * @author &copy; 2021 Николай Варанкин
 */
public final class LocalNeo4jProvider implements SerializableProvider<DbАрхив>
{
    private final File dir;

    public LocalNeo4jProvider( File dir )
    {
        this.dir = dir;
    }

    @Override
    public DbАрхив newInstance()
    {
        РезультатТипа<DbАрхив> результат = new ОткрытьЛокальныйАрхивNeo4j().выполнить( dir );
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
        return dir != null ? dir.getAbsolutePath() : "";
    }

    @Override
    public boolean equals( Object o )
    {
        return o instanceof LocalNeo4jProvider &&
            Objects.equals( dir, ((LocalNeo4jProvider)o).dir );
    }

    @Override
    public int hashCode()
    {
        return LocalNeo4jProvider.class.hashCode() ^ Objects.hashCode( dir );
    }
    
}
