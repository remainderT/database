package org.buaa.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.buaa.shortlink.dao.entity.LinkAccessStatsDO;
import org.buaa.shortlink.dto.req.ShortLinkStatsReqDTO;

import java.util.List;

/**
 * 短链接基础访问监控持久层
 */
public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {

    /**
     * 记录基础访问监控数据
     */
    @Insert("INSERT INTO t_link_access_stats (full_short_url, date, pv, uv, uip, hour, weekday, create_time, update_time, del_flag) " +
            "VALUES( #{linkAccessStats.fullShortUrl}, #{linkAccessStats.date}, #{linkAccessStats.pv}, #{linkAccessStats.uv}, #{linkAccessStats.uip}, #{linkAccessStats.hour}, #{linkAccessStats.weekday}, NOW(), NOW(), 0) ON DUPLICATE KEY UPDATE pv = pv +  #{linkAccessStats.pv}, " +
            "uv = uv + #{linkAccessStats.uv}, " +
            " uip = uip + #{linkAccessStats.uip};")
    void shortLinkStats(@Param("linkAccessStats") LinkAccessStatsDO linkAccessStatsDO);

    /**
     * 根据短链接获取指定日期内基础监控数据
     */
    @Select("SELECT " +
            "    tlas.date, " +
            "    SUM(tlas.pv) AS pv, " +
            "    SUM(tlas.uv) AS uv, " +
            "    SUM(tlas.uip) AS uip " +
            "FROM " +
            "    t_link tl INNER JOIN " +
            "    t_link_access_stats tlas ON tl.full_short_url = tlas.full_short_url " +
            "WHERE " +
            "    tlas.full_short_url = #{param.fullShortUrl} " +
            "    AND tl.del_flag = '0' " +
            "    AND tlas.date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    tlas.full_short_url, tlas.date;")
    List<LinkAccessStatsDO> listStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);


    /**
     * 根据短链接获取指定日期内小时基础监控数据
     */
    @Select("SELECT " +
            "    tlas.hour, " +
            "    SUM(tlas.pv) AS pv " +
            "FROM " +
            "    t_link tl INNER JOIN " +
            "    t_link_access_stats tlas ON tl.full_short_url = tlas.full_short_url " +
            "WHERE " +
            "    tlas.full_short_url = #{param.fullShortUrl} " +
            "    AND tl.del_flag = '0' " +
            "    AND tlas.date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    tlas.full_short_url, tlas.hour;")
    List<LinkAccessStatsDO> listHourStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内小时基础监控数据
     */
    @Select("SELECT " +
            "    tlas.weekday, " +
            "    SUM(tlas.pv) AS pv " +
            "FROM " +
            "    t_link tl INNER JOIN " +
            "    t_link_access_stats tlas ON tl.full_short_url = tlas.full_short_url " +
            "WHERE " +
            "    tlas.full_short_url = #{param.fullShortUrl} " +
            "    AND tl.del_flag = '0' " +
            "    AND tlas.date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    tlas.full_short_url, tlas.weekday;")
    List<LinkAccessStatsDO> listWeekdayStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

}
