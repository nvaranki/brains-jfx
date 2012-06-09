package com.varankin.brains.jfx;

import com.varankin.util.Текст;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для установки источника данных о мыслительной структуре.
 * Источником служит последний успешно установленный источник.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionRepositoryInThePast extends Action
{
    private final ApplicationView.Context context;
    private Task<Void> агент;

    ApplicationActionRepositoryInThePast( ApplicationView.Context context )
    {
        super( context.jfx, Текст.ПАКЕТЫ.словарь( ApplicationActionRepositoryInThePast.class, 
                context.jfx.контекст.специфика ) );
        this.context = context;
        setEnabled( false );
    }

    void агент( Task<Void> worker, String метка )
    {
        агент = worker;
        setText( "1 " + limitTextLength( метка != null ? метка : "" ) );
        setEnabled( агент != null );
    }
    
    @Override
    public void handle( ActionEvent event )
    {
        if( агент != null )
        {
            context.actions.getRepositorySql().setEnabled( false );
            context.actions.getRepositoryXml().setEnabled( false );
            context.actions.getRepositoryInThePast().setEnabled( false );
            
            jfx.getExecutorService().execute( агент ); 
        }
    }

    private String limitTextLength( String оригинал )
    {
        StringBuilder chars = new StringBuilder( оригинал );
        int лимит = Integer.valueOf( jfx.контекст.параметр( "frame.file.path.limit", "30" ) );
        int размер = chars.length();
        if( размер > лимит )
        {
            String вставка = jfx.контекст.параметр( "frame.file.path.implant", "..." );
            int m = размер / 2;
            int ext = Math.max( 0, размер - лимит + вставка.length() ) / 2;
            chars.replace( m - ext, m + ext, вставка );
        }
        String замена = chars.toString();
        return замена;
    }

}
