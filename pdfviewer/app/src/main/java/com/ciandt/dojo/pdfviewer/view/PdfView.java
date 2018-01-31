package com.ciandt.dojo.pdfviewer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * PdfView for Android 5.0 (level 21) - Example
 */

public class PdfView extends View {

    private Integer page = 0;

    private Integer pageCount = 0;

    private Byte zoom = 1;

    private Point position = new Point(0, 0);

    private PdfRenderer renderer;

    public PdfView(Context context) {
        super(context);
    }

    public PdfView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PdfView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PdfView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /***
     * Create a temporary file from URL
     * @param url URL
     * @return Object file with the path and name from temporary file.
     * @throws IOException I/O Exception
     */
    private File createTemporaryFile(URL url) throws IOException {
        File tempFile = File.createTempFile("tmp_", ".pdf");
        FileOutputStream output = new FileOutputStream(tempFile);
        URLConnection conn = url.openConnection();
        conn.connect();
        if (conn.getDoInput()) {
            BufferedInputStream input = new BufferedInputStream(conn.getInputStream());
            int data = -1;
            while ((data = input.read()) != -1) {
                output.write(data);
            }
            input.close();
        }
        output.close();
        tempFile.deleteOnExit();
        return tempFile;
    }

    /**
     * Load pdf from URL
     * @param url URL (example: http://www.google.com)
     */
    public void load(final URL url) {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final File temporaryFile = createTemporaryFile(url);
                    load(temporaryFile);
                } catch (Exception e) {
                    Log.e(PdfView.class.getName(), e.getMessage(), e);
                }
            }
        });
        thread.start();
    }

    /**
     * Load pdf from File
     * @param file Local file (example: /root/file)
     * @throws IOException I/O Exception
     */
    public void load(File file) throws IOException {
        renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
        pageCount = renderer.getPageCount();
        if (pageCount > 0) {
            page = 1;
        } else {
            throw new IOException("File not loaded");
        }
        post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }

    /***
     * Set page number from pdf file
     * @param page Page namber
     */
    public void setPage(int page) {
        if (page < 1 || page > pageCount) {
            throw new IndexOutOfBoundsException();
        }
        this.page = page;
        invalidate();
    }

    /**
     * Next page from pdf file
     */
    public void next() {
        setPage(page < pageCount ? page + 1 : page);
    }

    /**
     * Previous page from pdf file
     */
    public void previous() {
        setPage(page > 1 ? page - 1 : page);
    }

    /**
     * Get the current page from pdf file
     * @return Page number
     */
    public Integer getPage() {
        return page;
    }

    /**
     * Get the total pages from pdf file
     * @return Total pages
     */
    public Integer getPageCount() {
        return pageCount;
    }

    /**
     * Zoom view (values from 1 to 5)
     * @return zoom number (x1, x2, x3)
     */
    public Byte getZoom() {
        return zoom;
    }

    /**
     * Zoom view (values from 1 to 5)
     * @param zoom zoom number (x1, x2, x3)
     */
    public void setZoom(Byte zoom) {
        if ((zoom < 1) && (zoom > 5)) {
            throw new IllegalArgumentException();
        }
        this.zoom = zoom;
        invalidate();
    }

    /**
     * Current position (area: x, y) from page (pdf)
     * @return Current position
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Current position (area: x, y) from page (pdf)
     * @param position Position
     */
    public void setPosition(Point position) {
        this.position = position;
        invalidate();
    }

    /**
     * @override View#draw
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (page > 0 && page <= pageCount) {
            Paint paint = new Paint();
            Bitmap bitmap = Bitmap.createBitmap(getWidth() * zoom, getHeight() * zoom, Bitmap.Config.ARGB_4444);
            renderer.openPage(page - 1).render(bitmap, null, null
                    , PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            canvas.drawBitmap(bitmap, -position.x, -position.y, paint);
        }
    }

    /**
     * @override Object#finalize
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (renderer != null) {
            renderer.close();
        }
    }
}
