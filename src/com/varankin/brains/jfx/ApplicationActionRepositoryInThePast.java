package com.varankin.brains.jfx;

import com.varankin.brains.Контекст;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для установки источника данных о мыслительной структуре.
 * Источником служит последний успешно установленный источник.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ApplicationActionRepositoryInThePast extends AbstractJfxAction<ApplicationView.Context>
{
    private Task<Void> агент;

    ApplicationActionRepositoryInThePast( ApplicationView.Context context )
    {
        super( context, context.jfx.словарь( ApplicationActionRepositoryInThePast.class ) );
        //setEnabled( false );
    }

    void агент( Task<Void> worker, String метка )
    {
        агент = worker;
        textProperty().setValue( "1 " + limitTextLength( метка != null ? метка : "" ) );
        //setEnabled( агент != null );
    }
    
    @Override
    public void handle( ActionEvent event )
    {
        if( агент != null )
        {
//            контекст.actions.getRepositorySql().setEnabled( false );
//            контекст.actions.getRepositoryXml().setEnabled( false );
//            контекст.actions.getRepositoryInThePast().setEnabled( false );
            
            контекст.jfx.getExecutorService().execute( агент ); 
        }
    }

    private String limitTextLength( String оригинал )
    {
        StringBuilder chars = new StringBuilder( оригинал );
        Контекст к = контекст.jfx.контекст;
        int лимит = Integer.valueOf( к.параметр( "frame.file.path.limit", "30" ) );
        int размер = chars.length();
        if( размер > лимит )
        {
            String вставка = к.параметр( "frame.file.path.implant", "..." );
            int m = размер / 2;
            int ext = Math.max( 0, размер - лимит + вставка.length() ) / 2;
            chars.replace( m - ext, m + ext, вставка );
        }
        String замена = chars.toString();
        return замена;
    }

}
