package org.buaa.shortlink.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.buaa.shortlink.dao.entity.ShortLinkDO;
import org.buaa.shortlink.dto.req.ShortLinkCreateReqDTO;
import org.buaa.shortlink.dto.req.ShortLinkPageReqDTO;
import org.buaa.shortlink.dto.resp.ShortLinkCreateRespDTO;
import org.buaa.shortlink.dto.resp.ShortLinkPageRespDTO;

/**
 * 短链接接口层
 */
public interface ShortLinkService extends IService<ShortLinkDO> {

    /**
     * 创建短链接
     *
     * @param requestParam 创建短链接请求参数
     * @return 短链接创建信息
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    /**
     * 分页查询短链接
     *
     * @param requestParam 分页查询短链接请求参数
     * @return 短链接分页返回结果
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

}
