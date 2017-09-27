/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Generators;

import java.util.Calendar;

/**
 *
 * @author Julien
 */
public class Generator
{
    public static String GenerateDate()
    {
        Calendar c = Calendar.getInstance();
        
        Integer iYear = c.get(Calendar.YEAR);
        
        Integer iMonth = c.get(Calendar.MONTH) + 1;
        String strMonth;
        if(iMonth < 10)
            strMonth = "0" + iMonth.toString();
        else
            strMonth = iMonth.toString();
        
        Integer iDay = c.get(Calendar.DAY_OF_MONTH);
        String strDay;
        if(iDay < 10)
            strDay = "0" + iDay.toString();
        else
            strDay = iDay.toString();
        
        return strDay + "/" + strMonth + "/" + iYear.toString();
    }
    public static String GenerateIdMouvement()
    {
        Calendar c = Calendar.getInstance();
        
        String strIdMouvement = "M";
        
        Integer iYear = c.get(Calendar.YEAR);
        
        Integer iMonth = c.get(Calendar.MONTH) + 1;
        String strMonth;
        if(iMonth < 10)
            strMonth = "0" + iMonth.toString();
        else
            strMonth = iMonth.toString();
        
        Integer iDay = c.get(Calendar.DAY_OF_MONTH);
        String strDay;
        if(iDay < 10)
            strDay = "0" + iDay.toString();
        else
            strDay = iDay.toString();
        
        Integer iHour = c.get(Calendar.HOUR_OF_DAY);
        String strHour;
        if(iHour < 10)
            strHour = "0" + iHour.toString();
        else
            strHour = iHour.toString();
        
        Integer iMinute = c.get(Calendar.MINUTE);
        String strMinute;
        if(iMinute < 10)
            strMinute = "0" + iMinute.toString();
        else
            strMinute = iMinute.toString();
        
        Integer iSecond = c.get(Calendar.SECOND);
        String strSecond;
        if(iSecond < 10)
            strSecond = "0" + iSecond.toString();
        else
            strSecond = iSecond.toString();
        
        Integer iMillisecond = c.get(Calendar.MILLISECOND);
        String strMillisecond;
        if(iMillisecond < 10)
            strMillisecond = "00" + iMillisecond.toString();
        else if(iMillisecond < 100)
            strMillisecond = "0" + iMillisecond.toString();
        else
            strMillisecond = iMillisecond.toString();
        
        strIdMouvement = strIdMouvement + iYear.toString() + strMonth + strDay + strHour + strMinute + strSecond + strMillisecond;
        
        return strIdMouvement;
    }
    
    public static String GenerateNumeroReservation()
    {
        Calendar c = Calendar.getInstance();
        
        String strNumeroReservation = "R";
        
        Integer iYear = c.get(Calendar.YEAR);
        
        Integer iMonth = c.get(Calendar.MONTH) + 1;
        String strMonth;
        if(iMonth < 10)
            strMonth = "0" + iMonth.toString();
        else
            strMonth = iMonth.toString();
        
        Integer iDay = c.get(Calendar.DAY_OF_MONTH);
        String strDay;
        if(iDay < 10)
            strDay = "0" + iDay.toString();
        else
            strDay = iDay.toString();
        
        Integer iHour = c.get(Calendar.HOUR_OF_DAY);
        String strHour;
        if(iHour < 10)
            strHour = "0" + iHour.toString();
        else
            strHour = iHour.toString();
        
        Integer iMinute = c.get(Calendar.MINUTE);
        String strMinute;
        if(iMinute < 10)
            strMinute = "0" + iMinute.toString();
        else
            strMinute = iMinute.toString();
        
        Integer iSecond = c.get(Calendar.SECOND);
        String strSecond;
        if(iSecond < 10)
            strSecond = "0" + iSecond.toString();
        else
            strSecond = iSecond.toString();
        
        Integer iMillisecond = c.get(Calendar.MILLISECOND);
        String strMillisecond;
        if(iMillisecond < 10)
            strMillisecond = "00" + iMillisecond.toString();
        else if(iMillisecond < 100)
            strMillisecond = "0" + iMillisecond.toString();
        else
            strMillisecond = iMillisecond.toString();
        
        strNumeroReservation = strNumeroReservation + iYear.toString() + strMonth + strDay + strHour + strMinute + strSecond + strMillisecond;
        
        return strNumeroReservation;
    }
}
