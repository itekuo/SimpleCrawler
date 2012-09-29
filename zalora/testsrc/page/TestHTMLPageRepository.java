package page;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.junit.Test;

/**
 * Test case for URLQueue
 * 
 * @author ted.kuo
 *
 */
public class TestHTMLPageRepository {

	@Test
	public void testInsert_isUniqueURL() throws Exception {
		// Given the URL already exist in the queue,
		HTMLLinkRepository htmlPageQueue = new HTMLLinkRepository();
		htmlPageQueue.insert(new HTMLLink(new URL("http://www.zalora.sg")));
		assertTrue(htmlPageQueue.getNumberOfUnvisitedLinks() == 1);

		// When
		htmlPageQueue.insert(new HTMLLink(new URL("http://www.zalora.sg/shoes/")));

		// Then
		assertTrue(htmlPageQueue.getNumberOfUnvisitedLinks() == 2);	
	}

	@Test
	public void testInsert_URLAlreadyExists() throws Exception {
		// Given the URL already exist in the queue,
		HTMLLinkRepository urlQueue = new HTMLLinkRepository();
		urlQueue.insert(new HTMLLink(new URL("http://www.zalora.sg")));
		assertTrue(urlQueue.getNumberOfUnvisitedLinks() == 1);
		
		// When 
		urlQueue.insert(new HTMLLink(new URL("http://www.zalora.sg")));
		
		// Then
		assertTrue(urlQueue.getNumberOfUnvisitedLinks() == 1);
	}
}
