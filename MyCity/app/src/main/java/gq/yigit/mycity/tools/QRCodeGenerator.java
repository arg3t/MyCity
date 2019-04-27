package gq.yigit.mycity.tools;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;


import java.util.Arrays;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class QRCodeGenerator {
	public static Bitmap Generate(String data, int height,int width) throws WriterException {

		BitMatrix result;
		try {
			result = new MultiFormatWriter().encode(data,
					BarcodeFormat.QR_CODE, width, height, null);
		} catch (IllegalArgumentException iae) {
			// Unsupported format
			return null;
		}

		int w = result.getWidth();
		int h = result.getHeight();
		int[] pixels = new int[w * h];
		for (int y = 0; y < h; y++) {
			int offset = y * w;
			for (int x = 0; x < w; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
		return bitmap;
	}
}

