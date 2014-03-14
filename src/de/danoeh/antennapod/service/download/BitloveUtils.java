package de.danoeh.antennapod.service.download;

import android.content.Context;
import android.util.Log;
import de.danoeh.antennapod.AppConfig;
import de.danoeh.antennapod.feed.EnclosedFeedMedia;
import de.danoeh.antennapod.storage.DBWriter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Provides access to the bitlove.org API
 */
public class BitloveUtils {

    private static final String TAG = "BitloveUtils";

    private static final String BASE_SCHEME = "http";
    private static final String BASE_HOST = "api.bitlove.org";

    public static final String isAvailableOnBitlove(final Context context, EnclosedFeedMedia enclosedFeedMedia) {
        if (enclosedFeedMedia.getOriginalEnclosureLink() == null) {
            return null;
        }

        String result = null;
        try {
            URI queryUri = new URI(BASE_SCHEME, BASE_HOST, "/by-enclosure.json", "url=" + enclosedFeedMedia.getDownload_url(), null);

            if (AppConfig.DEBUG) Log.d(TAG, "Query url: " + queryUri.toString());

            HttpClient client = AntennapodHttpClient.getHttpClient();
            HttpGet request = new HttpGet(queryUri);
            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                entity.writeTo(outputStream);
                String resultString = outputStream.toString();

                if (AppConfig.DEBUG) Log.d(TAG, "Response from bitlove: " + resultString);
                result = getTorrentUrlFromApiResponse(resultString, enclosedFeedMedia.getDownload_url());

            } else {
                Log.e(TAG, "Response from bitlove API was " + statusCode);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DBWriter.setBitloveAvailability(context, result != null, enclosedFeedMedia.getId());
        return result;

    }

    private static String getTorrentUrlFromApiResponse(String response, String enclosureUrl) throws JSONException {
        JSONObject obj = new JSONObject(response);
        if (obj.has(enclosureUrl)) {
            JSONObject encInfo = obj.getJSONObject(enclosureUrl);
            JSONArray sources = encInfo.getJSONArray("sources");
            if (sources.length() > 0) {
                return sources.getJSONObject(0).getString("torrent");
            }
        }
        return null;
    }
}
