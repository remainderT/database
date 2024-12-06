package org.buaa.shortlink.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Alert {

    private String time;

    private String username;

    private String mail;

    private String ip;

    private String api;

}
