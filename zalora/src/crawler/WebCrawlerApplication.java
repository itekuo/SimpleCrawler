package crawler;

import java.net.MalformedURLException;
import java.net.URL;

import page.HTMLLinkRepository;
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
		
		/* It's assumed that price is stated in the same currency to what the url
		 * site uses, and the site only uses 1 currency.
		 */
		double minPrice = 20; 
		double maxPrice = 2000;
		
		URL rootURL = null;
		try {
			rootURL = new URL(url);
		} catch (MalformedURLException e) {
			System.err.println("The given URL is malformed.");
		}

		PriceAnalyzer priceAnalyzer = new PriceAnalyzer(new PriceScanner(), minPrice, maxPrice);

		HTMLLinkRepository htmlPageRepository = new HTMLLinkRepository();
		LinkScanner linkScanner = new LinkScanner(rootURL.getHost(), htmlPageRepository);
		WebCrawler webCrawler = new WebCrawler(linkScanner, htmlPageRepository, 50, priceAnalyzer);
		
		webCrawler.initialiseCrawlers();
		webCrawler.crawl(rootURL);
	}
}
