package gq.yigit.mycity.oldShit.votesFragment;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VotesContent {

	public static final List<VoteItem> ITEMS = new ArrayList<VoteItem>();


	public static final Map<String, VoteItem> ITEM_MAP = new HashMap<String, VoteItem>();
	public static void addItem(VoteItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}


	public static class VoteItem {
		public final String id;
		public final String name;
		public final String details;
		public final Bitmap img;

		public VoteItem(String id, String name, String details, Bitmap img) {
			this.id = id;
			this.name = name;
			this.details = details;
			this.img = img;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
