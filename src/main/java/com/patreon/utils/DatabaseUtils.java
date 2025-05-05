package com.patreon.utils;

import com.patreon.frontend.models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DatabaseUtils {

    public static void saveEarningsToDatabase(Connection connection, List<EarningEntry> earningData) throws SQLException {
        String deleteSQL = "DELETE FROM earnings";
        String insertSQL = "INSERT INTO earnings (" +
                "month, year, total, webMembershipCharges, iOSMembershipCharges, webGiftCharges, iOSGiftCharges, " +
                "earnings, processingFee, patreonFee, iOSFee, merchShipping, declines, " +
                "percentMembershipEarnings, percentMembershipProcessingFees, percentMembershipPatreonFees, " +
                "percentGiftEarnings, percentGiftProcessingFees, percentGiftPatreonFees, " +
                "currencyConversionFee, currencyConversionFeePercent, currency" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement deleteStmt = null;
        PreparedStatement insertStmt = null;

        try {
            connection.setAutoCommit(false); // Start transaction

            // Step 1: Clear table
            deleteStmt = connection.prepareStatement(deleteSQL);
            deleteStmt.executeUpdate();

            // Step 2: Insert new data
            insertStmt = connection.prepareStatement(insertSQL);
            for (EarningEntry entry : earningData) {
                insertStmt.setString(1, entry.getMonth().get());
                insertStmt.setInt(2, entry.getYear().get());
                insertStmt.setDouble(3, entry.getTotal().get());
                insertStmt.setDouble(4, entry.getWebMembershipCharges().get());
                insertStmt.setDouble(5, entry.getiOSMembershipCharges().get());
                insertStmt.setDouble(6, entry.getWebGiftCharges().get());
                insertStmt.setDouble(7, entry.getiOSGiftCharges().get());
                insertStmt.setDouble(8, entry.getEarnings().get());
                insertStmt.setDouble(9, entry.getProcessingFee().get());
                insertStmt.setDouble(10, entry.getPatreonFee().get());
                insertStmt.setDouble(11, entry.getiOSFee().get());
                insertStmt.setDouble(12, entry.getMerchShipping().get());
                insertStmt.setDouble(13, entry.getDeclines().get());
                insertStmt.setDouble(14, entry.getPercentMembershipEarnings().get());
                insertStmt.setDouble(15, entry.getPercentMembershipProcessingFees().get());
                insertStmt.setDouble(16, entry.getPercentMembershipPatreonFees().get());
                insertStmt.setDouble(17, entry.getPercentGiftEarnings().get());
                insertStmt.setDouble(18, entry.getPercentGiftProcessingFees().get());
                insertStmt.setDouble(19, entry.getPercentGiftPatreonFees().get());
                insertStmt.setDouble(20, entry.getCurrencyConversionFee().get());
                insertStmt.setDouble(21, entry.getCurrencyConversionFeePercent().get());
                insertStmt.setString(22, entry.getCurrency().get());

                insertStmt.addBatch();
            }

            insertStmt.executeBatch();
            connection.commit(); // Finish transaction

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback(); // Roll back on error
            }
            throw e;
        } finally {
            if (deleteStmt != null) deleteStmt.close();
            if (insertStmt != null) insertStmt.close();
            connection.setAutoCommit(true);
        }
    }
    
    public static void savePostToDatabase(Connection connection, List<PostEntry> postData) throws SQLException {
        String deleteSQL = "DELETE FROM posts";
        String insertSQL = "INSERT INTO posts (" +
                "title, totalImpressions, likes, comments, newFreeMembers, newPaidMembers, publishedDateTime, link" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement deleteStmt = null;
        PreparedStatement insertStmt = null;

        try {
            connection.setAutoCommit(false); // Start transaction

            // Step 1: Clear table
            deleteStmt = connection.prepareStatement(deleteSQL);
            deleteStmt.executeUpdate();

            // Step 2: Insert new data
            insertStmt = connection.prepareStatement(insertSQL);
            for (PostEntry entry : postData) {
                insertStmt.setString(1, entry.getTitle().get());
                insertStmt.setInt(2, entry.getTotalImpressions().get());
                insertStmt.setInt(3, entry.getLikes().get());
                insertStmt.setInt(4, entry.getComments().get());
                insertStmt.setInt(5, entry.getNewFreeMembers().get());
                insertStmt.setInt(6, entry.getNewPaidMembers().get());
                insertStmt.setString(7, entry.getPublishedDateTime().get());
                insertStmt.setString(8, entry.getLink().get());

                insertStmt.addBatch();
            }

            insertStmt.executeBatch();
            connection.commit(); // Finish transaction

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback(); // Roll back on error
            }
            throw e;
        } finally {
            if (deleteStmt != null) deleteStmt.close();
            if (insertStmt != null) insertStmt.close();
            connection.setAutoCommit(true);
        }
    }
    
    public static void saveSurveyToDatabase(Connection connection, List<SurveyEntry> surveyData) throws SQLException{
    	String deleteSQL = "DELETE FROM surveys";
        String insertSQL = "INSERT INTO surveys (" +
                "submittedDateTime, name, email, tier, survey, comments " +
                ") VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement deleteStmt = null;
        PreparedStatement insertStmt = null;

        try {
            connection.setAutoCommit(false); // Start transaction

            // Step 1: Clear table
            deleteStmt = connection.prepareStatement(deleteSQL);
            deleteStmt.executeUpdate();

            // Step 2: Insert new data
            insertStmt = connection.prepareStatement(insertSQL);
            for (SurveyEntry entry : surveyData) {
                insertStmt.setString(1, entry.getSubmittedDateTime().get());
                insertStmt.setString(2, entry.getName().get());
                insertStmt.setString(3, entry.getEmail().get());
                insertStmt.setString(4, entry.getTier().get());
                insertStmt.setString(5, entry.getSurvey().get());
                insertStmt.setString(6, entry.getComments().get());

                insertStmt.addBatch();
            }

            insertStmt.executeBatch();
            connection.commit(); // Finish transaction

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback(); // Roll back on error
            }
            throw e;
        } finally {
            if (deleteStmt != null) deleteStmt.close();
            if (insertStmt != null) insertStmt.close();
            connection.setAutoCommit(true);
        }
    }
    
    public static void saveUserToDatabase(Connection connection, List<UserEntry> userData) throws SQLException{
    	String deleteSQL = "DELETE FROM usercsv";
        String insertSQL = "INSERT INTO usercsv (" +
                "id, address_line1, address_line2, address_name, age_range, city,"
                + "country, education_level, email, first_name, gender, income_range, is_active,"
                + "last_name, pledge_amount_cents, raffle_eligible, state, tier_id, zip_code"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement deleteStmt = null;
        PreparedStatement insertStmt = null;

        try {
            connection.setAutoCommit(false); // Start transaction

            // Step 1: Clear table
            deleteStmt = connection.prepareStatement(deleteSQL);
            deleteStmt.executeUpdate();

            // Step 2: Insert new data
            insertStmt = connection.prepareStatement(insertSQL);
            for (UserEntry entry : userData) {
                insertStmt.setString(1, entry.getUserID().get());
                insertStmt.setString(2, entry.getAddressLine1().get());
                insertStmt.setString(3, entry.getAddressLine2().get());
                insertStmt.setString(4, entry.getAddressName().get());
                insertStmt.setString(5, entry.getAgeRange().get());
                insertStmt.setString(6, entry.getCity().get());
                insertStmt.setString(7, entry.getCountry().get());
                insertStmt.setString(8, entry.getEducationLevel().get());
                insertStmt.setString(9, entry.getEmail().get());
                insertStmt.setString(10, entry.getFirstName().get());
                insertStmt.setString(11, entry.getGender().get());
                insertStmt.setString(12, entry.getIncomeRange().get());
                insertStmt.setString(13, entry.getActive().get());
                insertStmt.setString(14, entry.getLastName().get());
                insertStmt.setString(15, entry.getPledge().get());
                insertStmt.setString(16, entry.getRaffleEligible().get());
                insertStmt.setString(17, entry.getState().get());
                insertStmt.setString(18, entry.getTier().get());
                insertStmt.setString(19, entry.getZipCode().get());

                insertStmt.addBatch();
            }

            insertStmt.executeBatch();
            connection.commit(); // Finish transaction

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback(); // Roll back on error
            }
            throw e;
        } finally {
            if (deleteStmt != null) deleteStmt.close();
            if (insertStmt != null) insertStmt.close();
            connection.setAutoCommit(true);
        }
    }
    
    public static void saveRewardToDatabase(Connection connection, EmailReward reward) {
    	String sql = "INSERT INTO rewards (message, subject, trigger, recipients) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, reward.getMessage().get());
            stmt.setString(2, reward.getSubject().get());
            stmt.setString(3, reward.getTriggerOpt().get());
            stmt.setString(4, String.join(",", reward.getRecepients())); // Serialize list to CSV

            stmt.executeUpdate();
            System.out.println("Reward saved to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void deleteRewardFromDatabase(Connection connection, EmailReward reward) {
    	String sql = "DELETE FROM rewards WHERE message = ? AND subject = ? AND trigger = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, reward.getMessage().get());
            stmt.setString(2, reward.getSubject().get());
            stmt.setString(3, reward.getTriggerOpt().get());

            int rowsDeleted = stmt.executeUpdate();
            System.out.println(rowsDeleted + " reward(s) deleted from database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
