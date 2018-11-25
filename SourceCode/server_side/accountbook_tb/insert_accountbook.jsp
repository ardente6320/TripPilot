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
String title = request.getParameter("title");
String createdtime = request.getParameter("createdtime");
int no =1;
try{
  Class.forName("org.mariadb.jdbc.Driver");
  conn = DriverManager.getConnection(url,id,pwd);
  pstmt = conn.prepareStatement("select no from account_book_tb where member_id = ? order by no");
  pstmt.setString(1,member_id);
  rs = pstmt.executeQuery();
  while(rs.next()){
    if(no == rs.getInt("no"))
        no++;
    else
      break;
  }

  String account_id = member_id+"-AB"+no;
  pstmt = conn.prepareStatement("insert into account_book_tb values(?,?,?,?,?)");
  pstmt.setInt(1,no);
  pstmt.setString(2,account_id);
  pstmt.setString(3,member_id);
  pstmt.setString(4,title);
  pstmt.setString(5,createdtime);
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
