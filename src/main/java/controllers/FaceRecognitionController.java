package controllers;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

public class FaceRecognitionController {

    static {
        // Charger la bibliothèque native d'OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public void enregistrerVisageUtilisateur() {
        // Ouvrir la webcam (index 0 pour la caméra par défaut)
        VideoCapture camera = new VideoCapture(0);

        // Vérifier si la caméra est ouverte
        if (!camera.isOpened()) {
           // System.out.println("Erreur : Impossible d'ouvrir la caméra !");
            return;
        }

        // Charger le classificateur de visage (fichier XML)
        CascadeClassifier faceDetector = new CascadeClassifier();
        faceDetector.load("src/main/resources/haarcascade_frontalface_default.xml");

        // Vérifier si le classificateur est chargé
        if (faceDetector.empty()) {
            //System.out.println("Erreur : Impossible de charger le classificateur de visage !");
            return;
        }

        // Capturer une image depuis la caméra
        Mat frame = new Mat();
        camera.read(frame);

        // Détecter les visages dans l'image
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(frame, faceDetections);

        // Enregistrer l'image du visage détecté
        for (Rect rect : faceDetections.toArray()) {
            Mat face = new Mat(frame, rect);
            String faceImagePath = "src/main/resources/user_faces/visage_utilisateur.png";
            Imgcodecs.imwrite(faceImagePath, face);
            System.out.println("Visage enregistré : " + faceImagePath);
        }

        // Libérer la caméra
        camera.release();
    }
}