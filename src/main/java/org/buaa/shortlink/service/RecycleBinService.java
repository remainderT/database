package org.buaa.shortlink.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.buaa.shortlink.dao.entity.ShortLinkDO;
import org.buaa.shortlink.dto.req.RecycleBinRecoverReqDTO;
import org.buaa.shortlink.dto.req.RecycleBinRemoveReqDTO;
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


    /**
     * 从回收站恢复短链接
     *
     * @param requestParam 恢复短链接请求参数
     */
    void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam);

    /**
     * 从回收站彻底移除短链接
     *
     * @param requestParam 移除短链接请求参数
     */
    void removeRecycleBin(RecycleBinRemoveReqDTO requestParam);

}
