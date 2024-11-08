package org.buaa.shortlink.dto.req;

import lombok.Data;

/**
 * 回收站彻底移除短链接
 */
@Data
public class RecycleBinRemoveReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 全部短链接
     */
    private String fullShortUrl;
}
