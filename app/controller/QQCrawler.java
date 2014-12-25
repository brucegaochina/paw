package controller;

import com.csvreader.CsvWriter;
import common.CommonPattern;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;


public class QQCrawler extends WebCrawler {

	private final static String CSV_PATH = "data/crawl/";
	private CsvWriter qqCw;

	private static File qqCsv;

	static Set<String> qqContainer = null;

	public QQCrawler() throws IOException {

		qqContainer = new HashSet<String>();

		qqCsv = new File(CSV_PATH + "/" + formatDate(new Date(), "yyyy-MM-dd") + "_qq.csv");

		qqCw = new CsvWriter(new FileWriter(qqCsv, true), ',');
	}

	public String formatDate(Date date,String format){
		if(date ==null) return "";
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		if (CommonPattern.FILTERS.matcher(href).matches()) {
			return false;
		}

		String[] strs = href.split("\\?");
		if (strs.length < 2) {
			return false;
		}
		
		return true;
	}
	
	public void visit(Page page)  {

		String url = page.getWebURL().getURL();
		String[] phone = url.split("13");
		if(phone.length > 1){
			System.out.println(phone);
		}

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();
			Matcher qqMatcher = CommonPattern.QQ.matcher(html);

			while(qqMatcher.find()) {

				// 写qq号
				qqContainer.add(qqMatcher.group());
				if(qqContainer.size() == 100){
					try {
						qqCw.write(qqContainer.toString().substring(1, qqContainer.toString().length()-1));
						qqCw.endRecord();
						qqCw.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
					qqContainer.clear();
				}
			}
		}
	}
}
