import com.csvreader.CsvWriter;
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
import java.util.regex.Pattern;


public class MailCrawler extends WebCrawler {
	private final static Pattern FILTERS = Pattern
			.compile(".*(\\.(css|js|bmp|gif|jpe?g|ico"
					+ "|png|tiff?|mid|mp2|mp3|mp4"
					+ "|wav|avi|mov|mpeg|ram|m4v|pdf"
					+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	
	private final static Pattern EMAIL = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");

	private final static Pattern QQ = Pattern.compile("[1-9][0-9]{4,9}");// QQ号从10000开始

	private final static Pattern phoneRegx = Pattern.compile("(13[0-9]|14[0-9]|15[0-9]|17[678]|18[0-9])\\d{8}");

	private final static String CSV_PATH = "data/crawl/";
	private CsvWriter phoneCw;
	private CsvWriter emailCw;
	private CsvWriter qqCw;

	private static File phoneCsv;
	private static File emailCsv;
	private static File qqCsv;

	static Set<String> phoneContainer = null;
	static Set<String> emailContainer = null;
	static Set<String> qqContainer = null;

	public MailCrawler() throws IOException {

		phoneContainer = new HashSet<String>();
		emailContainer = new HashSet<String>();
		qqContainer = new HashSet<String>();

		phoneCsv = new File(CSV_PATH + "/" + formatDate(new Date(), "yyyy-MM-dd") + "_phone.csv");
		emailCsv = new File(CSV_PATH + "/" + formatDate(new Date(), "yyyy-MM-dd") + "_email.csv");
		qqCsv = new File(CSV_PATH + "/" + formatDate(new Date(), "yyyy-MM-dd") + "_qq.csv");

		phoneCw = new CsvWriter(new FileWriter(phoneCsv, true), ',');
		emailCw = new CsvWriter(new FileWriter(emailCsv, true), ',');
		qqCw = new CsvWriter(new FileWriter(qqCsv, true), ',');
	}

	public String formatDate(Date date,String format){
		if(date ==null) return "";
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		if (FILTERS.matcher(href).matches()) {
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
			Matcher phoneMatcher = phoneRegx.matcher(html);
			Matcher emailMatcher = EMAIL.matcher(html);
			Matcher qqMatcher = QQ.matcher(html);

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
