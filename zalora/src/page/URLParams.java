package page;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This parses the parameters in a URL, and enable easy access to the parameters.
 * 
 * @author ted.kuo
 *
 */
public class URLParams {
	
	/**
	 * A map of the parameters of a URL.
	 */
	private Map<String, String> parameterEntryValue;
	
	/**
	 * Constructor. Extracts the parameters out of the URL automatically.
	 * 
	 * @param url the url to have parameters extracted for.
	 */
	public URLParams(URL url) {
		this.parameterEntryValue = new HashMap<>();
		parse(url.getQuery());
	}
	
	/**
	 * Parses the given parameters string
	 * 
	 * @param parametersString to be parsed
	 */
	private void parse(String parametersString) {
		// Validity of the parameter check.
		if (parametersString == null || parametersString.isEmpty()) {
			return;
		}
		
		String[] parameterArray = parametersString.trim().split("&");
		for (String string : parameterArray) {
			String[] entryValue = string.split("=");
			if (entryValue.length >= 1) {
				String parameterValue = "";
				if (entryValue.length == 2) {
					parameterValue = entryValue[1];
				}
				this.parameterEntryValue.put(entryValue[0], parameterValue);
			}
		}
	}
	
	/**
	 * Returns true if and only if the given list of keys is exactly what is contained in this URLParams.
	 * 
	 * @param keys to compare
	 * @return true if the given list of keys is exactly what is contained in this URLParams.
	 */
	public Set<String> getKeySet() {
		return this.parameterEntryValue.keySet();
	}
}
