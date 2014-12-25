package common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: bruce
 * Date: 2014-12-25
 */
public class CommonUtils {

    static class SingleTon{
        static CommonUtils instatnce = new CommonUtils();
    }

    public static CommonUtils getInstance(){
        return SingleTon.instatnce;
    }

    public String formatDate(Date date,String format){
        if(date ==null) return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

}
