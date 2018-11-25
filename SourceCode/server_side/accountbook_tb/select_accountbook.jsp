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
ResultSet uh_rs = null;
Connection conn=null;
String accountbook_id = request.getParameter("accountbook_id");
//String accountbook_id = "ac001";
try{
  Class.forName("org.mariadb.jdbc.Driver");
  conn = DriverManager.getConnection(url,id,pwd);
  pstmt = conn.prepareStatement("select * from usage_history_tb where accountbook_id = ? order by date");
  pstmt.setString(1,accountbook_id);
  rs=pstmt.executeQuery();
  JSONObject json = new JSONObject();
  JSONArray list = new JSONArray();
   while(rs.next()){
      JSONObject temp = new JSONObject();
      temp.put("content_id",rs.getString("content_id"));
      temp.put("date",rs.getString("date"));
      temp.put("money",rs.getInt("money"));
      temp.put("usage_history",rs.getString("usage_history"));
      temp.put("type",rs.getString("type"));
      temp.put("payment_method",rs.getString("payment_method"));
   list.add(temp);
  }
  json.put("list",list);
  out.clear();
  out.print(json);
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
