package test;

import org.opencv.core.*;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

public class WebcamFaceDetection {

    static {
        // Spécifiez le chemin de la bibliothèque native
        System.setProperty("java.library.path", "C:\\Users\\chemlali smail\\Downloads\\opencv\\build\\java\\x64");
        // Chargez la bibliothèque native
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        // Ouvrir la webcam (index 0 pour la caméra par défaut)$
        System.setProperty("java.library.path", "C:\\Users\\chemlali smail\\Downloads\\opencv\\build\\java\\x64");
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

        // Créer une fenêtre pour afficher le flux vidéo
        HighGui.namedWindow("Webcam - Détection de visage");

        // Boucle pour capturer et traiter les images en temps réel
        Mat frame = new Mat();
        while (true) {
            // Capturer une image depuis la caméra
            camera.read(frame);

            // Détecter les visages dans l'image
            MatOfRect faceDetections = new MatOfRect();
            faceDetector.detectMultiScale(frame, faceDetections);

            // Dessiner des rectangles autour des visages détectés
            for (Rect rect : faceDetections.toArray()) {
                Imgproc.rectangle(frame, rect, new Scalar(0, 255, 0), 2); // Rectangle vert
            }

            // Afficher l'image dans la fenêtre
            HighGui.imshow("Webcam - Détection de visage", frame);

            // Attendre 30 ms et vérifier si l'utilisateur a appuyé sur une touche
            if (HighGui.waitKey(30) >= 0) {
                break; // Quitter la boucle si une touche est pressée
            }
        }

        // Libérer la caméra et fermer la fenêtre
        camera.release();
        HighGui.destroyAllWindows();
    }
}