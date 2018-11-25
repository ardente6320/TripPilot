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
String member_id = request.getParameter("member_id");
String title = request.getParameter("title");
boolean scope = Boolean.parseBoolean(request.getParameter("scope"));
String createdtime = request.getParameter("createdtime");
int no =0;
try{
  Class.forName("org.mariadb.jdbc.Driver");
  conn = DriverManager.getConnection(url,id,pwd);
  pstmt=conn.prepareStatement("select no from plan_tb where member_id = ? order by no");
  pstmt.setString(1,member_id);
  rs = pstmt.executeQuery();
  while(rs.next()){
    if(no == rs.getInt("no"))
        no++;
    else
      break;
  }
  String plan_id = member_id+"-P"+no;
  pstmt = conn.prepareStatement("insert into plan_tb values(?,?,?,?,?,?)");
  pstmt.setInt(1,no);
  pstmt.setString(2,plan_id);
  pstmt.setString(3,member_id);
  pstmt.setString(4,title);
  pstmt.setBoolean(5,scope);
  pstmt.setString(6,createdtime);
  pstmt.executeUpdate();
  JSONObject json = new JSONObject();
  json.put("plan_id",plan_id);
  out.clear();
  out.print(json);
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
