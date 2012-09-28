package crawler;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import page.HTMLPage;
import page.HTMLPageRepository;
import policy.LinkScanner;

/**
 * This {@link WebCrawler} crawls through the given URL.
 * 
 * @author ted.kuo
 *
 */
public class WebCrawler {

	/**
	 * Specifies a queue of the links still to be crawled through.
	 */
	private HTMLPageRepository htmlPageRepository;
	
	/**
	 * Scanner for scanning links in a document.
	 */
	private LinkScanner linkScanner;
	
	/**
	 * crawlersQueue free crawlers that can be used.
	 */
	private Queue<PageCrawler> crawlersPool;
	
	/**
	 * Number of crawlers kept in this pool.
	 */
	private final int numberOfCrawlers;
	
	/**
	 * Constructor.
	 * 
	 * @param linkScanner used by this {@link WebCrawler} to identify all the links.
	 */
	public WebCrawler(LinkScanner linkScanner, HTMLPageRepository queue, int numberOfCrawlers) {
		this.htmlPageRepository = queue;
		this.linkScanner = linkScanner;
		this.numberOfCrawlers = numberOfCrawlers;
		this.crawlersPool = new ConcurrentLinkedQueue<>();
	}
	
	/**
	 * Initialise all the page crawlers, at present, starts 5 crawler threads to crawl through a page.
	 */
	public synchronized void initialiseCrawlers() {
		for (int i = 0; i < numberOfCrawlers; i++) {
			PageCrawler newCrawler = new PageCrawler(this.htmlPageRepository, this.linkScanner, this.crawlersPool);
			newCrawler.start();
			this.crawlersPool.add(newCrawler);
		}
	}
	
	/**
	 * This recursively crawls through the URLs that are found through the pages,
	 * and only stop until reach the end. It needs to check for uniqueness of each
	 * URL to avoid an endless loop.
	 * 
	 * @param rootURL under which all the web pages are crawled. 
	 */
	public void crawl(URL rootURL) {
		this.htmlPageRepository.insert(new HTMLPage(rootURL));
		
		/*
		 * Iterate through the web pages using the Breadth-first search approach.
		 * Keep distributing tasks while there are page to visit. Only stop if the
		 * page queue is empty and all crawlers have finished.
		 */ 
		while(!this.htmlPageRepository.isAllPagesVisited() || this.crawlersPool.size() != this.numberOfCrawlers) {
			if (!this.crawlersPool.isEmpty() && !this.htmlPageRepository.isAllPagesVisited()) {
				
				this.crawlersPool.poll().startCrawling(this.htmlPageRepository.pollForUnvisitedPage());
			}
			
		}
		System.out.println(this.htmlPageRepository.getNumberOfPagesDiscovered());
		System.out.println("Page Load Time: " + this.linkScanner.getPageFetchingTimer().getTotalDurationInSeconds());
		System.out.println("Scanning Time: " + this.linkScanner.getScanningTimer().getTotalDurationInSeconds());
		System.out.println("Queue Time: " + this.htmlPageRepository.getQueueTimer().getTotalDurationInSeconds());
	}
		
}
