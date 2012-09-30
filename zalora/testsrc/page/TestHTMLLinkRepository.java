package page;
import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Test;

/**
 * Test case for URLQueue
 * 
 * @author ted.kuo
 *
 */
public class TestHTMLLinkRepository {

	@Test
	public void testInsert_isUniqueURL() throws Exception {
		// Given the URL already exist in the queue,
		HTMLLinkRepository htmlLinkQueue = new HTMLLinkRepository();
		htmlLinkQueue.insert(new HTMLLink(new URL("http://www.zalora.sg")));
		assertTrue(htmlLinkQueue.getNumberOfUnvisitedLinks() == 1);

		// When
		htmlLinkQueue.insert(new HTMLLink(new URL("http://www.zalora.sg/shoes/")));

		// Then
		assertTrue(htmlLinkQueue.getNumberOfUnvisitedLinks() == 2);	
	}

	@Test
	public void testInsert_URLAlreadyExists() throws Exception {
		// Given the URL already exist in the queue,
		HTMLLinkRepository linkQueue = new HTMLLinkRepository();
		linkQueue.insert(new HTMLLink(new URL("http://www.zalora.sg")));
		assertTrue(linkQueue.getNumberOfUnvisitedLinks() == 1);
		
		// When 
		linkQueue.insert(new HTMLLink(new URL("http://www.zalora.sg")));
		
		// Then
		assertTrue(linkQueue.getNumberOfUnvisitedLinks() == 1);
	}
	
	@Test
	public void testPollUnvisitedLinkQueue() throws Exception {
		// Given
		HTMLLinkRepository htmlLinkQueue = new HTMLLinkRepository();
		HTMLLink link1 = new HTMLLink(new URL("http://www.zalora.sg"));
		HTMLLink link2 = new HTMLLink(new URL("http://www.zalora.sg/shoes/"));
		htmlLinkQueue.insert(link1);
		htmlLinkQueue.insert(link2);
		assertEquals(2, htmlLinkQueue.getNumberOfUnvisitedLinks());
		assertEquals(2, htmlLinkQueue.getNumberOfLinksDiscovered());

		// When & Then
		HTMLLink actualLink1 = htmlLinkQueue.pollUnvisitedPageQueue();
		assertEquals(link1, actualLink1);
		assertEquals(1, htmlLinkQueue.getNumberOfUnvisitedLinks());
		
		HTMLLink actualLink2 = htmlLinkQueue.pollUnvisitedPageQueue();
		assertEquals(link2, actualLink2);
		assertEquals(0, htmlLinkQueue.getNumberOfUnvisitedLinks());

		assertNull(htmlLinkQueue.pollUnvisitedPageQueue());
		
	}
}
