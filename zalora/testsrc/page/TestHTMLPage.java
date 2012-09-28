package page;
import java.net.URL;

import static org.junit.Assert.*;
import org.junit.Test;

import page.HTMLPage;


public class TestHTMLPage {

	@Test
	public void testGetPage() throws Exception {
		// Given
		URL rootURL = new URL("http://www.zalora.sg");
		
		// When 
		
		// Then
	}
	
	@Test
	public void testGetPage_networkIsDown() throws Exception {
		// Given that the network is down.
		
		// When
		
		// Then the getPage should simply return null document.
	}
	
	@Test
	public void testGetCanonicalPageURLString_pageWithTrailingSlashes() throws Exception {
		// Given
		HTMLPage page = new HTMLPage(new URL("http://www.singapore.sg/"));
		
		// When
		String canonicalURLString = page.getCanonicalPageURLString();
		
		// Then
		assertEquals("http://www.singapore.sg", canonicalURLString);
	}
	
	@Test
	public void testGetCanonicalPageURLString_pageWithFragments() throws Exception {
		// Given
		HTMLPage page = new HTMLPage(new URL("http://www.zalora.sg/3-in-1-Quarter-Socks-43227.html#brandInformation"));

		// When
		String canonicalURLString = page.getCanonicalPageURLString();

		// Then
		assertEquals("http://www.zalora.sg/3-in-1-Quarter-Socks-43227.html", canonicalURLString);
	}
	
	@Test
	public void testGetCanonicalPageURLString_normalRootPage() throws Exception {
		// Given
		HTMLPage page = new HTMLPage(
				new URL("http://www.zalora.sg/Aroma-Gardener-Antibac-Flow-Soap-%2F-300ml-43890.html"));

		// When
		String canonicalURLString = page.getCanonicalPageURLString();

		// Then
		assertEquals("http://www.zalora.sg/Aroma-Gardener-Antibac-Flow-Soap-%2F-300ml-43890.html", canonicalURLString);
	}

}
