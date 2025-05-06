package com.patreon.frontend.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

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

	public Map<String, Map<LocalDate, Integer>> getTierRetentionData(String interval, boolean isMock) {
		Map<String, Map<LocalDate, Integer>> retentionData = new HashMap<>();
		String timeFormat;

		switch (interval.toLowerCase()) {
			case "weekly":
				timeFormat = "%Y-%W"; // Week number format
				break;
			case "monthly":
				timeFormat = "%Y-%m"; // Month format
				break;
			case "daily":
			default:
				timeFormat = "%Y-%m-%d"; // Day format
				break;
		}

		String query = """
        SELECT strftime(?, timestamp) as period,
               tier_name,
               SUM(patron_count) as patrons
        FROM tier_snap
        WHERE is_mock = ?
        GROUP BY tier_name, period
        ORDER BY tier_name, period
    """;

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, timeFormat);
			pstmt.setBoolean(2, isMock);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String tier = rs.getString("tier_name");
				String period = rs.getString("period");
				LocalDate periodDate;

				try {
					// For daily and monthly: parse directly (e.g., 2024-05 or 2024-05-01)
					periodDate = LocalDate.parse(period + (interval.equals("monthly") ? "-01" : ""));
				} catch (DateTimeParseException e) {
					// For weekly: convert year + week to LocalDate (start of that week)
					String[] parts = period.split("-");
					int year = Integer.parseInt(parts[0]);
					int week = Integer.parseInt(parts[1]);

					periodDate = LocalDate.ofYearDay(year, 1)
							.with(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR, Math.max(1, week))
							.with(java.time.DayOfWeek.MONDAY); // or SUNDAY if needed
				}

				retentionData
						.computeIfAbsent(tier, k -> new TreeMap<>())
						.put(periodDate, rs.getInt("patrons"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return retentionData;
	}



	public List<String> getAllTiers(boolean isMock) {
		List<String> tierNames = new ArrayList<>();
		String query = "SELECT DISTINCT tier_name FROM tier_snap WHERE is_mock = ? ORDER BY tier_name";

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setBoolean(1, isMock);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				tierNames.add(rs.getString("tier_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return tierNames;
	}

	private Connection connect() {
		try {
			String url = "jdbc:sqlite:JavaDatabase.db"; // adjust path if needed
			return DriverManager.getConnection(url);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	public Map<String, Map<LocalDate, Double>> getAvgChurnRates(String interval, boolean isMock) {
		Map<String, Map<LocalDate, Double>> churnData = new HashMap<>();
		String timeFormat;

		switch (interval.toLowerCase()) {
			case "weekly":  timeFormat = "%Y-%W"; break;
			case "monthly": timeFormat = "%Y-%m"; break;
			default:        timeFormat = "%Y-%m-%d"; break;
		}

		String query = """
        SELECT 
            t1.tier_name,
            strftime(?, t1.timestamp) AS period,
            t1.patron_count - t2.patron_count AS churn
        FROM tier_snap t1
        JOIN tier_snap t2
            ON t1.tier_name = t2.tier_name
           AND date(t2.timestamp) = date(t1.timestamp, '-1 day')
        WHERE t1.is_mock = ?
        ORDER BY t1.tier_name, t1.timestamp
    """;

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, timeFormat);
			pstmt.setBoolean(2, isMock);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String tier = rs.getString("tier_name");
				String period = rs.getString("period");
				int churn = rs.getInt("churn");

				LocalDate periodDate;
				try {
					if ("weekly".equals(interval)) {
						String[] parts = period.split("-");
						int year = Integer.parseInt(parts[0]);
						int week = Integer.parseInt(parts[1]);
						if (week == 0) continue;
						periodDate = LocalDate.parse(year + "-W" + week + "-1", DateTimeFormatter.ofPattern("yyyy-'W'ww-e"));
					} else if ("monthly".equals(interval)) {
						periodDate = LocalDate.parse(period + "-01");
					} else {
						periodDate = LocalDate.parse(period);
					}
				} catch (Exception e) {
					continue;
				}

				churnData.computeIfAbsent(tier, k -> new TreeMap<>()).put(periodDate, (double) churn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return churnData;
	}


	public Map<String, Map<LocalDate, Integer>> getWeeklyChurnData(List<String> selectedTiers, boolean isMock) {
		Map<String, Map<LocalDate, Integer>> churnData = new HashMap<>();

		String query = """
        SELECT 
            tier_name,
            strftime('%Y-%W', timestamp) AS week,
            SUM(patron_count) AS patrons
        FROM tier_snap
        WHERE is_mock = ?
        GROUP BY tier_name, week
        ORDER BY tier_name, week
    """;

		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setBoolean(1, isMock);
			ResultSet rs = pstmt.executeQuery();

			Map<String, Map<String, Integer>> raw = new HashMap<>();

			while (rs.next()) {
				String tier = rs.getString("tier_name");
				String week = rs.getString("week");
				int count = rs.getInt("patrons");

				if (!selectedTiers.contains(tier)) continue;

				raw.computeIfAbsent(tier, k -> new LinkedHashMap<>()).put(week, count);
			}

			for (String tier : raw.keySet()) {
				Map<String, Integer> weekMap = raw.get(tier);
				Map<LocalDate, Integer> churn = new LinkedHashMap<>();
				String prevWeek = null;

				for (String currentWeek : weekMap.keySet()) {
					if (prevWeek != null) {
						int diff = weekMap.get(currentWeek) - weekMap.get(prevWeek);

						try {
							String[] parts = currentWeek.split("-");
							int year = Integer.parseInt(parts[0]);
							int week = Integer.parseInt(parts[1]);
							LocalDate label = LocalDate.ofYearDay(year, 1)
									.with(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR, Math.max(1, week))
									.with(java.time.DayOfWeek.MONDAY);
							churn.put(label, diff);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					prevWeek = currentWeek;
				}

				churnData.put(tier, churn);
			}

			// Trim to last 15 weeks
			for (String tier : churnData.keySet()) {
				Map<LocalDate, Integer> full = churnData.get(tier);
				List<LocalDate> keys = new ArrayList<>(full.keySet());
				if (keys.size() > 15) {
					keys = keys.subList(keys.size() - 15, keys.size());
				}

				Map<LocalDate, Integer> trimmed = new LinkedHashMap<>();
				for (LocalDate k : keys) {
					trimmed.put(k, full.get(k));
				}
				churnData.put(tier, trimmed);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return churnData;
	}


}
