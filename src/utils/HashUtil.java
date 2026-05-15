package src.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilitaire de hachage des mots de passe.
 *
 * Utilise SHA-256 (natif en Java, sans dépendance).
 * Note pour la présentation : SHA-256 sans salt n'est pas idéal en production
 * (vulnérable aux rainbow tables), mais c'est largement suffisant pour un projet
 * pédagogique et bien meilleur que stocker les mots de passe en clair.
 * En production réelle on utiliserait BCrypt ou Argon2.
 */
public class HashUtil {

    private HashUtil() {}

    /**
     * Hash une chaîne en SHA-256 et retourne le résultat en hexadécimal (64 caractères).
     */
    public static String sha256(String input) {
        if (input == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Erreur sha256 : " + e.getMessage());
            return null;
        }
    }

    /**
     * Vérifie qu'une chaîne en clair correspond à un hash donné.
     */
    public static boolean verify(String plain, String hash) {
        if (plain == null || hash == null) return false;
        String hashed = sha256(plain);
        return hashed != null && hashed.equals(hash);
    }
}