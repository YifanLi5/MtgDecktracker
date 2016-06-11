package com.example.yifan.mtgdecktracker.masterDetailDev.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<CardItem> ITEMS = new ArrayList<CardItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, CardItem> ITEM_MAP = new HashMap<String, CardItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        addItem(new CardItem("Ulamog, the Ceaseless Hunger", "https://image.deckbrew.com/mtg/multiverseid/402079.jpg"));
        addItem(new CardItem("Archangel Avacyn", "https://image.deckbrew.com/mtg/multiverseid/409741.jpg"));
    }

    private static void addItem(CardItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.cardName, item);
    }

    /**
     * A card item representing a piece of content.
     */
    public static class CardItem {
        public final String cardName;
        public final String imageURL;


        public CardItem(String cardName, String imageURL) {
            this.cardName = cardName;
            this.imageURL = imageURL;
        }

        @Override
        public String toString() {
            return cardName;
        }
    }
}
