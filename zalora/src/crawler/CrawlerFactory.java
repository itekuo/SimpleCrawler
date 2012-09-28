package crawler;
import page.HTMLPageRepository;
import policy.LinkScanner;

/**
 * A simple factory for creating all the collaborators to build a crawler
 * application.
 * 
 * @author ted.kuo
 * 
 */
public class CrawlerFactory {

	/**
	 * Creates a new instance of the {@link WebCrawler}.
	 * 
	 * @return a new {@link WebCrawler}.
	 */
	public static WebCrawler createNewWebCrawler() {
		HTMLPageRepository queue = new HTMLPageRepository();
		LinkScanner linkScanner = new LinkScanner("www.zalora.sg", queue);
		WebCrawler webCrawler = new WebCrawler(linkScanner, queue, 30);
		return webCrawler;
	}
}
