import redis.clients.jedis.Jedis;

public class App {

    public static void main(String[] args) {

        Jedis jedis = new Jedis("localhost");

        var setName = "realmset-dev-msusers";
        jedis.del(setName);

        for (int i = 0; i < 15; i++) {
            var t = System.currentTimeMillis();

            System.out.println("Adding: " + i);

            jedis.zadd(setName, t, Integer.toString(i));

            sleep(1000);

            while (true) {
                var size = jedis.zcount(setName, "-inf", "+inf");
                if (size <= 10)
                    break;
                var value = jedis.zpopmin(setName);
                System.out.println("Popped value: " + value.getElement());
            }
        }

        var allValues = jedis.zrangeByScore(setName, "-inf", "+inf");
        allValues.forEach(System.out::println);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }
}
