package services;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.File;

public class OcrService {

    public static String performOcr(File imageFile) throws TesseractException {
        ITesseract tess = new Tesseract();
        //pytesseract.pytesseract.tesseract_cmd = r'C:/Program Files/Tesseract-OCR/tesseract.exe'
        // Optional: if your Tesseract data path isn’t in PATH, set it
         tess.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        // If documents are in French, specify:
        // tess.setLanguage("fra");

        return tess.doOCR(imageFile);
    }
}
