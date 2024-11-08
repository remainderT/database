package org.buaa.shortlink.dto.req;

import lombok.Data;

/**
 * 移至回收站
 */
@Data
public class RecycleBinSaveReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 完整短链接
     */
    private String fullShortUrl;
}
