package page;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test case for URL Params.
 * 
 * @author ted.kuo
 */
public class TestURLParams {

	@Test
	public void test() {
		
		// Check abc=
		
		// Check abc no equals
		
		String s = "abc=";
		String[] split = s.split("=");
		System.out.println(split.length);
		System.out.println(split[0]);
		System.out.println(split[1]);
	}

	
	
}
