package src.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Utilitaire d'export CSV.
 *
 * Pur Java, sans dépendance. Gère les caractères spéciaux du CSV (virgule,
 * guillemet, retour à la ligne) via la convention RFC 4180 : valeurs entourées
 * de guillemets, guillemets internes doublés.
 */
public class CsvExporter {

    private CsvExporter() {}

    /**
     * Exporte une liste de lignes (chaque ligne = tableau de String) dans un fichier CSV.
     *
     * @param filePath chemin du fichier de sortie (ex: "/home/user/export.csv")
     * @param headers  en-têtes de colonnes (peut être null si pas d'en-tête)
     * @param rows     lignes de données
     * @return true si l'export a réussi, false sinon
     */
    public static boolean export(String filePath, String[] headers, List<String[]> rows) {
        if (filePath == null || filePath.isEmpty()) return false;

        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            if (headers != null && headers.length > 0) {
                pw.println(toCsvLine(headers));
            }
            if (rows != null) {
                for (String[] row : rows) {
                    pw.println(toCsvLine(row));
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("Erreur export CSV : " + e.getMessage());
            return false;
        }
    }

    // Convertit un tableau de valeurs en une ligne CSV avec échappement RFC 4180
    private static String toCsvLine(String[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(escape(values[i]));
        }
        return sb.toString();
    }

    // Échappe une valeur : si elle contient une virgule, un guillemet, ou un \n,
    // on l'entoure de guillemets et on double les guillemets internes.
    private static String escape(String value) {
        if (value == null) return "";
        boolean needQuotes = value.contains(",") || value.contains("\"")
                          || value.contains("\n") || value.contains("\r");
        String escaped = value.replace("\"", "\"\"");
        return needQuotes ? "\"" + escaped + "\"" : escaped;
    }
}