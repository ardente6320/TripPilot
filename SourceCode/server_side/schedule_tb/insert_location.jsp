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
String schedule_id = request.getParameter("master_schedule_id");
String location_id = request.getParameter("location_id");
String content_id = request.getParameter("content_id");
int sequence = Integer.parseInt(request.getParameter("sequence"));
int area_code = Integer.parseInt(request.getParameter("area_code"));
int sigungu = Integer.parseInt(request.getParameter("sigungu_code"));
int content_type = Integer.parseInt(request.getParameter("content_type"));
int no = 0;
try{
  Class.forName("org.mariadb.jdbc.Driver");
  conn = DriverManager.getConnection(url,id,pwd);
  pstmt = conn.prepareStatement("select * from location_tb where location_id = ?");
  pstmt.setString(1,location_id);
  rs = pstmt.executeQuery();
  if(rs.next()){
     pstmt = conn.prepareStatement("update location_tb set sequence = ? where location_id = ?");
     pstmt.setInt(1,sequence);
     pstmt.setString(2,location_id);
     pstmt.executeUpdate();
  }
  else{
    pstmt = conn.prepareStatement("select no from location_tb where master_schedule_id =?");
    pstmt.setString(1,schedule_id);
    rs = pstmt.executeQuery();
    while(rs.next()){
       if(no == rs.getInt("no"))
         no++;
       else
         break;
     }
     location_id = schedule_id+"LOC-"+no;
     pstmt = conn.prepareStatement("insert into location_tb values(?,?,?,?,?,?,?,?)"); 
     pstmt.setInt(1,no);
     pstmt.setString(2,location_id);
     pstmt.setString(3,content_id);
     pstmt.setString(4,schedule_id);
     pstmt.setInt(5,sequence);
     pstmt.setInt(6,area_code);
     pstmt.setInt(7,sigungu);
     pstmt.setInt(8,content_type);
     pstmt.executeUpdate();
  }
  out.clear();
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
