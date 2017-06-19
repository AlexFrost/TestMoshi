package frost.alex.testmoshi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;

import okio.BufferedSource;
import okio.Okio;

import static org.assertj.core.api.Assertions.assertThat;


public class ConfigCreatorTest {

    Moshi moshi = new Moshi.Builder().build();

    Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test1MoshiAdapter() throws Exception {
        final InputStream stream = getTestInputStream("test.json");

        assertThat(stream).isNotNull();

        final long t0 = System.nanoTime();
        moshiAdapter("UAT", moshi, stream);
        final long t1 = System.nanoTime();

        System.out.println(String.format("Moshi adapter takes %d us", (t1 - t0) / 1000));
    }

    @Test
    public void testMoshiJsonValue() throws Exception {
        final InputStream stream = getTestInputStream("test.json");

        assertThat(stream).isNotNull();

        final long t0 = System.nanoTime();
        moshiJsonValue("UAT", moshi, stream);
        final long t1 = System.nanoTime();

        System.out.println(String.format("Moshi json value takes %d us", (t1 - t0) / 1000));
    }

    @Test
    public void testGsonJsonObject() throws Exception {
        final InputStream stream = getTestInputStream("test.json");

        assertThat(stream).isNotNull();

        final long t0 = System.nanoTime();
        gsonJsonObject("UAT", gson, stream);
        final long t1 = System.nanoTime();

        System.out.println(String.format("Gson json value takes %d us", (t1 - t0) / 1000));
    }

    private InputStream getTestInputStream(String fileName) throws IOException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("init-config/" + fileName);
        return inputStream;
    }

    private void moshiAdapter(String scope, Moshi moshi, InputStream stream) {
        try {
            BufferedSource source = Okio.buffer(Okio.source(stream));
            final JsonReader jsonReader = JsonReader.of(source);
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                final String name = jsonReader.nextName();
                if (scope.equals(name)) {
                    Type mapType = Types.newParameterizedType(Map.class, String.class, Object.class);
                    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapType);
                    final Map<String, Object> map = adapter.fromJson(jsonReader);
                    break;
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moshiJsonValue(String scope, Moshi moshi, InputStream stream) {
        try {
            BufferedSource source = Okio.buffer(Okio.source(stream));
            final JsonReader jsonReader = JsonReader.of(source);
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                final String name = jsonReader.nextName();
                if (scope.equals(name)) {
                    final Map<String, Object> map = (Map<String, Object>) jsonReader.readJsonValue();
                    break;
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gsonJsonObject(String scope, Gson gson, InputStream stream) {
        try {
            final com.google.gson.stream.JsonReader jsonReader
                    = new com.google.gson.stream.JsonReader(new InputStreamReader(stream, "UTF-8"));
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                final String name = jsonReader.nextName();
                if (scope.equals(name)) {
                    JsonObject config = gson.fromJson(jsonReader, JsonObject.class);
                    break;
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}