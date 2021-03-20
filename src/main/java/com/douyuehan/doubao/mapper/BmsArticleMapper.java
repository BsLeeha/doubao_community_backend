package com.douyuehan.doubao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyuehan.doubao.model.entity.BmsArticle;
import com.douyuehan.doubao.model.vo.ArticleVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BmsArticleMapper extends BaseMapper<BmsArticle> {
    /**
     * 分页查询首页话题列表
     * <p>
     *
     * @param page
     * @param tab
     * @return
     */
    Page<ArticleVO> selectListAndPage(@Param("page") Page<ArticleVO> page, @Param("tab") String tab);

    /**
     * 获取详情页推荐
     *
     * @param id
     * @return
     */
    List<BmsArticle> selectRecommend(@Param("articleId") String articleId);
    /**
     * 全文检索
     *
     * @param page
     * @param keyword
     * @return
     */
    Page<ArticleVO> searchByKey(@Param("page") Page<ArticleVO> page, @Param("keyword") String keyword);
}
