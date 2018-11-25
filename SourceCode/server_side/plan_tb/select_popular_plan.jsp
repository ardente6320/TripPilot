<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="org.json.simple.*" %>
<%
String url = "jdbc:mariadb://localhost:3306/trip";
String id = "root";
String pwd = "q1w2e3!@#";
PreparedStatement pstmt;
Statement stmt= null;
ResultSet rs= null,av_rs=null;
Connection conn=null;
try{
  Class.forName("org.mariadb.jdbc.Driver");
  conn = DriverManager.getConnection(url,id,pwd);
 pstmt = conn.prepareStatement("select p.*, AVG(star.star_rating) as average from plan_tb as p left join star_rating_tb as star on p.plan_id = star.content_id where p.scope NOT IN(0) group by p.plan_id order by average DESC limit 5");
  rs=pstmt.executeQuery();
 JSONObject json = new JSONObject();
 JSONArray list = new JSONArray();
  String plan_id;
  while(rs.next()){
    JSONObject temp = new JSONObject();
    if(!rs.wasNull()){
       String avg = String.format("%.1f",rs.getFloat("average"));
       temp.put("star_rating",avg);
    }
    else{
       continue;
    }
    plan_id = rs.getString("plan_id");
    temp.put("plan_id",rs.getString("plan_id"));
    temp.put("member_id",rs.getString("member_id"));
    temp.put("title",rs.getString("title"));
    temp.put("scope",rs.getBoolean("scope"));
    temp.put("createdtime",rs.getString("createdtime"));
   pstmt = conn.prepareStatement("select loc.content_id from schedule_tb as sch, location_tb as loc where sch.master_id = ? and sch.schedule_id = loc.master_schedule_id limit 1");
    pstmt.setString(1,plan_id);
    av_rs = pstmt.executeQuery();
    if(av_rs.next()){
       temp.put("img",av_rs.getString("content_id"));
    }
    else{
       temp.put("img","N");
    }
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
