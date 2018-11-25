<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="org.json.simple.*" %>
<%
String url = "jdbc:mariadb://localhost:3306/trip";
String id = "root";
String pwd = "q1w2e3!@#";
PreparedStatement pstmt;
Statement stmt= null;
ResultSet rs= null, sch_rs=null,star_rs = null;
Connection conn=null;

 String plan_id = request.getParameter("plan_id");
  try{
    Class.forName("org.mariadb.jdbc.Driver");
    conn = DriverManager.getConnection(url,id,pwd);
    JSONObject json = new JSONObject();
    JSONArray list = new JSONArray();
    pstmt = conn.prepareStatement("select * from schedule_tb where master_id = ? order by date");
    pstmt.setString(1,plan_id);
    rs = pstmt.executeQuery();
    int total_cnt = 0;
    while(rs.next()){
       JSONObject sch_json = new JSONObject();
       JSONArray sch_list = new JSONArray();
       String schedule_id = rs.getString("schedule_id");
       pstmt = conn.prepareStatement("select * from location_tb where master_schedule_id = ? order by sequence");
       pstmt.setString(1,schedule_id);
       sch_rs = pstmt.executeQuery();
       sch_json.put("schedule_id",schedule_id);
       int cnt =0;
       while(sch_rs.next()){
          JSONObject content = new JSONObject();
          content.put("location_id",sch_rs.getString("location_id"));
          String content_id = sch_rs.getString("content_id");
          content.put("content_id",content_id);
          pstmt = conn.prepareStatement("select AVG(star_rating) as star_rating from star_rating_tb where content_id = ?");
          pstmt.setString(1,content_id);
          star_rs = pstmt.executeQuery();
          if(star_rs.next())
             content.put("star_rating",star_rs.getFloat("star_rating"));
          else
             content.put("star_rating",0.0);
          content.put("sequence",sch_rs.getInt("sequence"));
          content.put("area_code",sch_rs.getInt("area_code"));
          content.put("sigungu_code",sch_rs.getInt("sigungu_code"));
          content.put("content_type",sch_rs.getInt("content_type"));
          sch_list.add(content);
          cnt++;
       }
       sch_json.put(Integer.toString(rs.getInt("date")),sch_list);
       if(cnt==0)
          sch_json.put("isEmpty","true");
       else
          sch_json.put("isEmpty","false");
       total_cnt++;
       list.add(sch_json);
    }
    json.put("total_day",total_cnt);
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
