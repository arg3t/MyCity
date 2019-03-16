package gq.yigit.mycity.vote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VotesContent {

	public static final List<VoteItem> ITEMS = new ArrayList<VoteItem>();


	public static final Map<String, VoteItem> ITEM_MAP = new HashMap<String, VoteItem>();
	private static void addItem(VoteItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	private static VoteItem creaVoteItem(int position) {
		return new VoteItem(String.valueOf(position), "Item " + position, makeDetails(position));
	}

	private static String makeDetails(int position) {
		StringBuilder builder = new StringBuilder();
		builder.append("Details about Item: ").append(position);
		for (int i = 0; i < position; i++) {
			builder.append("\nMore details information here.");
		}
		return builder.toString();
	}

	public static class VoteItem {
		public final String id;
		public final String name;
		public final String details;

		public VoteItem(String id, String name, String details) {
			this.id = id;
			this.name = name;
			this.details = details;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
