package org.buaa.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.buaa.shortlink.dao.entity.ShortLinkDO;

/**
 * 短链接持久层
 */
public interface ShortLinkMapper extends BaseMapper<ShortLinkDO> {

    /**
     * 短链接访问统计自增
     */
    @Update("UPDATE t_link" +
            " SET total_pv  = total_pv + #{totalPv}," +
            " total_uv  = total_uv + #{totalUv}," +
            " total_uip = total_uip + #{totalUip} " +
            " WHERE full_short_url = #{fullShortUrl}; " )
    void incrementStats(@Param("fullShortUrl") String fullShortUrl,
                        @Param("totalPv") Integer totalPv,
                        @Param("totalUv") Integer totalUv,
                        @Param("totalUip") Integer totalUip);

}
