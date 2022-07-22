import georglider.mcrpc.discord.KnownServers;
import org.junit.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotSame;

public class Tests {

    @Test
    public void http() throws ExecutionException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://eu.mc-api.net/v3/server/favicon/" + "null.bo"))
                .build();
        CompletableFuture<HttpResponse<String>> httpResponseCompletableFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> stringHttpResponse = httpResponseCompletableFuture.get();
        // assertEquals(stringHttpResponse.statusCode(), 200);
        assertNotSame(stringHttpResponse.statusCode(), 200);
    }

    @Test
    public void random() {
        Random r = new Random();

        for (int i = 0; i < 15000; i++) {
            int i1 = r.nextInt(100);
            assertNotSame(i1, 100);
        }
    }

    @Test
    public void address() {
        // var address = "mc.hypixel.net";
        var address = "play.cubecraft.net";

        String[] split = address.split("\\.");
        List<String> collect = Arrays.stream(split)
                .filter(s -> !s.equals("mc"))
                .filter(s -> !s.equals("play"))
                .map(String::toLowerCase).collect(Collectors.toList());
        collect.remove(collect.size() - 1);

        assertEquals(1, collect.size());
    }

    @Test
    public void Enum() {
        KnownServers hypixel = KnownServers.valueOf("HYPIXEL");

    }

}
