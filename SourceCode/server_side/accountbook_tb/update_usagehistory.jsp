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
try{
  Class.forName("org.mariadb.jdbc.Driver");
  conn = DriverManager.getConnection(url,id,pwd);
  String content_id= request.getParameter("content_id");
  int money = Integer.parseInt(request.getParameter("money"));
  String usagehistory = request.getParameter("usage_history");
  String type = request.getParameter("type");
  String payment_method = request.getParameter("payment_method");
  
  pstmt = conn.prepareStatement("update usage_history_tb set money=?,usage_history=?,type=?,payment_method=? where content_id=?");
  pstmt.setInt(1,money);
  pstmt.setString(2,usagehistory);
  pstmt.setString(3,type);
  pstmt.setString(4,payment_method);
  pstmt.setString(5,content_id);
  pstmt.executeUpdate();
  out.clear();
}catch(SQLException sqlException){
  out.print("dberror");
}catch(Exception exception){
  out.print("error");
}finally{
  if( rs != null )
   try{ rs.close(); } catch(SQLException ex) {}
  if( stmt != null )
   try { stmt.close(); } catch(SQLException ex) {}
  if( conn != null )
   try{ conn.close(); } catch(Exception ex){}
}
%>
