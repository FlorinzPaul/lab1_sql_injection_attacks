package com.ias101.lab1.security;

import com.ias101.lab1.database.util.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Authentication class for user validation
 */
public class Authenticator {
    /**
     * Authenticates a user by checking username and password against database
     *
     * @param username The username to authenticate
     * @param password The password to authenticate
     * @return boolean Returns true if authentication successful, false otherwise
     * @throws RuntimeException if there is a SQL error during authentication
     */
    public static boolean authenticateUser(String username, String password) {

        username = sanitizeInput(username);
        password = sanitizeInput(password);

        try (var conn = DBUtil.connect("jdbc:sqlite:src/main/resources/database/sample.db", "app_user", "secure_password")) {
            try (var statement = conn.createStatement()) {
                var query = """
                        SELECT * FROM user_data
                        WHERE username = '""" + username + "'"
                        + " AND password = '" + password + "'";

                System.out.println(query); // Debugging only, remove in production
                ResultSet rs = statement.executeQuery(query);

                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sanitizes user input by escaping quotes and removing dangerous characters.
     *
     * @param input The raw user input
     * @return The sanitized input
     */
    private static String sanitizeInput(String input) {
        if (input == null) return "";
        input = input.trim(); // Remove leading and trailing spaces
        input = input.replace("'", "''"); // Escape single quotes
        input  = input.replaceAll("[\";\\-\\/*]", ""); // Remove SQL-injection related characters
        return input;
    }
}