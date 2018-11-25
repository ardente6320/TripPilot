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
ResultSet star_rs= null;
Connection conn=null;
 String content_id = request.getParameter("content_id");
  try{
    Class.forName("org.mariadb.jdbc.Driver");
    conn = DriverManager.getConnection(url,id,pwd);
    pstmt = conn.prepareStatement("select * from comment_tb where content_id=?");
    pstmt.setString(1,content_id);
    rs=pstmt.executeQuery();
    JSONObject json = new JSONObject();
    JSONArray list = new JSONArray();
    JSONArray tempList =null;
    while(rs.next()){
	JSONObject temp= new JSONObject();
	String comment_id = rs.getString("comment_id");
	temp.put("comment_id",comment_id);
	temp.put("writer_id",rs.getString("writer_id"));	
	temp.put("content",rs.getString("content"));
	temp.put("createdtime",rs.getString("createdtime"));
      	pstmt = conn.prepareStatement("select star_rating from star_rating_tb where comment_key = ? and content_id=?");
    	pstmt.setString(1,comment_id);
	pstmt.setString(2,content_id);
    	star_rs=pstmt.executeQuery();
	star_rs.next();
	if(!star_rs.wasNull()){
		temp.put("star_rating",Float.toString(star_rs.getFloat("star_rating")));
	}
	else{
		temp.put("star_rating",Float.toString(0));
	}
	list.add(temp);
	json.put("list",list);
	temp = null;
    }
    pstmt = conn.prepareStatement("select AVG(star_rating) as average from star_rating_tb where content_id=?");
    pstmt.setString(1,content_id);
    rs=pstmt.executeQuery();
    if(rs.next()){
      String tmp = String.format("%.1f",rs.getFloat("average"));
      json.put("average",tmp);
    }
    else{
        json.put("average",0);
    }
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
