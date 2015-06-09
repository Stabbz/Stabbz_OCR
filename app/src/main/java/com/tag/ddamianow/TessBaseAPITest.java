package com.tag.ddamianow;

/**
 * Created by Damyan Damyanov on 27.5.2015 г..
 */
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.test.suitebuilder.annotation.SmallTest;

import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.googlecode.tesseract.android.TessBaseAPI.PageIteratorLevel;

import junit.framework.TestCase;

import java.io.File;

public class TessBaseAPITest extends TestCase {
    private static final String TESSBASE_PATH = "/mnt/sdcard/tesseract/";
    private static final String DEFAULT_LANGUAGE = "eng";
    private static final String EXPECTED_FILE = TESSBASE_PATH + "tessdata/" + DEFAULT_LANGUAGE
            + ".traineddata";

    @SmallTest
    public void testInit() {
        // First, make sure the eng.traineddata file exists.
        assertTrue("Make sure that you've copied " + DEFAULT_LANGUAGE + ".traineddata to "
                + EXPECTED_FILE, new File(EXPECTED_FILE).exists());

        // Attempt to initialize the API.
        final TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);

        // Attempt to shut down the API.
        baseApi.end();
    }

    @SmallTest
    public void testSetImage() {
        // First, make sure the eng.traineddata file exists.
        assertTrue("Make sure that you've copied " + DEFAULT_LANGUAGE + ".traineddata to "
                + EXPECTED_FILE, new File(EXPECTED_FILE).exists());

        // Attempt to initialize the API.
        final TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);

        // Set the image to a Bitmap.
        final Bitmap bmp = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888);
        baseApi.setImage(bmp);

        // Attempt to shut down the API.
        baseApi.end();
    }

    private static Bitmap getTextImage(String text, int width, int height) {
        final Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Paint paint = new Paint();
        final Canvas canvas = new Canvas(bmp);

        canvas.drawColor(Color.WHITE);

        paint.setColor(Color.BLACK);
        paint.setStyle(Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(24.0f);
        canvas.drawText(text, width / 2, height / 2, paint);

        return bmp;
    }

    @SmallTest
    public void testGetUTF8Text() {
        // First, make sure the eng.traineddata file exists.
        assertTrue("Make sure that you've copied " + DEFAULT_LANGUAGE + ".traineddata to "
                + EXPECTED_FILE, new File(EXPECTED_FILE).exists());

        final String inputText = "hello";
        final Bitmap bmp = getTextImage(inputText, 640, 480);

        // Attempt to initialize the API.
        final TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
        baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
        baseApi.setImage(bmp);

        // Ensure that the result is correct.
        final String outputText = baseApi.getUTF8Text();
        assertEquals("\"" + outputText + "\" != \"" + inputText + "\"", inputText, outputText);

        // Ensure getRegions() works.
        final Pixa regions = baseApi.getRegions();
        assertEquals("Found one region", regions.size(), 1);

        // Ensure getWords() works.
        final Pixa words = baseApi.getWords();
        assertEquals("Found one word", words.size(), 1);

        // Iterate through the results.
        final ResultIterator iterator = baseApi.getResultIterator();
        String lastUTF8Text;
        float lastConfidence;
        int count = 0;
        iterator.begin();
        do {
            lastUTF8Text = iterator.getUTF8Text(PageIteratorLevel.RIL_WORD);
            lastConfidence = iterator.confidence(PageIteratorLevel.RIL_WORD);
            count++;
        } while (iterator.next(PageIteratorLevel.RIL_WORD));

        assertEquals("Found only one result", count, 1);
        assertEquals("Found the correct result", lastUTF8Text, outputText);
        assertTrue("Result was high-confidence", lastConfidence > 80);

        // Attempt to shut down the API.
        baseApi.end();
        bmp.recycle();
    }
}