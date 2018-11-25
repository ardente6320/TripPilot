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
  String content = request.getParameter("content");
  Float star_rating = Float.parseFloat(request.getParameter("star_rating"));
  String createdtime  = request.getParameter("createdtime");
  try{
    Class.forName("org.mariadb.jdbc.Driver");
    conn = DriverManager.getConnection(url,id,pwd);
    /*ÄÚ¸àÆ® ¼öÁ¤*/
    pstmt = conn.prepareStatement("update comment_tb set content = ?, createdtime= ? where comment_id=?");
    pstmt.setString(1,content);
    pstmt.setString(2,createdtime);
    pstmt.setString(3,comment_id);
    pstmt.executeUpdate();
    pstmt = conn.prepareStatement("update star_rating_tb set star_rating = ? where comment_key = ?");
    pstmt.setFloat(1,star_rating);
    pstmt.setString(2,comment_id);
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
