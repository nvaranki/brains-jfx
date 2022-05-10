/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.varankin.brains.jfx.archive.multi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Схема мультипликации элемента коллекции.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
public class Схема 
{
    
    public static class Направление 
    {
        public int повтор;
        public int смещение;
    }

    public enum Наименование 
    {
        ЦЕПОЧКА_RC, ЦЕПОЧКА_CR, МАТРИЦА_RC, МАТРИЦА_CR
    }
    
    String префикс;
    Integer индекс;
    Наименование наименование = Наименование.ЦЕПОЧКА_RC;
    public final Направление x = new Направление();
    public final Направление y = new Направление();

    public String название( int row, int col, String название ) 
    {
        String[] spl = split( название );
        String префикс;
        String суффикс;
        String name = this.префикс != null ? this.префикс : spl[0];
        Integer index = индекс != null ? индекс : (spl[1] == null || spl[1].isEmpty()) ? null : Integer.valueOf(spl[1]);
        if( name == null && index == null ) 
            return null;
        префикс = name == null ? "" : name;
        int dcc;
        int dcr;
        int dcn; //TODO index defines base, so base + x.повтор and so on
        dcc = 1 + (int) Math.floor(Math.log10(Math.max(1, x.повтор)));
        dcr = 1 + (int) Math.floor(Math.log10(Math.max(1, y.повтор)));
        dcn = 1 + (int) Math.floor(Math.log10(Math.max(1, y.повтор * x.повтор)));
        switch( наименование ) 
        {
            case ЦЕПОЧКА_RC:
                суффикс = index == null ? "" : Integer.toString(index + row * x.повтор + col); //TODO dcn
                return префикс + суффикс;
            case ЦЕПОЧКА_CR:
                суффикс = index == null ? "" : Integer.toString(index + col * y.повтор + row);
                return префикс + суффикс;
            case МАТРИЦА_RC:
                суффикс = index == null ? "" : String.format("%0" + dcr + "d", row) + String.format("%0" + dcc + "d", col);
                return префикс + суффикс;
            case МАТРИЦА_CR:
                суффикс = index == null ? "" : String.format("%0" + dcr + "d", row) + String.format("%0" + dcc + "d", col);
                return префикс + суффикс;
            default:
                return null;
        }
    }

    private static String[] split(String название) 
    {
        Pattern p = Pattern.compile( "(\\D*)(\\d*)\\z" );
        Matcher pm = p.matcher(название);
        return pm.matches() ? new String[]{pm.group(1), pm.group(2)} : new String[]{null, null};
    }
    
}
