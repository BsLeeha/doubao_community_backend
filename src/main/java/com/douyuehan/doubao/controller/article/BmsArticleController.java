package com.douyuehan.doubao.controller.article;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.controller.BaseController;
import com.douyuehan.doubao.model.dto.CreateArticleDTO;
import com.douyuehan.doubao.model.entity.BmsArticle;
import com.douyuehan.doubao.model.entity.UmsUser;
import com.douyuehan.doubao.model.vo.ArticleVO;
import com.douyuehan.doubao.service.IBmsArticleService;
import com.douyuehan.doubao.service.IUmsUserService;
import com.vdurmont.emoji.EmojiParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static com.douyuehan.doubao.jwt.JwtUtil.USER_NAME;


/**
 * 文章控制器
 *
 * @author liyonghai
 * @date 2021/3/14 23:15
 * @version 0.1
 */
@RestController
@RequestMapping("/article")
public class BmsArticleController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(BmsArticleController.class);

    @Resource
    private IBmsArticleService iBmsArticleService;
    @Resource
    private IUmsUserService umsUserService;

    /**
     * 获取文章列表
     * 最新文章获取策略：TODO
     * 最热文章获取策略：TODO
     *
     * @param type 文章类型：latest-最新、hot-最热门
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @return 文章列表
     * @author liyonghai
     * @date 2021/3/14 23:16
     */
    @GetMapping("/list")
    public ApiResult<Page<ArticleVO>> list(@RequestParam(value = "type", defaultValue = "latest") String type,
                                           @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                           @RequestParam(value = "size", defaultValue = "10") Integer pageSize) {
        Page<ArticleVO> list = iBmsArticleService.getList(new Page<>(pageNo, pageSize), type);
        return ApiResult.success(list);
    }

    /**
     * 创建文章
     *
     * @param userName 用户名
     * @param dto 文章创建 dto
     * @return 创建的文章详情
     * @author liyonghai
     * @date 2021/3/18 23:15
     */
    @PostMapping(value = "/create")
    public ApiResult<BmsArticle> create(@RequestHeader(value = USER_NAME) String userName
            , @RequestBody CreateArticleDTO dto) {
        UmsUser user = umsUserService.getUserByUsername(userName);
        BmsArticle article = iBmsArticleService.create(dto, user);
        return ApiResult.success(article);
    }

    /**
     * 查看文章
     *
     * @param articleId 文章 id
     * @return 文章
     * @author liyonghai
     * @date 2021/3/18 23:52
     */
    @GetMapping
    public ApiResult<Map<String, Object>> view(@RequestParam("articleId") String articleId) {
        Map<String, Object> map = iBmsArticleService.viewTopic(articleId);
        return ApiResult.success(map);
    }

    /**
     * 获取推荐文章：非当期文章的随机 10 篇文章
     *
     * @param articleId 当期文章id
     * @return 推荐文章列表
     * @author liyonghai
     * @date 2021/3/18 23:19
     */
    @GetMapping("/recommend")
    public ApiResult<List<BmsArticle>> getRecommend(@RequestParam("articleId") String articleId) {
        List<BmsArticle> articles = iBmsArticleService.getRecommend(articleId);
        return ApiResult.success(articles);
    }

    /**
     * 文章更新
     *
     * @param userName 用户名
     * @param article 文章
     * @return com.douyuehan.doubao.common.api.ApiResult<com.douyuehan.doubao.model.entity.BmsArticle>
     * @author liyonghai
     * @date 2021/3/18 23:34
     */
    @PostMapping("/update")
    public ApiResult<BmsArticle> update(@RequestHeader(value = USER_NAME) String userName, @Valid @RequestBody BmsArticle article) {
        UmsUser umsUser = umsUserService.getUserByUsername(userName);
        Assert.isTrue(umsUser.getId().equals(article.getUserId()), "非本人无权修改");
        article.setContent(EmojiParser.parseToAliases(article.getContent()));
        iBmsArticleService.updateById(article);
        return ApiResult.success(article);
    }

    /**
     * 删除文章
     *
     * @param userName 用户名
     * @param articleId 文章 id
     * @return 删除结果
     * @author liyonghai
     * @date 2021/3/18 23:51
     */
    @DeleteMapping("/delete/{id}")
    public ApiResult<String> delete(@RequestHeader(value = USER_NAME) String userName, @PathVariable("articleId") String articleId) {
        UmsUser umsUser = umsUserService.getUserByUsername(userName);
        BmsArticle byId = iBmsArticleService.getById(articleId);
        Assert.notNull(byId, "来晚一步，话题已不存在");
        Assert.isTrue(byId.getUserId().equals(umsUser.getId()), "你为什么可以删除别人的话题？？？");
        iBmsArticleService.removeById(articleId);
        return ApiResult.success(null,"删除成功");
    }
}
