package common;

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

}
