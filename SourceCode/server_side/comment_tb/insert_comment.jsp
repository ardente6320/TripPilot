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
  int content_type = Integer.parseInt(request.getParameter("content_type"));
  String writer_id = request.getParameter("writer_id");
  String content_id = request.getParameter("content_id");
  String content = request.getParameter("content");
  float star_rating = Float.parseFloat(request.getParameter("star_rating"));
  String createdtime = request.getParameter("createdtime");
  int no= 0;
  String comment_id = null;
  try{
    Class.forName("org.mariadb.jdbc.Driver");
    conn = DriverManager.getConnection(url,id,pwd);
    /*코멘트 등록*/
  pstmt=conn.prepareStatement("select no from comment_tb where content_id = ? order by no");
  pstmt.setString(1,content_id);
  rs = pstmt.executeQuery();
  while(rs.next()){
    if(no == rs.getInt("no"))
        no++;
    else
      break;
  }
    comment_id = content_id+"cmt-"+Integer.toString(no);
    pstmt = conn.prepareStatement("insert into comment_tb values(?,?,?,?,?,?,?)");
    pstmt.setInt(1,no);
    pstmt.setString(2,comment_id);
    pstmt.setInt(3,content_type);
    pstmt.setString(4,writer_id);
    pstmt.setString(5,content_id);
    pstmt.setString(6,content);
    pstmt.setString(7,createdtime);
    pstmt.executeUpdate();
    /*코멘트에 대한 별점 등록*/
    pstmt = conn.prepareStatement("insert into star_rating_tb values(?,?,?)");
    pstmt.setString(1,comment_id);
    pstmt.setString(2,content_id);
    pstmt.setFloat(3,star_rating);
    pstmt.executeUpdate();
  out.clear();
 }catch(SQLException sqlException){
    out.print("dberror");
    out.print(sqlException.toString());
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
