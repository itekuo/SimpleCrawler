package crawler;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import page.HTMLLink;
import page.HTMLLinkRepository;
import policy.HTMLLinkScanner;
import policy.LinkScanner;
import policy.PageAnalyser;

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
	private HTMLLinkRepository htmlPageRepository;
	
	/**
	 * Scanner for scanning links in a document.
	 */
	private List<HTMLLinkScanner> linkScanners;
	
	/**
	 * crawlersQueue free crawlers that can be used.
	 */
	private Queue<PageCrawler> freeCrawlersPool;
	
	/**
	 * Number of crawlers kept in this pool.
	 */
	private final int numberOfCrawlers;
	
	/**
	 * {@link PageAnalyser}
	 */
	private List<PageAnalyser> pageAnalysers;
	
	/**
	 * Constructor.
	 * 
	 * @param linkScanners used by this {@link WebCrawler} to identify all the links.
	 * @param pageRepository storage of all the pages that have been discovered.
	 * @param numberOfCrawlers the number of crawler threads to create
	 * @param pageAnalyzer for analysing information on pages.
	 */
	public WebCrawler(List<HTMLLinkScanner> linkScanners, HTMLLinkRepository pageRepository, 
			int numberOfCrawlers, List<PageAnalyser> pageAnalysers) {
		this.htmlPageRepository = pageRepository;
		this.numberOfCrawlers = numberOfCrawlers;
		this.freeCrawlersPool = new ConcurrentLinkedQueue<>();
		this.linkScanners = new ArrayList<>(linkScanners);
		this.pageAnalysers = new ArrayList<>(pageAnalysers);
	}

	/**
	 * Initialise all the page crawlers, at present, starts 5 crawler threads to crawl through a page.
	 */
	public synchronized void initialiseCrawlers() {
		for (int i = 0; i < numberOfCrawlers; i++) {
			PageCrawler newCrawler = new PageCrawler(this.htmlPageRepository, this.linkScanners, 
					this.freeCrawlersPool, this.pageAnalysers);
			newCrawler.start();
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
		System.out.println("Crawling " + rootURL.toString());
		this.htmlPageRepository.insert(new HTMLLink(rootURL));
		
		/*
		 * Iterate through the web pages using the Breadth-first search approach.
		 * Keep distributing tasks while there are page to visit. Only stop if the
		 * page queue is empty and all crawlers have finished.
		 */ 
		while(true) {
			if (!this.freeCrawlersPool.isEmpty() && !this.htmlPageRepository.isAllLinksVisited()) {
				this.freeCrawlersPool.poll().startCrawling(this.htmlPageRepository.pollUnvisitedPageQueue());
			}

			if (this.htmlPageRepository.isAllLinksVisited()) {
				if (this.freeCrawlersPool.size() == this.numberOfCrawlers) {
					break;
				}
			}
		}
		System.out.println("Finished Crawling" + rootURL.toString());
		System.out.println("Found " + this.htmlPageRepository.getNumberOfLinksDiscovered() + " links");
	}
}
