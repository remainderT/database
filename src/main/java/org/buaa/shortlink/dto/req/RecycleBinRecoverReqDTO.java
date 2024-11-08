package org.buaa.shortlink.dto.req;

import lombok.Data;

/**
 * 从回收站恢复
 */
@Data
public class RecycleBinRecoverReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 全部短链接
     */
    private String fullShortUrl;
}
