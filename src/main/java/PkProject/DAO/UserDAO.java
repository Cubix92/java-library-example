package PkProject.DAO;

import PkProject.Entity.User;
import PkProject.Model.ConnectionManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UserDAO {
    
    public static void insert(User user) {
        Statement stmt;
        Connection connection = ConnectionManager.getConnection();
        
        String query = "INSERT INTO user (username, pass, email, firstname, surname) VALUES("
                + "'" + user.getUsername() + "',"
                + "'" + user.getPassword()+ "',"
                + "'" + user.getEmail()+ "',"
                + "'" + user.getFirstname()+ "',"
                + "'" + user.getSurname() + "'"
                + ")";
        System.out.println(query);
        try {
            stmt = connection.createStatement();
        
            Integer userId = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
         
            user.setId((long)userId);
            user.setValid(true);
            
            
        } catch (SQLException ex) {
            
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Boolean update(Integer id, HashMap<String, String> data) {
        if (data.isEmpty()) {
            return true;
        }
        
        Statement stmt;
        Connection connection = ConnectionManager.getConnection();
        
        
        try {
            List<String> params = new ArrayList<>();
            
            for (String column : data.keySet()) {
                params.add(column + " = '"+data.get(column)+ "'");
            }
            String joinedParams = params.stream().collect(Collectors.joining(", "));
            
            
            String query = "UPDATE user SET "+ joinedParams + " WHERE id="+id;
            System.out.println(query);
            
            stmt = connection.createStatement();        
            stmt.executeUpdate(query);
            
            return true;
        } catch (SQLException ex) {            
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    public static Boolean getByLoginOrEmail(String login, String email) {
        Statement stmt;
        Connection connection = ConnectionManager.getConnection();
        ResultSet rs;
        System.out.println(connection);
        String query = "SELECT * FROM user WHERE "
                + " username='"+login+"'"
                + " OR email='"+email+"'";
        
        try {
            stmt = connection.createStatement();
        
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                return true;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    public static User login(User user) {
        Statement stmt;
        Connection connection = ConnectionManager.getConnection();
        ResultSet rs;
        user.setValid(false);
        
        String query = "SELECT * FROM user WHERE "
                + " username='"+user.getUsername()+"'"
                + " AND pass='"+user.getPassword()+"'";
        
        try {
            stmt = connection.createStatement();
        
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                user.setEmail(rs.getString("email"));
                user.setFirstname(rs.getString("firstname"));
                user.setSurname(rs.getString("surname"));
                user.setId(rs.getLong("id"));
                user.setPassword(rs.getString("pass"));
                user.setUsername(rs.getString("username"));
                user.setRank(rs.getString("rank"));
                user.setValid(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
         
            return user;
        }
        
        return user;
    }
}
