<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="org.json.simple.*" %>
<%
String url = "jdbc:mariadb://localhost:3306/trip";
String id = "root";
String pwd = "q1w2e3!@#";
PreparedStatement pstmt;
Statement stmt= null;
ResultSet rs= null;
Connection conn=null;
String schedule_id= request.getParameter("schedule_id");
int date = Integer.parseInt(request.getParameter("date"));
try{
  Class.forName("org.mariadb.jdbc.Driver");
  conn = DriverManager.getConnection(url,id,pwd);
  pstmt = conn.prepareStatement("update schedule_tb set date = ? where schedule_id = ?");
  pstmt.setInt(1,date);
  pstmt.setString(2,schedule_id);
  pstmt.executeUpdate();
  out.clear();
}catch(SQLException sqlException){
  out.print("dberror");
}catch(Exception exception){
  out.print("error");
}finally{
  out.print("success");
  if( rs != null )
   try{ rs.close(); } catch(SQLException ex) {}
  if( stmt != null )
   try { stmt.close(); } catch(SQLException ex) {}
  if( conn != null )
   try{ conn.close(); } catch(Exception ex){}
}
%>
