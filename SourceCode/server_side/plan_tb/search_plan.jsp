<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="org.json.simple.*" %>
<%@ page import="java.util.ArrayList"%>
<%
String url = "jdbc:mariadb://localhost:3306/trip";
String id = "root";
String pwd = "q1w2e3!@#";
PreparedStatement pstmt;
Statement stmt= null;
ResultSet rs= null;
ResultSet av_rs= null;
Connection conn=null;
JSONObject json = new JSONObject();
JSONArray list = new JSONArray();
String member_id = request.getParameter("member_id");
String keyword = request.getParameter("keyword");
int area_code = Integer.parseInt(request.getParameter("area_code"));
int sigungu_code = Integer.parseInt(request.getParameter("sigungu_code"));
int content_type = Integer.parseInt(request.getParameter("content_type"));
int cnt = Integer.parseInt(request.getParameter("cnt"));
cnt = (cnt-1)*10;
if(keyword.equals("N"))
 keyword = "";
try{
  Class.forName("org.mariadb.jdbc.Driver");
  conn = DriverManager.getConnection(url,id,pwd);
  pstmt = conn.prepareStatement("select count(*) as cnt from plan_tb as p, (select count(loc.area_code) as area, count(loc.sigungu_code) as sigungu, count(loc.content_type) as content_type, sch.master_id from location_tb as loc ,schedule_tb as sch where sch.schedule_id = loc.master_schedule_id and loc.area_code = ? and loc.sigungu_code = ? and loc.content_type = ? group by sch.master_id) as location where  p.scope NOT IN(0) and p.plan_id = location.master_id and p.member_id NOT IN(?)");
  pstmt.setInt(1,area_code);
  pstmt.setInt(2,sigungu_code);
  pstmt.setInt(3,content_type);
  pstmt.setString(4,member_id);
  rs = pstmt.executeQuery();
  if(rs.next())
     json.put("totalCount", rs.getInt("cnt"));
  else
     json.put("totalCount",0);
  pstmt = conn.prepareStatement("select p.*,location.area,location.content_type from plan_tb as p, (select count(loc.area_code) as area, count(loc.sigungu_code) as sigungu, count(loc.content_type) as content_type, sch.master_id from location_tb as loc ,schedule_tb as sch where sch.schedule_id = loc.master_schedule_id and loc.area_code = ? and loc.sigungu_code = ? and loc.content_type = ? group by sch.master_id) as location where  p.scope NOT IN(0) and p.plan_id = location.master_id and p.member_id NOT IN(?) and p.title like ? group by p.plan_id order by area DESC, content_type DESC limit ?,10");
  pstmt.setInt(1,area_code);
  pstmt.setInt(2,sigungu_code);
  pstmt.setInt(3,content_type);
  pstmt.setString(4, member_id);
  pstmt.setString(5,"%"+keyword+"%");
  pstmt.setInt(6, cnt);
  rs=pstmt.executeQuery();
  while(rs.next()){
	JSONObject temp = new JSONObject();
	String plan_id = rs.getString("plan_id");
    	temp.put("plan_id",plan_id);
             temp.put("member_id",rs.getString("member_id"));
    	temp.put("title",rs.getString("title"));
    	temp.put("scope",rs.getBoolean("scope"));
    	temp.put("createdtime",rs.getString("createdtime"));
             temp.put("area_cnt",rs.getInt("area"));
             temp.put("content_type_cnt",rs.getInt("content_type"));
             pstmt = conn.prepareStatement(" select AVG(star_rating) as average from star_rating_tb where content_id =?");
    	pstmt.setString(1,plan_id);
    	av_rs = pstmt.executeQuery();
    	av_rs.next();
    	if(!av_rs.wasNull()){
       		String avg = String.format("%.1f",av_rs.getFloat("average"));
       		temp.put("star_rating",avg);
   	 }
   	 else{
     		  temp.put("star_rating",0);
    	}
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
