package page;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Represents a HTML link identified by it's URL. 
 * 
 * @author ted.kuo
 */
public class HTMLLink {

	/**
	 * Specifies the URL this HTML represents
	 */
	private URL linkURL;

	/**
	 * Constructor
	 * 
	 * @param url for which this {@link HTMLLink} represents.
	 */
	public HTMLLink(URL url) {
		if (url == null) {
			throw new IllegalArgumentException("the URL cannot be null");
		}
		try {
			// Reconstruct the URL to store in its simplest form
			this.linkURL = new URL(getCanonicalPageURL(url));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("URL is malformed: " + url.toString());
		}
	}
	
	/**
	 * @return the pageURL
	 */
	public URL getPageURL() {
		return linkURL;
	}

	/**
	 * Using the URL of this page, it retrieves it's content, and then create a
	 * document that represents the content of the page.
	 * 
	 * NOTE: If an error, the page that is valid but somehow conneection went out,
	 * it should go back to the unvisited queue.
	 * 
	 * @return the Document that represents the content of the given URL. null if
	 *         an error is encountered while reading the stream.
	 */
	public Document getContent() throws IOException {
		Document pageContent = null;
		BufferedReader contentReader = null;
		try {
			URLConnection newConnection = linkURL.openConnection();
			contentReader = new BufferedReader(new InputStreamReader(newConnection.getInputStream()));

			StringBuilder htmlPageBuilder = new StringBuilder();
			String line = "";

			// Concatenate the page content into 1 single string. New-line character is not preserved.
			while ((line = contentReader.readLine()) != null) {
				htmlPageBuilder.append(line);
			}
			pageContent = Jsoup.parse(htmlPageBuilder.toString());

		}
		finally {
			if (contentReader != null) {
				contentReader.close();
			}
		}
		return pageContent;
	}
	
	/**
	 * Returns the "canonical" URL string represented by this page.
	 * 
	 * @return the "canonical" URL string
	 */
	public String getCanonicalPageURLString() {
		return getCanonicalPageURL(linkURL);
	}
	
	/**
	 * Return the canonical form of the given URL in string.
	 * 
	 * @param url to be normalised.
	 * @return the normalised form of the given URL in string.
	 */
	public String getCanonicalPageURL(URL url) {
		// Remove the trailing slash if it exists
		String newURL = url.toString();
		if (newURL.endsWith("/")) {
			newURL = newURL.substring(0, newURL.length() - 1);
		}
		
		// Remove the trailing fragment
		String[] urlSplitByFragment = newURL.split("#");
		if (urlSplitByFragment.length >= 1) {
			newURL = urlSplitByFragment[0];
		}
		return newURL;
	}

	/** 
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.getCanonicalPageURLString().hashCode();
	}
	
	/**
	 * Returns true if equals
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !obj.getClass().isAssignableFrom(HTMLLink.class)) {
			return false;
		}
		
		HTMLLink otherPage = (HTMLLink) obj;
		
		return this.getCanonicalPageURLString().equals(otherPage.getCanonicalPageURLString());
	}
}
