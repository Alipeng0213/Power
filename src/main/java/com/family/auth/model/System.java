package com.family.auth.model;

import lombok.Data;

import java.util.Date;

@Data
public class System {
    private Integer fid;

    private String fname;

    private String fdescription;

    private String furl;

    private String fclientId;

    private String fclientSecret;

    private String fredirectUrls;

    private Integer fimportance;

    private Date fcreateTime;

    private String fcreator;

    private Date fmodifyTime;

    private String fdeveloper;

    private Integer fstate;

    private String freason;

    private Integer ftwiceLoginEnabled;

    private Integer fexpireNotify;

    private Integer fcheckEnabled;

    private String fsign;

    private String fbackupAuditor;

    private Integer foaEnabled;
}