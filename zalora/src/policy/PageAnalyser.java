/**
 * 
 */
package policy;

import org.jsoup.nodes.Document;

import crawler.PageCrawler;

import page.HTMLLink;

/**
 * This specifies a {@link PageAnalyser} which analyses a page that is being
 * crawled by a {@link PageCrawler}. It gives {@link PageAnalyser} a change to
 * extract information from the page being crawled.
 * 
 * @author ted.kuo
 */
public interface PageAnalyser {

	/**
	 * Given a link and its content, it gives a {@link PageAnalyser} opportunity
	 * to analyses the given page for information.
	 * 
	 * @param link for which the page content is analysed 
	 * @param linkContent of the link to be analysed.
	 */
	void analyse(HTMLLink link, Document linkContent);
}
