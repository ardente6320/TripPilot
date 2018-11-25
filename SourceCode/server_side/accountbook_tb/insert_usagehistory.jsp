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
  Class.forName("org.mariadb.jdbc.Driver");
  conn = DriverManager.getConnection(url,id,pwd);
  String accountbook_id = request.getParameter("accountbook_id");
  String date = request.getParameter("date");
  int money = Integer.parseInt(request.getParameter("money"));
  String usagehistory = request.getParameter("usage_history");
  String type = request.getParameter("type");
  String payment_method = request.getParameter("payment_method");
  
  pstmt = conn.prepareStatement("select no from usage_history_tb where accountbook_id=? order by no");
  pstmt.setString(1,accountbook_id);
  rs = pstmt.executeQuery();
  int no =0;
  while(rs.next()){
    if(no == rs.getInt("no"))
        no++;
    else
      break;
  }
  String uh_id = accountbook_id+"-UH"+no;
  pstmt = conn.prepareStatement("insert into usage_history_tb values(?,?,?,?,?,?,?,?)");
  pstmt.setInt(1,no);
  pstmt.setString(2,uh_id);
  pstmt.setString(3,accountbook_id);
  pstmt.setString(4,date);
  pstmt.setInt(5,money);
  pstmt.setString(6,usagehistory);
  pstmt.setString(7,type);
  pstmt.setString(8,payment_method);
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
