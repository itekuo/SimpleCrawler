package policy;
import static org.junit.Assert.*;

import org.junit.Test;

import policy.LinkScanner;

/**
 * Test case for {@link LinkScanner}
 * 
 * @author ted.kuo
 *
 */
public class TestLinkScanner {

	@Test
	public void testScanePage_malformedLink() {
		// Then ignore the malformed link and carry on.
	}
	
	
	public void testScanPage_searchForAnchorTags() throws Exception {
		
		
		// Then all the href tag is found.
	}
	
	public void testScanPage_shouldAvoidImages() throws Exception {
		// ?
	}
	
	public void testScanPage_onlyReturnLinksWithTheRestrictedDomain() throws Exception {
		
	}

}
