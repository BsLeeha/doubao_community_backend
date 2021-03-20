package com.douyuehan.doubao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.douyuehan.doubao.model.dto.CreateArticleDTO;
import com.douyuehan.doubao.model.entity.BmsArticle;
import com.douyuehan.doubao.model.entity.UmsUser;
import com.douyuehan.doubao.model.vo.ArticleVO;

import java.util.List;
import java.util.Map;


public interface IBmsArticleService extends IService<BmsArticle> {

    /**
     * 获取文章列表
     *
     * @param page
     * @param tab
     * @return
     */
    Page<ArticleVO> getList(Page<ArticleVO> page, String tab);

    /**
     * 发布
     *
     * @param dto
     * @param principal
     * @return
     */
    BmsArticle create(CreateArticleDTO dto, UmsUser principal);

    /**
     * 查看话题详情
     *
     * @param id
     * @return
     */
    Map<String, Object> viewTopic(String id);

    /**
     * 获取随机推荐10篇
     *
     * @param id
     * @return
     */
    List<BmsArticle> getRecommend(String articleId);

    /**
     * 关键字检索
     *
     * @param keyword
     * @param page
     * @return
     */
    Page<ArticleVO> searchByKey(String keyword, Page<ArticleVO> page);

    /**
     * 文章更新
     * @param entity 文章
     * @return boolean
     * @author liyonghai
     * @date 2021/3/18 23:22
     */
    @Override
    boolean updateById(BmsArticle entity);
}
