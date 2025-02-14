package services;

import entities.Utilisateur;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtService {

    // Clé secrète pour signer le JWT
    private static final String SECRET_KEY = "secret_key";

    // Méthode pour générer le JWT
    public String generateToken(Utilisateur utilisateur) {
        long expirationTime = 1000 * 60 * 60 * 24; // Expiration du JWT dans 24h
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        return Jwts.builder()
                .setSubject(String.valueOf(utilisateur.getId()))  // ID de l'utilisateur dans le token
                .claim("role", utilisateur.getRole())              // Ajouter le rôle dans le token
                .setIssuedAt(new Date())                           // Date de création du token
                .setExpiration(expirationDate)                     // Date d'expiration
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)    // Utilisation de la clé secrète
                .compact();
    }
}
