package Components.Repository;

import Components.Service.RespSerializer;

import Components.Store;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(classes = AppConfig.class)
class StoreTest {
    @Autowired
    public Store store;
    @Autowired
    public RespSerializer respSerializer;

    @Test
    public void testSetAndGetKey() {
        String key = "testKey";
        String value = "testValue";
        String result = store.set(key, value);
        assertEquals("+OK\r\n", result);

        String retrievedValue = store.get(key, value);
        assertEquals(respSerializer.serializeBulkString(value), retrievedValue);
    }
    @Test
    public void testSetWithExpiry() throws InterruptedException {
        String key = "testkey";
        String value = "testValue";
        String value2 = "testValue2";
        int expiryMilliseconds = 100;
        String setResult = store.set(key, value,expiryMilliseconds);
        String getResult = store.get(key);
        String setResultReset = store.set(key, value2 , expiryMilliseconds);
        Thread.sleep((long)100);
        String getResultReset = store.get(key);
        assertEquals("+OK\r\n", setResult);
        assertEquals("+OK\r\n", setResultReset);
        assertEquals(respSerializer.serializeBulkString(value), getResult);
    }
    @Test
    public void testConcurrentlySetting() throws InterruptedException {
        List<CompletableFuture<Void>> l = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            CompletableFuture<Void> f = CompletableFuture.runAsync(() -> {
                for(int j = 0; j < 100; j++) {
                    store.set("key" + finalI, "value" );
                }
            });
            l.add(f);
            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(l.toArray(new CompletableFuture[0]));

                     }

          }
          @Test
    public void testSetAndGetKeyExpiryReset() throws InterruptedException {
        String key = "testKey";
        String value = "testValue";
        String value2 = "testValue2";
        int expiryMilliseconds = 100;

        String setResult = store.set(key, value, expiryMilliseconds);
        String getResult = store.get(key);
        Thread.sleep((long) 100);

        String getResultAfterExpiry = store.get(key);
        assertEquals("+OK\r\n", setResult);
        assertEquals(respSerializer.serializeBulkString(value), getResult);
        assertEquals("$-1\r\n", getResultAfterExpiry);
          }


}