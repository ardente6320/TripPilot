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
 String content_id = request.getParameter("content_id");
  try{
    Class.forName("org.mariadb.jdbc.Driver");
    conn = DriverManager.getConnection(url,id,pwd);
    pstmt = conn.prepareStatement("select AVG(star_rating) as average from star_rating_tb where content_id = ?");
    pstmt.setString(1,content_id);
    rs=pstmt.executeQuery();
    JSONObject json = new JSONObject();
    JSONObject temp=null;
    rs.next();
    if(!rs.wasNull()){
      json.put("star_rating",Float.toString(rs.getFloat("average")));
    }
    else{
      json.put("star_rating",Float.toString(0));
    }
     out.clear();
    out.print(json);
 }catch(SQLException sqlException){
    out.print("dberror");
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
