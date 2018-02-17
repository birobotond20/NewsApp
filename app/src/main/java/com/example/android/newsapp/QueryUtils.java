package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper methods related to requesting and receiving news from Guardian.
 */

public final class QueryUtils {

    /**
     * Tag for the LOG messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl){

        // Create URL object
        URL url = createUrl(requestUrl);

        String jsonResponse = null;

        // Perform HTTP request to the URL and receive a JSON response back
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e){
            Log.e(LOG_TAG, "Problem making the HTTP request: ", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}
        // objects
        List<News> news = extractFeatureFromJson(jsonResponse);

        // Return the {@link List<News>}
        return news;
    }

    /**
     * Returns a new URL object from the given string URL
     */
    private static URL createUrl(String stringUrl){
        URL url = null;
        try{
            url = new URL(stringUrl);
        } catch (MalformedURLException e){
            Log.e(LOG_TAG, "Error with creating url: ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";

        // If the url is null, then return early.
        if (url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else{
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode() + url);
            }
        } catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the news JSON response", e);
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies that an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();

        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = bufferedReader.readLine();
            while (line != null){
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON){

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)){
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<News> news = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        try{
            // Create a JSONObject from the bookJSON string
            JSONObject rootJsonObject = new JSONObject(newsJSON);

            // Extract the JSONObject associated with the key called "response",
            // which contains information about the result of the query, including the results
            JSONObject response = rootJsonObject.getJSONObject("response");

            // Get the number of results received. If it is 0 (zero), than return early, otherwise
            // continue parsing the JSON response
            int totalResults = response.optInt("total");

            if (totalResults != 0){
                JSONArray resultsArray = response.getJSONArray("results");

                // For each article in the resultsArray, create a {@link News} object
                for (int i = 0; i < resultsArray.length(); i++){

                    // Get a single news at position i within the list of news
                    JSONObject currentArticle = resultsArray.getJSONObject(i);

                    // Get the name of the section to which the article belongs to
                    String sectionName = currentArticle.getString("sectionName");

                    // Get the title of the article
                    String title = currentArticle.getString("webTitle");

                    // Get the url of the article
                    String webUrl = currentArticle.getString("webUrl");

                    // For a given article extract the object associated with the key "fields",
                    // which represents a list of additionally queried information, like author,
                    // date of first publication and url for the thumbnail
                    JSONObject fields = currentArticle.optJSONObject("fields");

                    // If "fields" is not empty, extract the information
                    String author = null;
                    String date = null;
                    String thumbnail = null;

                    if (fields != null) {
                        author = fields.optString("byline");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date newDate = simpleDateFormat.parse(fields.optString("firstPublicationDate"));
                        date = simpleDateFormat.format(newDate);
                        thumbnail = fields.optString("thumbnail");
                    }
                    // Create a new {@link News} object with the thumbnail, sectionName, title,
                    // author, date and webUrl from the JSON response.
                    News currentNews = new News(thumbnail, sectionName, title, author, date,
                            webUrl);

                    // Add the new {@link News} to the list of news.
                    news.add(currentNews);
                }
            } else return null;
        } catch (JSONException e){
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
        } catch (ParseException e) {
            Log.e("QueryUtils", "Error parsing simpleDateFormat", e);
        }

        // Return the list of books
        return news;
    }
}
