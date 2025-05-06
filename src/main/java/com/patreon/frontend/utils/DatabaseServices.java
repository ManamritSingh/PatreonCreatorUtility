package com.patreon.frontend.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.patreon.frontend.models.EarningEntry;
import com.patreon.frontend.models.EmailReward;
import com.patreon.frontend.models.PostEntry;
import com.patreon.frontend.models.SurveyEntry;
import com.patreon.frontend.models.UserEntry;
import com.patreon.utils.DatabaseConnection;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class DatabaseServices {
	public void loadEarningsFromDB(TableView<EarningEntry> earningTable, ObservableList<EarningEntry> earningData) {
        String query = "SELECT month, year, total, webMembershipCharges, iOSMembershipCharges, " +
                       "webGiftCharges, iOSGiftCharges, earnings, processingFee, patreonFee, iOSFee, " +
                       "merchShipping, declines, percentMembershipEarnings, percentMembershipProcessingFees, " +
                       "percentMembershipPatreonFees, percentGiftEarnings, percentGiftProcessingFees, " +
                       "percentGiftPatreonFees, currencyConversionFee, currencyConversionFeePercent, currency " +
                       "FROM earnings";  // match your actual DB table and columns

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            earningData.clear();

            while (rs.next()) {
                EarningEntry entry = new EarningEntry(
                    rs.getString("month"),
                    rs.getInt("year"),
                    rs.getDouble("total"),
                    rs.getDouble("webMembershipCharges"),
                    rs.getDouble("iOSMembershipCharges"),
                    rs.getDouble("webGiftCharges"),
                    rs.getDouble("iOSGiftCharges"),
                    rs.getDouble("earnings"),
                    rs.getDouble("processingFee"),
                    rs.getDouble("patreonFee"),
                    rs.getDouble("iOSFee"),
                    rs.getDouble("merchShipping"),
                    rs.getDouble("declines"),
                    rs.getDouble("percentMembershipEarnings"),
                    rs.getDouble("percentMembershipProcessingFees"),
                    rs.getDouble("percentMembershipPatreonFees"),
                    rs.getDouble("percentGiftEarnings"),
                    rs.getDouble("percentGiftProcessingFees"),
                    rs.getDouble("percentGiftPatreonFees"),
                    rs.getDouble("currencyConversionFee"),
                    rs.getDouble("currencyConversionFeePercent"),
                    rs.getString("currency")
                );

                earningData.add(entry);
            }

            earningTable.setItems(earningData);

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            if (e.getMessage().contains("no such table")) {
                System.out.println("Earnings table does not exist. Creating it...");
                if (conn != null) {
                    createEarningsTable(conn);
                }
            } else {
                e.printStackTrace();
            }
        }
    }

    public static void createEarningsTable(Connection conn) {
        String sql = """
            CREATE TABLE IF NOT EXISTS earnings (
                month TEXT,
                year INTEGER,
                total REAL,
                webMembershipCharges REAL,
                iOSMembershipCharges REAL,
                webGiftCharges REAL,
                iOSGiftCharges REAL,
                earnings REAL,
                processingFee REAL,
                patreonFee REAL,
                iOSFee REAL,
                merchShipping REAL,
                declines REAL,
                percentMembershipEarnings REAL,
                percentMembershipProcessingFees REAL,
                percentMembershipPatreonFees REAL,
                percentGiftEarnings REAL,
                percentGiftProcessingFees REAL,
                percentGiftPatreonFees REAL,
                currencyConversionFee REAL,
                currencyConversionFeePercent REAL,
                currency TEXT
            )
            """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Earnings table created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void loadPostFromDB(TableView<PostEntry> postTable, ObservableList<PostEntry> postData ) {
    	String query = "SELECT title, totalImpressions, likes, comments, newFreeMembers, " +
    			"newPaidMembers, publishedDateTime, link FROM posts";  // match your actual DB table and columns

    	Connection conn = null;

    	try {conn = DatabaseConnection.getConnection();
    		PreparedStatement stmt = conn.prepareStatement(query);
    		ResultSet rs = stmt.executeQuery();

    		postData.clear();

    		while (rs.next()) {
    			PostEntry entry = new PostEntry(
    					rs.getString("title"),
    					rs.getInt("totalImpressions"),
    					rs.getInt("likes"),
    					rs.getInt("comments"),
    					rs.getInt("newFreeMembers"),
    					rs.getInt("newPaidMembers"),
    					rs.getString("publishedDateTime"),
    					rs.getString("link")
    			);

    			postData.add(entry);
    		}

    		postTable.setItems(postData);

    		rs.close();
    		stmt.close();
    		conn.close();

    	} catch (SQLException e) {
    		if (e.getMessage().contains("no such table")) {
    			System.out.println("Post table does not exist. Creating it...");
    			if (conn != null) {
    				createPostTable(conn);
    			}
    		} else {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static void createPostTable(Connection conn) {
    	String sql = """
                CREATE TABLE IF NOT EXISTS posts (
                    title TEXT, 
                    totalImpressions INTEGER, 
                    likes INTEGER, 
                    comments INTEGER, 
                    newFreeMembers INTEGER,
    				newPaidMembers INTEGER, 
    				publishedDateTime TEXT, 
    				link TEXT
                )
                """;

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Posts table created.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
    
    public void loadSurveyFromDB(TableView<SurveyEntry> surveyTable, ObservableList<SurveyEntry> surveyData) {
    	String query = "SELECT submittedDateTime, name, email, tier, survey, comments FROM surveys";  // match your actual DB table and columns

    	Connection conn = null;

    	try {conn = DatabaseConnection.getConnection();
    		PreparedStatement stmt = conn.prepareStatement(query);
    		ResultSet rs = stmt.executeQuery();

    		surveyData.clear();

    		while (rs.next()) {
    			SurveyEntry entry = new SurveyEntry(
    					rs.getString("submittedDateTime"),
    					rs.getString("name"),
    					rs.getString("email"),
    					rs.getString("tier"),
    					rs.getString("survey"),
    					rs.getString("comments")
    			);

    			surveyData.add(entry);
    		}

    		surveyTable.setItems(surveyData);

    		rs.close();
    		stmt.close();
    		conn.close();

    	} catch (SQLException e) {
    		if (e.getMessage().contains("no such table")) {
    			System.out.println("Survey table does not exist. Creating it...");
    			if (conn != null) {
    				createSurveyTable(conn);
    			}
    		} else {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static void createSurveyTable(Connection conn) {
    	String sql = """
                CREATE TABLE IF NOT EXISTS surveys (
                    submittedDateTime TEXT, 
                    name TEXT, 
                    email TEXT, 
                    tier TEXT, 
                    survey TEXT, 
                    comments TEXT
                )
                """;

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Surveys table created.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
    
    public void loadUserFromDB(TableView<UserEntry> userTable, ObservableList<UserEntry> userData) {
    	String query = "SELECT id, address_line1, address_line2, address_name, age_range, city,"
    			+ "country, education_level, email, first_name, gender, income_range, is_active,"
    			+ "last_name, pledge_amount_cents, raffle_eligible, state, tier_id, zip_code "
    			+ "FROM usercsv";  // match your actual DB table and columns

    	Connection conn = null;

    	try {
    		conn = DatabaseConnection.getConnection();
    		PreparedStatement stmt = conn.prepareStatement(query);
    		ResultSet rs = stmt.executeQuery();

    		userData.clear();

    		while (rs.next()) {
    			UserEntry entry = new UserEntry(
    					rs.getString("id"),
    					rs.getString("first_name"),
    					rs.getString("last_name"),
    					rs.getString("email"),
    					rs.getString("is_active"),	
    					rs.getString("tier_id"),
    					rs.getString("pledge_amount_cents"),
    					rs.getString("address_name"),	
    					rs.getString("address_line1"),
    					rs.getString("address_line2"),
    					rs.getString("city"),
    					rs.getString("state"),		
    					rs.getString("zip_code"),
    					rs.getString("country"),
    					rs.getString("gender"),
    					rs.getString("age_range"),
    					rs.getString("education_level"),
    					rs.getString("income_range"),
    					rs.getString("raffle_eligible")
    			);

         userData.add(entry);
     }

     userTable.setItems(userData);

     rs.close();
     stmt.close();
     conn.close();

 } catch (SQLException e) {
     if (e.getMessage().contains("no such table")) {
         System.out.println("Member table does not exist. Creating it...");
         if (conn != null) {
             createUserTable(conn);
         }
     } else {
         e.printStackTrace();
     }
 }
    }
    
    public static void createUserTable(Connection conn) {
    	String sql = """
                CREATE TABLE IF NOT EXISTS usercsv (
                    id TEXT, 
                    address_line1 TEXT, 
                    address_line2 TEXT, 
                    address_name TEXT, 
                    age_range TEXT, 
                    city TEXT,
    				country TEXT, 
    				education_level TEXT, 
    				email TEXT, 
    				first_name TEXT, 
    				gender TEXT, 
    				income_range TEXT, 
    				is_active TEXT, 
    				last_name TEXT, 
    				pledge_amount_cents INTEGER, 
    				raffle_eligible TEXT, 
    				state TEXT, 
    				tier_id TEXT, 
    				zip_code TEXT
                )
                """;

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("User table created.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
    
    public void loadRewardsFromDB(TableView<EmailReward> rewardsTable, ObservableList<EmailReward> rewardList) {
    	String query = "SELECT message, subject, trigger, recipients FROM rewards";  // match your actual DB table and columns

    	Connection conn = null;

    	try {conn = DatabaseConnection.getConnection();
    		PreparedStatement stmt = conn.prepareStatement(query);
    		ResultSet rs = stmt.executeQuery();

    		rewardList.clear();

    		while (rs.next()) {
    			EmailReward entry = new EmailReward(
    					rs.getString("message"),
    					rs.getString("subject"),
    					rs.getString("trigger"),
    					rs.getString("recipients")
    			);

    			rewardList.add(entry);
    		}

    		rewardsTable.setItems(rewardList);

    		rs.close();
    		stmt.close();
    		conn.close();

    	} catch (SQLException e) {
    		if (e.getMessage().contains("no such table")) {
    			System.out.println("Rewards table does not exist. Creating it...");
    			if (conn != null) {
    				createRewardsTable(conn);
    			}
    		} else {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static void createRewardsTable(Connection conn) {
    	String sql = """
                CREATE TABLE IF NOT EXISTS rewards (
                    message TEXT, 
                    subject TEXT, 
                    trigger TEXT, 
                    recipients TEXT
                )
                """;

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Rewards table created.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
}
