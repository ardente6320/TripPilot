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
String member_id = request.getParameter("member_id");
try{
  Class.forName("org.mariadb.jdbc.Driver");
  conn = DriverManager.getConnection(url,id,pwd);
  pstmt = conn.prepareStatement("select * from account_book_tb where member_id =?");
  pstmt.setString(1,member_id);
  rs=pstmt.executeQuery();
  JSONObject json = new JSONObject();
  JSONArray list = new JSONArray();
  int count = 0;
   while(rs.next()){
    JSONObject book_obj = new JSONObject();
    JSONArray usage_list = new JSONArray();
    String accountbook_id = rs.getString("accountbook_id");
    String title = rs.getString("title");
    pstmt = conn.prepareStatement("select * from usage_history_tb where accountbook_id = ? order by date");
    pstmt.setString(1,accountbook_id);
    uh_rs = pstmt.executeQuery();
    while(uh_rs.next()){
      JSONObject temp = new JSONObject();
      temp.put("content_id",uh_rs.getString("content_id"));
      temp.put("date",uh_rs.getString("date"));
      temp.put("money",uh_rs.getInt("money"));
      temp.put("usage_history",uh_rs.getString("usage_history"));
      temp.put("type",uh_rs.getString("type"));
      temp.put("payment_method",uh_rs.getString("payment_method"));
      usage_list.add(temp);
   }
   book_obj.put("title",title);
   book_obj.put("accountbook_id",accountbook_id);
   book_obj.put("usage_list",usage_list);
   list.add(book_obj);
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
