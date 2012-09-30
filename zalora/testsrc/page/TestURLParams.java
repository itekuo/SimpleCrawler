package page;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Arrays;
import java.util.Set;

import org.junit.Test;

/**
 * Test case for URL Params.
 * 
 * @author ted.kuo
 */
public class TestURLParams {

	@Test
	public void testParameterParsing() throws Exception {
		// Given
		URLParams paramsURL = new URLParams(new URL("http://www.zalora.sg/shoes?gender=male&sort=popularity&from=front-banner"));
		
		// When 
		Set<String> parameterKeys = paramsURL.getKeySet();
		
		// Then
		assertEquals(3, parameterKeys.size());
		assertTrue(parameterKeys.containsAll(Arrays.asList("gender", "sort", "from")));
	}
	
	@Test
	public void testParameterParsing_noParameter() throws Exception {
		// Given
		URLParams paramsURL = new URLParams(new URL("http://www.zalora.sg/shoes"));
			
		// When 
		Set<String> parameterKeys = paramsURL.getKeySet();
		
		// Then
		assertEquals(0, parameterKeys.size());
	}
	
	@Test
	public void testParameterParsing_emptyParameter() throws Exception {
		// Given
		URLParams paramsURL = new URLParams(new URL("http://www.zalora.sg/shoes?gender=&sort=popularity"));
			
		// When 
		Set<String> parameterKeys = paramsURL.getKeySet();

		// Then
		assertEquals(2, parameterKeys.size());
		assertTrue(parameterKeys.containsAll(Arrays.asList("gender", "sort")));
	}
}
