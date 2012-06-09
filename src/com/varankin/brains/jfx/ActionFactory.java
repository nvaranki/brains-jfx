package com.varankin.brains.jfx;

import javafx.stage.FileChooser;

/**
 * Предопределенные {@linkplain Action действия} в контексте среды JavaFX.
 *
 * @author &copy; 2012 Николай Варанкин
 */
public class ActionFactory
{
    private final ApplicationView.Context context;

    private ApplicationActionAbout about;
    private ApplicationActionExit exit;
    private ApplicationActionStart start;
    private ApplicationActionStop stop;
    private ApplicationActionPause pause;
    private ApplicationActionLoad load;
    private ApplicationActionClean clean;
    private ApplicationActionRepositorySql rSql;
    private ApplicationActionRepositoryXml rXml;
    private ApplicationActionRepositoryInThePast rPast;
    private ApplicationActionImportXml[] iXml;

    ActionFactory( ApplicationView.Context context )
    {
        this.context = context;
    }

    Action getAbout()
    {
        if( about == null ) about = new ApplicationActionAbout( context );
        return about;
    }

    Action getExit()
    {
        if( exit == null ) exit = new ApplicationActionExit( context );
        return exit;
    }

    Action getPause()
    {
        if( pause == null ) pause = new ApplicationActionPause( context );
        return pause;
    }

    Action getStart()
    {
        if( start == null ) start = new ApplicationActionStart( context );
        return start;
    }

    Action getStop()
    {
        if( stop == null ) stop = new ApplicationActionStop( context );
        return stop;
    }

    Action getLoad()
    {
        if( load == null ) load = new ApplicationActionLoad( context );
        return load;
    }

    Action getClean()
    {
        if( clean == null ) clean = new ApplicationActionClean( context );
        return clean;
    }

    Action getRepositorySql()
    {
        if( rSql == null ) rSql = new ApplicationActionRepositorySql( context );
        return rSql;
    }

    Action getRepositoryXml()
    {
        if( rXml == null ) rXml = new ApplicationActionRepositoryXml( context );
        return rXml;
    }

    Action[] getImportXml()
    {
        if( iXml == null ) 
            iXml = new ApplicationActionImportXml[]
                { 
                    new ApplicationActionImportXml( context, new FileChooser() ), 
                    new ApplicationActionImportXml( context, 1 ), 
                    new ApplicationActionImportXml( context, 2 ), 
                    new ApplicationActionImportXml( context, 3 ) 
                    //TODO predefined array size by config
                };
        return iXml;
    }

    ApplicationActionRepositoryInThePast getRepositoryInThePast()
    {
        if( rPast == null ) rPast = new ApplicationActionRepositoryInThePast( context );
        return rPast;
    }
    
}
