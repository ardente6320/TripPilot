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
try{
  String member_id = request.getParameter("member_id");
  String password = request.getParameter("password");
  String ac = request.getParameter("AC");
  String cc = request.getParameter("CC");
  String ec = request.getParameter("EC");
  String lc = request.getParameter("LC");
  String sc = request.getParameter("SC");
  String fc = request.getParameter("FC");
  Class.forName("org.mariadb.jdbc.Driver");
  conn = DriverManager.getConnection(url,id,pwd);
  if(!password.equals("N")){
     pstmt = conn.prepareStatement("update member_tb set password=? where id=?");
     pstmt.setString(1,password);
     pstmt.setString(2,member_id);
     pstmt.executeUpdate();
  }
  if(ac.equals("Y")){
      pstmt = conn.prepareStatement("select * from member_interest_tb where member_id =? and content_type =?");
      pstmt.setString(1,member_id);
      pstmt.setString(2,"12");
      rs = pstmt.executeQuery();
      if(!rs.next()){
         pstmt = conn.prepareStatement("insert into member_interest_tb values(?,?)");
         pstmt.setString(1,member_id);
         pstmt.setString(2,"12");
         pstmt.executeUpdate();
      }
  }
 else{
     pstmt = conn.prepareStatement("delete from member_interest_tb where member_id =? and content_type =?");
     pstmt.setString(1,member_id);
     pstmt.setString(2,"12");
     pstmt.executeUpdate();
 } 
 if(cc.equals("Y")){
      pstmt = conn.prepareStatement("select * from member_interest_tb where member_id =? and content_type =?");
      pstmt.setString(1,member_id);
      pstmt.setString(2,"14");
      rs = pstmt.executeQuery();
      if(!rs.next()){
         pstmt = conn.prepareStatement("insert into member_interest_tb values(?,?)");
         pstmt.setString(1,member_id);
         pstmt.setString(2,"14");
         pstmt.executeUpdate();
      }
  }
 else{
     pstmt = conn.prepareStatement("delete from member_interest_tb where member_id =? and content_type =?");
     pstmt.setString(1,member_id);
     pstmt.setString(2,"14");
     pstmt.executeUpdate();
 }
  if(ec.equals("Y")){
      pstmt = conn.prepareStatement("select * from member_interest_tb where member_id =? and content_type =?");
      pstmt.setString(1,member_id);
      pstmt.setString(2,"15");
      rs = pstmt.executeQuery();
      if(!rs.next()){
         pstmt = conn.prepareStatement("insert into member_interest_tb values(?,?)");
         pstmt.setString(1,member_id);
         pstmt.setString(2,"15");
         pstmt.executeUpdate();
      }
  }
 else{
     pstmt = conn.prepareStatement("delete from member_interest_tb where member_id =? and content_type =?");
     pstmt.setString(1,member_id);
     pstmt.setString(2,"15");
     pstmt.executeUpdate();
 }
  if(lc.equals("Y")){
      pstmt = conn.prepareStatement("select * from member_interest_tb where member_id =? and content_type =?");
      pstmt.setString(1,member_id);
      pstmt.setString(2,"28");
      rs = pstmt.executeQuery();
      if(!rs.next()){
         pstmt = conn.prepareStatement("insert into member_interest_tb values(?,?)");
         pstmt.setString(1,member_id);
         pstmt.setString(2,"28");
         pstmt.executeUpdate();
      }
  }
 else{
     pstmt = conn.prepareStatement("delete from member_interest_tb where member_id =? and content_type =?");
     pstmt.setString(1,member_id);
     pstmt.setString(2,"28");
     pstmt.executeUpdate();
 }
  if(sc.equals("Y")){
      pstmt = conn.prepareStatement("select * from member_interest_tb where member_id =? and content_type =?");
      pstmt.setString(1,member_id);
      pstmt.setString(2,"38");
      rs = pstmt.executeQuery();
      if(!rs.next()){
         pstmt = conn.prepareStatement("insert into member_interest_tb values(?,?)");
         pstmt.setString(1,member_id);
         pstmt.setString(2,"38");
         pstmt.executeUpdate();
      }
  }
 else{
     pstmt = conn.prepareStatement("delete from member_interest_tb where member_id =? and content_type =?");
     pstmt.setString(1,member_id);
     pstmt.setString(2,"38");
     pstmt.executeUpdate();
 }
  if(fc.equals("Y")){
      pstmt = conn.prepareStatement("select * from member_interest_tb where member_id =? and content_type =?");
      pstmt.setString(1,member_id);
      pstmt.setString(2,"39");
      rs = pstmt.executeQuery();
      if(!rs.next()){
         pstmt = conn.prepareStatement("insert into member_interest_tb values(?,?)");
         pstmt.setString(1,member_id);
         pstmt.setString(2,"39");
         pstmt.executeUpdate();
      }
  }
 else{
     pstmt = conn.prepareStatement("delete from member_interest_tb where member_id =? and content_type =?");
     pstmt.setString(1,member_id);
     pstmt.setString(2,"39");
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
