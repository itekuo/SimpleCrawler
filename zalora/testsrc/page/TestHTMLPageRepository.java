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
		HTMLPageRepository htmlPageQueue = new HTMLPageRepository();
		htmlPageQueue.insert(new HTMLPage(new URL("http://www.zalora.sg")));
		assertTrue(htmlPageQueue.getNumberOfUnvisitedPages() == 1);

		// When
		htmlPageQueue.insert(new HTMLPage(new URL("http://www.zalora.sg/shoes/")));

		// Then
		assertTrue(htmlPageQueue.getNumberOfUnvisitedPages() == 2);	
	}

	@Test
	public void testInsert_URLAlreadyExists() throws Exception {
		// Given the URL already exist in the queue,
		HTMLPageRepository urlQueue = new HTMLPageRepository();
		urlQueue.insert(new HTMLPage(new URL("http://www.zalora.sg")));
		assertTrue(urlQueue.getNumberOfUnvisitedPages() == 1);
		
		// When 
		urlQueue.insert(new HTMLPage(new URL("http://www.zalora.sg")));
		
		// Then
		assertTrue(urlQueue.getNumberOfUnvisitedPages() == 1);
	}
}
