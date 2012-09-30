package policy;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import java.net.URL;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import page.HTMLLink;
import page.HTMLLinkRepository;

/**
 * Test-case for {@link LinkScanner}
 * 
 * @author ted.kuo
 */
public class TestLinkScanner {

	@Mock HTMLLinkRepository repo;
	
	/**
	 * Object in test
	 */
	LinkScanner linkScanner;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.linkScanner = new LinkScanner("www.zalora.sg", this.repo);
	}
	
	@Test
	public void testScanePage_malformedLink() {
		
		// Then ignore the malformed link and carry on.
	}
	
	@Test
	public void testScanPage_onlyFindLinksUnderTheSameHost() throws Exception {
		// Given
		HTMLLink link = new HTMLLink(new URL("http://zalora.sg/shoes"));
		Document content = Jsoup.parse("<h4 class=\"ui-borderBottom pbs\"><a href=\"/women/new-arrivals/\">Women</a></h4>" +
		"<a href=\"http://cosmopolitan.sg/2012/09/19/for-your-surfing-pleasure/\"><span style=\"margin:6px\">" +
		"<img src=\"http://d1iv60t95n7wx3.cloudfront.net/cms/icon/cosmopolitan.png\"  width=\"80\" alt=\"\" " +
		" title=\"Best Fashion Site\" /></span></a>");
		
		// When
		List<HTMLLink> linksFound = this.linkScanner.scanPage(link, content);
		
		// Then all the href tag is found.
		assertEquals(1, linksFound.size());
		assertEquals("http://zalora.sg/shoes/women/new-arrivals/", linksFound.get(0).toString());
	}
	
	@Test
	public void testScanPage_avoidZaloraAccountSpecificLinks() throws Exception {
		// Given
		HTMLLink link = new HTMLLink(new URL("http://zalora.sg/shoes"));
		Document content = Jsoup.parse("<a class=\"ui-block icon i-recommend mbs\" id=\"send-to-a-friend-link\" title=\"Send to a friend\" href=\"/sendfriend/index/sku/GL262AA90YJX\">" + 
				"<a class=\"\" href=\"/customer/wishlist\" title=\"Wishlist\" rel=\"follow\">My Wishlist</a>");
		
		// When
		List<HTMLLink> linksFound = this.linkScanner.scanPage(link, content);
		
		// Then all the href tag is found.
		assertEquals(0, linksFound.size());
	}
}
