package crawler;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class represents the application that runs the web crawler to crawl
 * through the Zalora website for price errors.
 * 
 * @author ted.kuo
 */
public class WebCrawlerApplication {

	/**
	 * The main class to trigger the application
	 * 
	 * @param args argument to this console application
	 */
	public static void main(String[] args) {
		
		String url = "http://www.zalora.sg";
		// double minPrice = 20;
		// double maxPrice = 2000;
		
		URL rootURL = null;
		try {
			rootURL = new URL(url);
		} catch (MalformedURLException e) {
			System.err.println("The given URL is malformed.");
		}
		
		WebCrawler webCrawler = CrawlerFactory.createNewWebCrawler();
		webCrawler.initialiseCrawlers();
		webCrawler.crawl(rootURL/*, specification of the range*/);
	}
}
