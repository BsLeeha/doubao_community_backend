package com.douyuehan.doubao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.mapper.BmsTagMapper;
import com.douyuehan.doubao.mapper.BmsArticleMapper;
import com.douyuehan.doubao.mapper.UmsUserMapper;
import com.douyuehan.doubao.model.dto.CreateArticleDTO;
import com.douyuehan.doubao.model.entity.BmsArticle;
import com.douyuehan.doubao.model.entity.BmsTag;
import com.douyuehan.doubao.model.entity.BmsTopicTag;
import com.douyuehan.doubao.model.entity.UmsUser;
import com.douyuehan.doubao.model.vo.ArticleVO;
import com.douyuehan.doubao.model.vo.ProfileVO;
import com.douyuehan.doubao.service.IBmsArticleService;
import com.douyuehan.doubao.service.IBmsTagService;
import com.douyuehan.doubao.service.IBmsTopicTagService;
import com.douyuehan.doubao.service.IUmsUserService;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class IBmsArticleServiceImpl extends ServiceImpl<BmsArticleMapper, BmsArticle> implements IBmsArticleService {
    @Resource
    private BmsTagMapper bmsTagMapper;
    @Resource
    private UmsUserMapper umsUserMapper;

    @Autowired
    @Lazy
    private IBmsTagService iBmsTagService;

    @Autowired
    private IUmsUserService iUmsUserService;

    @Autowired
    private IBmsTopicTagService iBmsTopicTagService;

    /**
     * 获取文章列表
     *
     * @param [page, tab]
     * @return 文章列表
     * @author liyonghai
     * @date 2021/3/14 23:26
     */
    @Override
    public Page<ArticleVO> getList(Page<ArticleVO> page, String tab) {
        // 查询文章
        Page<ArticleVO> iPage = this.baseMapper.selectListAndPage(page, tab);
        // 查询文章的标签
        setTopicTags(iPage);
        return iPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BmsArticle create(CreateArticleDTO dto, UmsUser user) {
        BmsArticle topic1 = this.baseMapper.selectOne(new LambdaQueryWrapper<BmsArticle>().eq(BmsArticle::getTitle, dto.getTitle()));
        Assert.isNull(topic1, "文章已存在，请修改");

        // 封装
        BmsArticle article = BmsArticle.builder()
                .userId(user.getId())
                .title(dto.getTitle())
                .content(EmojiParser.parseToAliases(dto.getContent()))
                .createTime(new Date())
                .build();
        this.baseMapper.insert(article);

        // 用户积分增加
        int newScore = user.getScore() + 1;
        umsUserMapper.updateById(user.setScore(newScore));

        // 标签
        if (!ObjectUtils.isEmpty(dto.getTags())) {
            // 保存标签
            List<BmsTag> tags = iBmsTagService.insertTags(dto.getTags());
            // 处理标签与文章的关联
            iBmsTopicTagService.createTopicTag(article.getId(), tags);
        }

        return article;
    }

    @Override
    public Map<String, Object> viewTopic(String id) {
        Map<String, Object> map = new HashMap<>(16);
        BmsArticle topic = this.baseMapper.selectById(id);
        Assert.notNull(topic, "当前文章不存在,或已被作者删除");
        // 查询文章详情
        topic.setView(topic.getView() + 1);
        this.baseMapper.updateById(topic);
        // emoji转码
        topic.setContent(EmojiParser.parseToUnicode(topic.getContent()));
        map.put("topic", topic);
        // 标签
        QueryWrapper<BmsTopicTag> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(BmsTopicTag::getTopicId, topic.getId());
        Set<String> set = new HashSet<>();
        for (BmsTopicTag articleTag : iBmsTopicTagService.list(wrapper)) {
            set.add(articleTag.getTagId());
        }
        List<BmsTag> tags = iBmsTagService.listByIds(set);
        map.put("tags", tags);

        // 作者
        ProfileVO user = iUmsUserService.getUserProfile(topic.getUserId());
        map.put("user", user);

        return map;
    }

    @Override
    public List<BmsArticle> getRecommend(String articleId) {
        return this.baseMapper.selectRecommend(articleId);
    }

    @Override
    public Page<ArticleVO> searchByKey(String keyword, Page<ArticleVO> page) {
        // 查询文章
        Page<ArticleVO> iPage = this.baseMapper.searchByKey(page, keyword);
        // 查询文章的标签
        setTopicTags(iPage);
        return iPage;
    }

    private void setTopicTags(Page<ArticleVO> iPage) {
        iPage.getRecords().forEach(topic -> {
            List<BmsTopicTag> topicTags = iBmsTopicTagService.selectByTopicId(topic.getId());
            if (!topicTags.isEmpty()) {
                List<String> tagIds = topicTags.stream().map(BmsTopicTag::getTagId).collect(Collectors.toList());
                List<BmsTag> tags = bmsTagMapper.selectBatchIds(tagIds);
                topic.setTags(tags);
            }
        });
    }

    /**
     * 根据 id 更新
     * @param entity 实体类
     * @return boolean
     * @author liyonghai
     * @date 2021/3/18 23:25
     */
    @Override
    public boolean updateById(BmsArticle entity) {
        // 修改时间
        entity.setModifyTime(new Date());
        return this.baseMapper.updateById(entity) == 1;
    }
}
