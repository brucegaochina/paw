package controller;

import com.csvreader.CsvWriter;
import common.CommonPattern;
import common.CommonUtils;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;


public class CellPhoneCrawler extends WebCrawler {

	private final static String CSV_PATH = "data/crawl/";
	private CsvWriter phoneCw;

	public CellPhoneCrawler() throws IOException {
		File phoneCsv = new File(CSV_PATH + "/" + CommonUtils.getInstance().formatDate(new Date(), "yyyy-MM-dd") + "_phone.csv");

		phoneCw = new CsvWriter(new FileWriter(phoneCsv, true), ',');
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

		Set<String> phoneContainer = new HashSet<String>();

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();
			Matcher phoneMatcher = CommonPattern.CELL_PHONE.matcher(html);

			while(phoneMatcher.find()) {

				// 写手机号码
				phoneContainer.add(phoneMatcher.group());
				if(phoneContainer.size() == 100){
					try {
						phoneCw.write(phoneContainer.toString().substring(1, phoneContainer.toString().length()-1));
						phoneCw.endRecord();
						phoneCw.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
					phoneContainer.clear();
				}
			}
		}
	}
}
