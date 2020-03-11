package com.harvard.pdfGenerationModule;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.harvard.R;
import com.harvard.utils.AppController;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class PDFGenerationActivity extends AppCompatActivity {
    private static final String FILE_FOLDER = "SamplePDF";
    private static File file;
    private static final String filepath = Environment.getExternalStorageDirectory().getPath();
    private File myFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfgeneration);
        createPdf();
        displayPDF();
    }

    private void createPdf() {
        try {
            getFile();
            //Create time stamp
            String timeStamp = AppController.getDateFormatType3();

            String filePath = file.getAbsolutePath() + File.separator + timeStamp + ".pdf";
            myFile = new File(filePath);
            if (!myFile.exists())
                myFile.createNewFile();
            OutputStream output = new FileOutputStream(myFile);

            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, output);
            writer.setFullCompression();

            document.addCreationDate();
            document.setPageSize(PageSize.A4);
            document.setMargins(36, 36, 36, 36);

            document.open();

            Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);

            Paragraph p1 = new Paragraph("FDA REPORT", catFont);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(p1);
            addEmptyLine(p1, 30);

            for (int i = 0; i < 10; i++) {
                Paragraph consentItem = new Paragraph("Title1", catFont);
                consentItem.add(new Paragraph("What is Lorem Ipsum?"));
                consentItem.add(new Paragraph("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."));
                consentItem.setAlignment(Paragraph.ALIGN_LEFT);
                document.add(consentItem);
                addEmptyLine(consentItem, 30);
            }


            document.close();

        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }

    }

    private void getFile() {
        file = new File(filepath, FILE_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private void displayPDF() {
        PDFView pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfView.fromFile(new File(myFile.toString()))
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }
}
