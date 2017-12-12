package com.vimensa.core.service;

import com.vimensa.core.dao.AppData;

import java.sql.SQLException;
import java.util.List;

public interface AppService {
    public List<AppData> getAllApp() throws SQLException;
}
