package page;
import java.net.URL;

import static org.junit.Assert.*;
import org.junit.Test;

import page.HTMLLink;

/**
 * Test-case for {@link HTMLLink}
 * 
 * @author ted.kuo
 */
public class TestHTMLLink {

	@Test
	public void testGetCanonicalPageURLString_pageWithTrailingSlashes() throws Exception {
		// Given
		HTMLLink page = new HTMLLink(new URL("http://www.singapore.sg/"));
		
		// When
		String canonicalURLString = page.getCanonicalPageURLString();
		
		// Then
		assertEquals("http://www.singapore.sg", canonicalURLString);
	}
	
	@Test
	public void testGetCanonicalPageURLString_pageWithFragments() throws Exception {
		// Given
		HTMLLink page = new HTMLLink(new URL("http://www.zalora.sg/3-in-1-Quarter-Socks-43227.html#brandInformation"));

		// When
		String canonicalURLString = page.getCanonicalPageURLString();

		// Then
		assertEquals("http://www.zalora.sg/3-in-1-Quarter-Socks-43227.html", canonicalURLString);
	}
	
	@Test
	public void testGetCanonicalPageURLString_normalRootPage() throws Exception {
		// Given
		HTMLLink page = new HTMLLink(
				new URL("http://www.zalora.sg/Aroma-Gardener-Antibac-Flow-Soap-%2F-300ml-43890.html"));

		// When
		String canonicalURLString = page.getCanonicalPageURLString();

		// Then
		assertEquals("http://www.zalora.sg/Aroma-Gardener-Antibac-Flow-Soap-%2F-300ml-43890.html", canonicalURLString);
	}
}
