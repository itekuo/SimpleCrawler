package crawler;

import java.net.MalformedURLException;
import java.net.URL;

import page.HTMLPageRepository;
import policy.LinkScanner;
import price.PriceAnalyzer;
import price.PriceScanner;

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
		double minPrice = 20;
		double maxPrice = 2000;
		
		URL rootURL = null;
		try {
			rootURL = new URL(url);
		} catch (MalformedURLException e) {
			System.err.println("The given URL is malformed.");
		}

		PriceAnalyzer priceAnalyzer = new PriceAnalyzer(new PriceScanner(), minPrice, maxPrice);

		HTMLPageRepository htmlPageRepository = new HTMLPageRepository();
		LinkScanner linkScanner = new LinkScanner(rootURL.getHost(), htmlPageRepository);
		WebCrawler webCrawler = new WebCrawler(linkScanner, htmlPageRepository, 50, priceAnalyzer);
		
		webCrawler.initialiseCrawlers();
		webCrawler.crawl(rootURL);
	}
}
