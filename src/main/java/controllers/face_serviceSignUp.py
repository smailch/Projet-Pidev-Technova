import cv2
import numpy as np

MODEL_PATH = r"C:\Users\ichaa\Desktop\ESPRIT\ESPRIT 3A\Git Projects\Nouveau dossier\Projet-Pidev-Technova\git\Projet-Pidev-Technova\arcfaceresnet100-8.onnx"

def get_face_embedding():
    cap = cv2.VideoCapture(0)
    embeddings = []
    required_captures = 8  # Nombre de captures
    face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
    net = cv2.dnn.readNetFromONNX(MODEL_PATH)

    try:
        start_time = cv2.getTickCount()
        frame_skip = 5  # Capture tous les 5 frames pour éviter la surcharge
        frame_count = 0

        while len(embeddings) < required_captures and (cv2.getTickCount() - start_time) / cv2.getTickFrequency() < 15:
            ret, frame = cap.read()
            if not ret:
                continue

            frame_count += 1
            if frame_count % frame_skip != 0:
                continue  # Ignore certaines frames pour éviter la surcharge

            # Détection du visage
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            faces = face_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=6, minSize=(100, 100))

            if len(faces) > 0:
                x, y, w, h = faces[0]
                face_img = frame[y:y+h, x:x+w]

                # Prétraitement et génération d'embedding
                blob = cv2.dnn.blobFromImage(face_img, 1/127.5, (112, 112), (127.5, 127.5, 127.5), swapRB=True)
                net.setInput(blob)
                embedding = net.forward().flatten()
                embeddings.append(embedding)

                # Affichage du compteur de captures
                cv2.putText(frame, f"Capture {len(embeddings)}/{required_captures}", (10, 30),
                            cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 255, 0), 2)

                # Pause de 0.5s entre les captures pour éviter trop de captures rapides
                cv2.waitKey(500)

            cv2.imshow('Camera', frame)
            if cv2.waitKey(1) & 0xFF == ord('q'):
                break

    finally:
        cap.release()
        cv2.destroyAllWindows()

    if len(embeddings) == required_captures:
        return np.mean(embeddings, axis=0)  # Calcul de la moyenne
    return None

def embedding_to_string(embedding):
    return ' '.join(map(str, embedding))

if __name__ == "__main__":
    embedding = get_face_embedding()

    if embedding is not None:
        print(embedding_to_string(embedding))
    else:
        print("Aucun visage détecté")
