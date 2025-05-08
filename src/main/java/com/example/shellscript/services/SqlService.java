package com.example.shellscript.services;

import com.example.shellscript.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SqlService {

    @Autowired
    private Config systemConfig;

    public void processQuery() {
        if(systemConfig.getIsMariaDBServiceEnabled()) {
            /***
             *
             * Write Logics for mariadb or anyother sql service
             */
        }
    }
}
