<%@ page language="java" contentType="text/html;charset=UTF-8"%>
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
String member_id = request.getParameter("member_id");
try{
  Class.forName("org.mariadb.jdbc.Driver");
  conn = DriverManager.getConnection(url,id,pwd);
  pstmt = conn.prepareStatement("select * from member_interest_tb where member_id = ?");
  pstmt.setString(1,member_id);
  rs=pstmt.executeQuery();
  JSONObject json = new JSONObject();
  JSONArray list = new JSONArray();
while(rs.next()){
  JSONObject temp = new JSONObject();
  temp.put("interest",rs.getString("content_type"));
  list.add(temp);
}
json.put("list",list);
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
