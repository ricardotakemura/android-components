package com.ciandt.dojo.pdfviewer;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ciandt.dojo.pdfviewer.view.PdfView;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private PdfView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pdfView = findViewById(R.id.pdfViewComponent);
        try {
            pdfView.load(new URL("http://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf"));
            pdfView.setZoom((byte) 3);
            pdfView.setPosition(new Point(400,400));
        } catch (Exception e) {
            Log.e(MainActivity.class.getName(), e.getMessage(), e);
        }
    }
}
