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


public class EmailCrawler extends WebCrawler {

	private final static String CSV_PATH = "data/crawl/";
	private CsvWriter emailCw;

	private static File emailCsv;

	static Set<String> emailContainer = null;

	public EmailCrawler() throws IOException {

		emailContainer = new HashSet<String>();

		emailCsv = new File(CSV_PATH + "/" + formatDate(new Date(), "yyyy-MM-dd") + "_email.csv");

		emailCw = new CsvWriter(new FileWriter(emailCsv, true), ',');
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
			Matcher emailMatcher = CommonPattern.EMAIL.matcher(html);

			while(emailMatcher.find()) {

				// 写email
				emailContainer.add(emailMatcher.group());
				if(emailContainer.size() == 100){
					try {
						emailCw.write(emailContainer.toString().substring(1, emailContainer.toString().length()-1));
						emailCw.endRecord();
						emailCw.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
					emailContainer.clear();
				}

			}

		}
	}
}
