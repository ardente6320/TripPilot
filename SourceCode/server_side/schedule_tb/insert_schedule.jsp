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
String plan_id = request.getParameter("master_id");
int date = Integer.parseInt(request.getParameter("date"));
int no = 0;
try{
  Class.forName("org.mariadb.jdbc.Driver");
  conn = DriverManager.getConnection(url,id,pwd);
  pstmt = conn.prepareStatement("select no from schedule_tb where master_id =?");
  pstmt.setString(1,plan_id);
  rs = pstmt.executeQuery();
  while(rs.next()){
    if(no == rs.getInt("no"))
        no++;
    else
      break;
  }
  String schedule_id = plan_id+"-SCH"+no;
  pstmt = conn.prepareStatement("insert into schedule_tb values(?,?,?,?)");
  pstmt.setInt(1,no);
  pstmt.setString(2,schedule_id);
  pstmt.setString(3,plan_id);
  pstmt.setInt(4,date);
  pstmt.executeUpdate();
  JSONObject json = new JSONObject();
  json.put("schedule_id",schedule_id);
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
