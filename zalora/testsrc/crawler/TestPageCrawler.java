package crawler;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.verification.NeverWantedButInvoked;

import page.HTMLLink;
import page.HTMLLinkRepository;
import policy.HTMLLinkScanner;
import policy.LinkScanner;
import policy.PageAnalyser;

/**
 * Test-Case for {@link PageCrawler}
 * 
 * @author ted.kuo
 *
 */
public class TestPageCrawler {

	@Mock HTMLLinkRepository linkRepository;
	@Mock HTMLLinkScanner linkScanner;
	@Mock Queue<PageCrawler> freeCrawlersPool;
	@Mock PageAnalyser priceAnalyser;
	@Mock HTMLLink htmlLink;
	@Mock Document docRetreived;
	
	/**
	 * Object in test
	 */
	PageCrawler pageCrawler;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.pageCrawler = new PageCrawler(linkRepository, Arrays.asList(linkScanner), freeCrawlersPool, Arrays.asList(priceAnalyser));
	}
	
	@Test
	public void testCrawl_secondAttemptAndSucceed() throws Exception {
		// Given a link to crawl
		this.pageCrawler.startCrawling(htmlLink);
		assertTrue(this.pageCrawler.hasPageToCrawl()); //pre-condition.
		
		stub(htmlLink.getContent()).toThrow(new ConnectException()).toReturn(docRetreived);
		List<HTMLLink> linksFound = Arrays.asList(new HTMLLink(new URL("http://google.com")));
		stub(linkScanner.scanPage(htmlLink, docRetreived)).toReturn(linksFound);
		
		// When 
		this.pageCrawler.crawl();
		
		// Then: not crawling and has added itself back to be available.
		assertFalse(this.pageCrawler.hasPageToCrawl());
		verify(this.linkRepository).insert(linksFound);
		verify(this.priceAnalyser).analyse(htmlLink, docRetreived);
	}

	@Test
	public void testStartCrawling() throws Exception {
		// When
		this.pageCrawler.startCrawling(htmlLink);
		
		// Then should become busy 
		assertTrue(this.pageCrawler.hasPageToCrawl());
	}
	
	public void testCrawl_attemptThreeTimeAndFail() throws Exception {
		// Given a link to crawl
		this.pageCrawler.startCrawling(htmlLink);
		assertTrue(this.pageCrawler.hasPageToCrawl()); // pre-condition.

		stub(htmlLink.getContent()).toThrow(new SocketException());

		// When
		this.pageCrawler.crawl();

		// Then: not crawling and has added itself back to be available.
		assertFalse(this.pageCrawler.hasPageToCrawl());
		verifyZeroInteractions(this.linkRepository);
		verifyZeroInteractions(this.priceAnalyser);
	}	
}
