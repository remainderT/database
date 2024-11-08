package org.buaa.shortlink.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.buaa.shortlink.dao.entity.ShortLinkDO;
import org.buaa.shortlink.dto.req.RecycleBinSaveReqDTO;

/**
 * 回收站管理接口层
 */
public interface RecycleBinService extends IService<ShortLinkDO> {

    /**
     * 移至回收站
     *
     * @param requestParam 请求参数
     */
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);

}
