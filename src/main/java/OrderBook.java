import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.InputStreamReader;

public class OrderBook {
    private final Map<Integer, Long> bids;
    private final Map<Integer, Long> asks;

    public OrderBook() {
        bids = new TreeMap<>(Collections.reverseOrder());
        asks = new TreeMap<>();
    }

    public void update(int price, long size, String type) {
        if (type.equals("bid")) {
            if (size == 0) {
                bids.remove(price);
            } else {
                bids.put(price, size);
            }
        } else {
            if (size == 0) {
                asks.remove(price);
            } else {
                asks.put(price, size);
            }
        }
    }

    public String getBestBid() {
        if (bids.isEmpty()) {
            return "NA";
        }
        Map.Entry<Integer, Long> bestBid = bids.entrySet().iterator().next();
        return bestBid.getKey() + "," + bestBid.getValue();
    }

    public String getBestAsk() {
        if (asks.isEmpty()) {
            return "NA";
        }
        Map.Entry<Integer, Long> bestAsk = asks.entrySet().iterator().next();
        return bestAsk.getKey() + "," + bestAsk.getValue();
    }

    public long getSizeAtPrice(int price) {
        if (bids.containsKey(price)) {
            return bids.get(price);
        } else if (asks.containsKey(price)) {
            return asks.get(price);
        } else {
            return 0;
        }
    }

    public void executeMarketOrder(String type, long size) {
        if (type.equals("buy")) {
            executeBuyMarketOrder(size);
        } else {
            executeSellMarketOrder(size);
        }
    }

    private void executeBuyMarketOrder(long size) {
        for (Iterator<Map.Entry<Integer, Long>> it = asks.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Long> entry = it.next();
            long entrySize = entry.getValue();
            if (entrySize <= size) {
                size -= entrySize;
                it.remove();
            } else {
                asks.put(entry.getKey(), entrySize - size);
                break;
            }
        }
    }

    private void executeSellMarketOrder(long size) {
        for (Iterator<Map.Entry<Integer, Long>> it = bids.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Long> entry = it.next();
            long entrySize = entry.getValue();
            if (entrySize <= size) {
                size -= entrySize;
                it.remove();
            } else {
                bids.put(entry.getKey(), entrySize - size);
                break;
            }
        }
    }

    public static void main(String[] args) {


        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(OrderBook.class.getResourceAsStream("./input.txt"))));
             FileWriter writer = new FileWriter("output.txt")) {

            OrderBook orderBook = new OrderBook();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                switch (parts[0]) {
                    case "u" -> orderBook.update(Integer.parseInt(parts[1]), Long.parseLong(parts[2]), parts[3]);
                    case "q" -> {
                        switch (parts[1]) {
                            case "best_bid" -> writer.write(orderBook.getBestBid() + "\n");
                            case "best_ask" -> writer.write(orderBook.getBestAsk() + "\n");
                            case "size" -> writer.write(orderBook.getSizeAtPrice(Integer.parseInt(parts[2])) + "\n");
                        }
                    }
                    case "o" -> orderBook.executeMarketOrder(parts[1], Long.parseLong(parts[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




