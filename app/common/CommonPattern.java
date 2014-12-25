package common;

import java.util.regex.Pattern;

/**
 * Author: bruce
 * Date: 2014-12-25
 */
public class CommonPattern {

    public final static Pattern FILTERS = Pattern
            .compile(".*(\\.(css|js|bmp|gif|jpe?g|ico"
                    + "|png|tiff?|mid|mp2|mp3|mp4"
                    + "|wav|avi|mov|mpeg|ram|m4v|pdf"
                    + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    public final static Pattern QQ = Pattern.compile("[1-9][0-9]{4,9}");// QQ号从10000开始

    public final static Pattern EMAIL = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");

    public final static Pattern CELL_PHONE = Pattern.compile("(13[0-9]|14[0-9]|15[0-9]|17[678]|18[0-9])\\d{8}");
}
