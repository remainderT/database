package org.buaa.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.buaa.shortlink.dao.entity.LinkLocaleStatsDO;
import org.buaa.shortlink.dto.req.ShortLinkStatsReqDTO;

import java.util.List;

/**
 * 地区统计访问持久层
 */
public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStatsDO> {

    /**
     * 记录地区访问监控数据
     */
    @Insert("INSERT INTO " +
            "t_link_locale_stats (full_short_url, date, cnt, country, province, city, adcode, create_time, update_time, del_flag) " +
            "VALUES( #{linkLocaleStats.fullShortUrl}, #{linkLocaleStats.date}, #{linkLocaleStats.cnt}, #{linkLocaleStats.country}, #{linkLocaleStats.province}, #{linkLocaleStats.city}, #{linkLocaleStats.adcode}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkLocaleStats.cnt};")
    void shortLinkLocaleState(@Param("linkLocaleStats") LinkLocaleStatsDO linkLocaleStatsDO);

    /**
     * 根据短链接获取指定日期内地区监控数据
     */
    @Select("SELECT " +
            "    tlls.province, " +
            "    SUM(tlls.cnt) AS cnt " +
            "FROM " +
            "    t_link tl INNER JOIN " +
            "    t_link_locale_stats tlls ON tl.full_short_url = tlls.full_short_url " +
            "WHERE " +
            "    tlls.full_short_url = #{param.fullShortUrl} " +
            "    AND tl.del_flag = '0' " +
            "    AND tlls.date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    tlls.full_short_url, tlls.province;")
    List<LinkLocaleStatsDO> listLocaleByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

}
