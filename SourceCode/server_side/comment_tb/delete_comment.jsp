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
  String comment_id = request.getParameter("comment_id");
  try{
    Class.forName("org.mariadb.jdbc.Driver");
    conn = DriverManager.getConnection(url,id,pwd);
    /*ÄÚ¸àÆ® µî·Ï*/
    pstmt = conn.prepareStatement("delete from comment_tb where comment_id=?");
    pstmt.setString(1,comment_id);
    pstmt.executeUpdate();
  out.clear();
 }catch(SQLException sqlException){
    out.print("dberror");
    out.print(sqlException.toString());
 }catch(Exception exception){
  //out.print("error");
 }finally{
  if( rs != null )
   try{ rs.close(); } catch(SQLException ex) {}
  if( stmt != null )
   try { stmt.close(); } catch(SQLException ex) {}
  if( conn != null )
   try{ conn.close(); } catch(Exception ex){}
}
%>
