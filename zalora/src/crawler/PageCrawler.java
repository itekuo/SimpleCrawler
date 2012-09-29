/**
 * 
 */
package crawler;

import java.util.Collection;
import java.util.Queue;

import org.jsoup.nodes.Document;

import page.HTMLPage;
import page.HTMLPageRepository;
import policy.ContentScanner;
import price.PriceAnalyzer;

/**
 * This defines a page crawler, which crawls through the given URL link. Links
 * found on the page are added to its {@link HTMLPageRepository}.
 * 
 * @author ted.kuo
 * 
 */
public class PageCrawler extends Thread {

	/**
	 * Specifies the queue this {@link PageCrawler} interacts with to insert all the links found in a page
	 */
	private HTMLPageRepository htmlPageQueue;
	
	/**
	 * Scanner for scanning the links on a page.
	 */
	private ContentScanner<HTMLPage> linkScanner;
	
	/**
	 * Specifies the page to crawl, null when there is nothing to crawl.
	 */
	private HTMLPage pageToCrawl;
	
	/**
	 * Specifies the role that analyses a page for prices.
	 */
	private PriceAnalyzer priceAnalyzer;
	
	/**
	 * Specifies the queue which this crawler should return once its done.
	 */
	private Queue<PageCrawler> crawlersQueue;
	
	/**
	 * Constructor 
	 * 
	 * @param queue which this {@link PageCrawler} interacts to insert all the links found in a page.
	 * @param linkScanner for scanning links in a page
	 * @param crawlersPool the home of these crawlers, it should add itself back to the pool once is done with crawling of a page.
	 * @param priceAnalyzer for analysing prices in a page.
	 */
	public PageCrawler(HTMLPageRepository queue, ContentScanner<HTMLPage> linkScanner, Queue<PageCrawler> crawlersPool, PriceAnalyzer priceAnalyzer) {
		super();
		this.htmlPageQueue = queue;
		this.linkScanner = linkScanner;
		this.crawlersQueue = crawlersPool;
		this.priceAnalyzer = priceAnalyzer;
	}

	/**
	 * Return true if this PageCrawler is still crawling a page that was given to it.
	 * 
	 * @return true if this {@link PageCrawler} still has something to do.
	 */
	public boolean isCrawling() {
		synchronized (this) {
			return this.pageToCrawl != null;
		}
	}
	
	/**
	 * Starts the crawling and set the busy signal to true.
	 */
	public void startCrawling(HTMLPage page) {
		synchronized (this) {
			this.pageToCrawl = page;
			this.notifyAll();
		}
	}

	/**
	 * Stops the crawling and make itself available in the crawler's queue.
	 */
	private void stopCrawling() {
		synchronized (this) {
			this.pageToCrawl = null;
			this.crawlersQueue.add(this);
		}
	}
	
	/**
	 * Run to crawl the given page as set by
	 * {@link PageCrawler#startCrawling(HTMLPage)}. Once its done, it does nothing
	 * until another page is set for it to crawl.
	 */
	@Override
	public void run() {
		while (true) {
			synchronized (this) {
				if (this.pageToCrawl != null) {
					try {
						Document pageContent = this.pageToCrawl.getContent();
						Collection<HTMLPage> linksFound = this.linkScanner.scanPage(this.pageToCrawl, pageContent);
						this.htmlPageQueue.insert(linksFound);

						this.priceAnalyzer.analyse(this.pageToCrawl, pageContent);

						stopCrawling();			
					}
					catch (Exception e) {
						System.err.println("Error:" + e.getMessage() + " " + e.getStackTrace());
					}
				}
				else {
					// Wait on itself until the WebCrawler gave it another page to crawl.
					try {
						this.wait();
					} catch (InterruptedException e) {
						System.err.println("Error" + e.getMessage() + " " + e.getStackTrace());
						// If interrupted, simply continue.
					}
				}
		  }
		}
	}
}
