package com.vimensa.core.serviceimp;

import com.vimensa.core.dao.AppData;
import com.vimensa.core.pools.HikariPool;
import com.vimensa.core.service.AppService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppServiceImp implements AppService {
    @Override
    public List<AppData> getAllApp() throws SQLException {
        Connection connection=HikariPool.getConnection();
        List<AppData> list=new ArrayList<>();
        try{
            String query="SELECT * from app";
            PreparedStatement st=connection.prepareStatement(query);
            ResultSet rs=st.executeQuery();
            while (rs.next()){
                AppData appData=new AppData();
                appData.setId(rs.getString("id"));
                appData.setAppName(rs.getString("appName"));
                list.add(appData);
            }
        }catch (Exception e){
        }finally {
            connection.close();
        }
        return list;
    }
}
