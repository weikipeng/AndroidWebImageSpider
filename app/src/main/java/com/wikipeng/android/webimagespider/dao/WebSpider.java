package com.wikipeng.android.webimagespider.dao;

import android.util.Log;

import com.wikipeng.android.webimagespider.MainActivity;

import org.apache.http.protocol.HTTP;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WikiPeng on 15/6/8 上午11:26.
 */
public class WebSpider {
    private Parser mParser;
    private Parser htmlParser;
    private String mHtmlTitle;
    private List<ImageTag> mImageTagList;

    /**
     * 最外层表情
     */
    private static final String TAG_NAME = "div";
    /**
     * 属性名称
     */
    private static final String ATTR_NAME = "class";
    /**
     * 属性值
     */
    private static final String ATTR_VALUE = "c_content_overflow";
    /**
     * 目标图片父标签
     */
    private static final String AIM_TAG_NAME = "p";

    private static final String IMAGE_TAG_NAME = "img";

    public WebSpider() {
        mImageTagList = new ArrayList<>();
    }

    public void init(String url) {
        try {
            mParser = new Parser(url);
            mParser.setEncoding(HTTP.UTF_8);
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }

    public void resetUrl(String url) {
        try {
            mParser.reset();
            mParser.setURL(url);
            mParser.setEncoding(HTTP.UTF_8);
        } catch (ParserException e) {
            e.printStackTrace();
        }

    }

    public void parseHtml() {
        try {
            NodeList divOfImageList = mParser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter(TAG_NAME)
                    , new HasAttributeFilter(ATTR_NAME, ATTR_VALUE)));

            if (divOfImageList != null && divOfImageList.size() > 0) {
                NodeList pList = divOfImageList.elementAt(0).getChildren()
                        .extractAllNodesThatMatch(new TagNameFilter(AIM_TAG_NAME));

                if (pList != null && pList.size() > 0) {
                    for (int i = 0; i < pList.size(); i++) {
                        NodeList imageList = pList.elementAt(i).getChildren()
                                .extractAllNodesThatMatch(new TagNameFilter(IMAGE_TAG_NAME));
                        if (imageList != null && imageList.size() > 0) {
                            for (int j = 0; j < imageList.size(); j++) {
                                Node imageNode = imageList.elementAt(j);
                                if (imageNode instanceof ImageTag) {
                                    ImageTag imageTag = (ImageTag) imageNode;
                                    Log.e("wiki", imageTag.getImageURL());
                                    mImageTagList.add(imageTag);
                                }
                            }
                        }
                    }
                }

            }
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }

    public List<ImageTag> getImageTagList() {
        return mImageTagList;
    }

//    private List<String> parserDwPost() throws ParserException {
//        final String DW_HOME_PAGE_URL = "http://www.ibm.com/developerworks/cn";
//        ArrayList<String> pTitleList = new ArrayList<String>();
//        // 创建 html parser 对象，并指定要访问网页的 URL 和编码格式
//        htmlParser = new Parser(DW_HOME_PAGE_URL);
//        htmlParser.setEncoding("UTF-8");
//        String postTitle = "";
//        // 获取指定的 div 节点，即 <div> 标签，并且该标签包含有属性 id 值为“tab1”
//        NodeList divOfTab1 = htmlParser.extractAllNodesThatMatch(
//                new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("id", "tab1")));
//
//        if (divOfTab1 != null && divOfTab1.size() > 0) {
//            // 获取指定 div 标签的子节点中的 <li> 节点
//            NodeList itemLiList = divOfTab1.elementAt(0).getChildren().extractAllNodesThatMatch
//                    (new TagNameFilter("li"), true);
//
//            if (itemLiList != null && itemLiList.size() > 0) {
//                for (int i = 0; i < itemLiList.size(); ++i) {
//                    // 在 <li> 节点的子节点中获取 Link 节点
//                    NodeList linkItem
//                            = itemLiList.elementAt(i).getChildren().extractAllNodesThatMatch
//                            (new NodeClassFilter(LinkTag.class), true);
//                    if (linkItem != null && linkItem.size() > 0) {
//                        // 获取 Link 节点的 Text，即为要获取的推荐文章的题目文字
//                        postTitle = ((LinkTag) linkItem.elementAt(0)).getLinkText();
//                        System.out.println(postTitle);
//                        pTitleList.add(postTitle);
//                    }
//                }
//            }
//        }
//        return pTitleList;
//    }
}
