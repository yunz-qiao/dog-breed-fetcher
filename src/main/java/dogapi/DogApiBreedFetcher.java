package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // Build the API URL
        String url = "https://dog.ceo/api/breed/" + breed + "/list";

        // Create the HTTP request
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            // Check if response body is null
            if (response.body() == null) {
                throw new BreedNotFoundException(breed);
            }

            // Parse the JSON response
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);

            // Check the status field
            String status = jsonObject.getString("status");

            if ("success".equals(status)) {
                // Extract the message array (list of sub breeds)
                JSONArray messageArray = jsonObject.getJSONArray("message");
                List<String> subBreeds = new ArrayList<>();
                for (int i = 0; i < messageArray.length(); i++) {
                    subBreeds.add(messageArray.getString(i));
                }
                return subBreeds;
            } else {
                // Status is "error", throw BreedNotFoundException
                throw new BreedNotFoundException(breed);
            }
        } catch (Exception e) {
            // Any errors (IO, JSON parsing, etc.) are reported as BreedNotFoundException
            throw new BreedNotFoundException(breed);
        }
    }
}