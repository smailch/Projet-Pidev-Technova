package services;

import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.stripe.exception.StripeException;
import entities.DossierFiscale;
import tools.MyConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PaymentProcessor {

    // Initialize Stripe API key
    static {
        Stripe.apiKey = "sk_test_51Qxf4dQXJMBIITLC2DezGnUJgUywCGGHzUWy03aCqvG6lieBvs5BQdOi8trOLkRba9LOXuLBpYGACuExOk9DYqOF00W40MvO8x";  // Replace with your secret key
    }



    // Method to process the payment
    public static void processPayment(String tokenId,int userId,int dossierId) {
        try {

            if (dossierId == 0) {
                System.out.println("No valid dossier found for the session.");
                return;
            }

            // Retrieve dossier details
            PaymentProcessor pp = new PaymentProcessor();
            DossierFiscale dossier = pp.getDossierFiscaleById(dossierId);
            if (dossier == null) {
                System.out.println("Dossier not found!");
                return;
            }

            // Define amount to be paid (You can set this dynamically)
            double amountPaid = dossier.getTotalImpot() - dossier.getTotalImpotPaye();
            if (amountPaid <= 0) {
                System.out.println("No remaining balance to pay.");
                return;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("amount", (int) (amountPaid * 100)); // Stripe requires amount in cents
            params.put("currency", "eur");
            params.put("description", "Payment for Dossier ID: ");
            params.put("source", tokenId);

            // Create a charge
            Charge charge = Charge.create(params);
            System.out.println("Charge successful: " + charge.getId());
            pp.updateDossierPayment(dossierId,amountPaid);
        } catch (StripeException e) {
            System.out.println("Payment failed: " + e.getMessage());
        }
    }






    public DossierFiscale getDossierFiscaleById(int dossierId) {
        DossierFiscale dossier = null;
        String req = "SELECT * FROM DossierFiscale WHERE id = ?";

        try {
            // Prepare the statement
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);

            // Set the parameter using SessionManager's method to get the dossierId
            pst.setInt(1, dossierId);

            // Execute the query
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Create the DossierFiscale object and populate it with the result
                dossier = new DossierFiscale();
                dossier.setId(rs.getInt(1));
                dossier.setIdUser(rs.getInt(2));
                dossier.setAnneeFiscale(rs.getInt(3));
                dossier.setTotalImpot(rs.getDouble(4));
                dossier.setTotalImpotPaye(rs.getDouble(5));
                dossier.setStatus(rs.getString(6));
                dossier.setDateCreation(rs.getString(7));
                dossier.setMoyenPaiement(rs.getString(8));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return dossier;
    }
    public void updateDossierPayment(int dossierId, double amountPaid) {
        String req = "UPDATE dossierfiscale SET total_impot_paye = total_impot_paye + ?, moyen_paiement = ?, status = ? WHERE id = ?";

        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);

            // Get existing dossier data
            DossierFiscale dossier = getDossierFiscaleById(dossierId);
            if (dossier == null) {
                System.out.println("Dossier not found for update.");
                return;
            }

            // Calculate new total paid
            double newTotalPaid = dossier.getTotalImpotPaye() + amountPaid;

            // Determine the new status
            String newStatus = (newTotalPaid >= dossier.getTotalImpot()) ? "payé" : "partiellement payé";

            // Set parameters
            pst.setDouble(1, amountPaid);
            pst.setString(2, "Virement Bancaire");
            pst.setString(3, newStatus);
            pst.setInt(4, dossierId);

            // Execute the update
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Dossier updated successfully!");
            } else {
                System.out.println("Failed to update dossier.");
            }
        } catch (SQLException e) {
            System.out.println("Database update error: " + e.getMessage());
        }
    }


}
