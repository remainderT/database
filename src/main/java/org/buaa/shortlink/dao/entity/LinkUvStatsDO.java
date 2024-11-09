package org.buaa.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.buaa.shortlink.common.database.BaseDO;

/**
 * uv访问实体
 */
@Data
@TableName("t_link_uv_stats")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkUvStatsDO extends BaseDO {

    /**
     * id
     */
    private Long id;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * uuid
     */
    private String uuid;

}
