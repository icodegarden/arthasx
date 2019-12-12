package xff.arthasx.sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class SampleApp {

	public static void main(String[] args) {
		System.out.println("start " + SampleApp.class.getSimpleName());
		Thread thread = new Thread("sample-thread") {
			@Override
			public void run() {
				for (;;) {
					try {
						Thread.sleep(5000);

						URL url = new URL("https://www.baidu.com");
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						connection.setRequestMethod("GET");
						connection.setConnectTimeout(3000);
						connection.setReadTimeout(3000);
						connection.connect();
						int responseCode = connection.getResponseCode();
						if (responseCode == HttpURLConnection.HTTP_OK) {
							BufferedReader reader = null;
							try {
								reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
								String line;
								while ((line = reader.readLine()) != null) {
									//
								}
							} finally {
								if (reader != null) {
									reader.close();
								}
							}
						}
					} catch (Exception e) {
					}
				}
			}
		};
		thread.start();
	}
}
