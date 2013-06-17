package pl.mbassara.location.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Helper class which provides simple interface for HTTP requests
 * 
 * @author maciek
 */
public abstract class HTTPHelper {

	public static enum Method {
		POST, GET
	}

	private static String contentType = "application/x-www-form-urlencoded";

	/**
	 * Send http request
	 * 
	 * @param url
	 *            the URL
	 * @param method
	 *            http request method: POST or GET
	 * @param data
	 *            data which will be send to server as request body
	 * @param timeoutMillis
	 *            time in millis after which request will be cancelled and
	 *            exception will be thrown
	 * @return XML containing server's response
	 * @throws TimeoutException
	 *             if the timeout has occurred for this request
	 */
	public static String sendRequest(String url, Method method, Map<String, String> data,
			long timeoutMillis) throws TimeoutException {

		long begTime = System.currentTimeMillis();

		HTTPRequestThread thread = new HTTPRequestThread(url, method, contentType,
				urlEncodeData(data));
		thread.start();

		while (!thread.isResultReady()) {
			if (System.currentTimeMillis() - begTime > timeoutMillis) {
				thread.cancel();
				throw new TimeoutException("Http POST request timeout (" + timeoutMillis + " ms)");
			}

			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return thread.getResponse();
	}

	private static String urlEncodeData(Map<String, String> data) {
		String urlEncodedData = "";
		for (String paramName : data.keySet())
			try {
				urlEncodedData += "&" + URLEncoder.encode(paramName, "UTF-8") + "="
						+ URLEncoder.encode(data.get(paramName), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		if (urlEncodedData.length() > 0)
			return urlEncodedData.substring(1);
		else
			return "";
	}

	private static class HTTPRequestThread extends Thread {

		private String url, contentType, urlEncodedData;
		private Method method;
		private boolean resultReady = false;
		private StringBuilder result = new StringBuilder();
		private HttpURLConnection urlConnection;
		private OutputStreamWriter out;
		private BufferedReader in;

		public HTTPRequestThread(String url, Method method, String contentType,
				String urlEncodedData) {
			setName("HTTPRequestThread");
			this.url = url;
			this.method = method;
			this.contentType = contentType;
			this.urlEncodedData = urlEncodedData;
		}

		@Override
		public void run() {
			try {

				urlConnection = (HttpURLConnection) (new URL(url + "?" + urlEncodedData)
						.openConnection());
				urlConnection.setDoOutput(true);
				urlConnection.setRequestMethod(method.toString());
				urlConnection.setRequestProperty("content-type", contentType);
				urlConnection.setRequestProperty("accept", "text/plain");
				urlConnection.connect();

				in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),
						"UTF-8"));
				String tmp;
				while ((tmp = in.readLine()) != null)
					result.append(tmp);

				in.close();
				urlConnection.disconnect();

				resultReady = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void cancel() {
			try {
				if (urlConnection != null)
					urlConnection.disconnect();
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public boolean isResultReady() {
			return resultReady;
		}

		public String getResponse() {
			return result.toString();
		}
	}
}
