package com.xly.middlelibrary.utils

import com.xly.business.square.model.SelectionType
import com.xly.business.square.model.TodaySelectionUser


object TodaySelectionMockData {

    /**
     * 生成今日精选用户数据
     */
    fun generateTodaySelection(): List<TodaySelectionUser> {
        // 8张头像图片资源
        val avatarResources = listOf(
            "head_one",
            "head_two",
            "head_three",
            "head_four",
            "head_five",
            "head_six",
            "head_seven",
            "head_eight"
        )
        
        return listOf(
            TodaySelectionUser(
                id = "sel_001",
                name = "小美",
                age = 25,
                location = "北京 · 朝阳区",
                avatar = avatarResources[0], // head_one
                tags = listOf("163cm", "本科", "5k-8k", "喜欢旅行"),
                selectionReason = "高匹配度",
                selectionDescription = "根据您的偏好，为您精准匹配。相似的教育背景和兴趣爱好，匹配度高达92%。",
                selectionType = SelectionType.AI_MATCHED,
                matchScore = 92f
            ),
            TodaySelectionUser(
                id = "sel_002",
                name = "小雨",
                age = 23,
                location = "上海 · 浦东新区",
                avatar = avatarResources[1], // head_two
                tags = listOf("160cm", "硕士", "8k-12k", "文艺青年"),
                selectionReason = "官方推荐",
                selectionDescription = "官方精选优质用户，认证信息完整，活跃度高，值得信赖。",
                selectionType = SelectionType.OFFICIAL,
                matchScore = null
            ),
            TodaySelectionUser(
                id = "sel_003",
                name = "小芳",
                age = 26,
                location = "深圳 · 南山区",
                avatar = avatarResources[2], // head_three
                tags = listOf("165cm", "本科", "10k-15k", "工作稳定"),
                selectionReason = "高匹配度",
                selectionDescription = "智能算法推荐，年龄、地域、收入水平高度匹配，建议优先关注。",
                selectionType = SelectionType.AI_MATCHED,
                matchScore = 88f
            ),
            TodaySelectionUser(
                id = "sel_004",
                name = "小丽",
                age = 24,
                location = "广州 · 天河区",
                avatar = avatarResources[3], // head_four
                tags = listOf("162cm", "本科", "6k-10k", "性格开朗"),
                selectionReason = "活跃用户",
                selectionDescription = "官方推荐活跃用户，近期登录频繁，回复及时，互动意愿强。",
                selectionType = SelectionType.OFFICIAL,
                matchScore = null
            ),
            TodaySelectionUser(
                id = "sel_005",
                name = "小雅",
                age = 27,
                location = "杭州 · 西湖区",
                avatar = avatarResources[4], // head_five
                tags = listOf("168cm", "硕士", "15k-20k", "文艺青年"),
                selectionReason = "高匹配度",
                selectionDescription = "智能匹配系统推荐，匹配度达90%。职业背景相似，价值观相符。",
                selectionType = SelectionType.AI_MATCHED,
                matchScore = 90f
            ),
            TodaySelectionUser(
                id = "sel_006",
                name = "小婷",
                age = 25,
                location = "成都 · 锦江区",
                avatar = avatarResources[5], // head_six
                tags = listOf("164cm", "本科", "7k-12k", "热爱生活"),
                selectionReason = "官方推荐",
                selectionDescription = "官方精选用户，资料真实完整，照片认证通过，信誉度高。",
                selectionType = SelectionType.OFFICIAL,
                matchScore = null
            ),
            TodaySelectionUser(
                id = "sel_007",
                name = "小静",
                age = 28,
                location = "武汉 · 江汉区",
                avatar = avatarResources[6], // head_seven
                tags = listOf("166cm", "硕士", "12k-18k", "事业有成"),
                selectionReason = "高匹配度",
                selectionDescription = "根据您的个人资料和择偶标准，智能推荐。教育水平、收入范围高度匹配。",
                selectionType = SelectionType.AI_MATCHED,
                matchScore = 85f
            ),
            TodaySelectionUser(
                id = "sel_008",
                name = "小雯",
                age = 23,
                location = "南京 · 鼓楼区",
                avatar = avatarResources[7], // head_eight
                tags = listOf("161cm", "本科", "5k-9k", "温柔体贴"),
                selectionReason = "新用户推荐",
                selectionDescription = "官方推荐新用户，刚注册不久，资料新鲜，值得关注。",
                selectionType = SelectionType.OFFICIAL,
                matchScore = null
            )
        )
    }

    /**
     * 生成随机精选用户
     */
    fun generateRandomSelection(count: Int): List<TodaySelectionUser> {
        val names = listOf("小美", "小雨", "小芳", "小丽", "小雅", "小婷", "小静", "小雯")
        val locations = listOf(
            "北京 · 朝阳区", "上海 · 浦东新区", "深圳 · 南山区",
            "广州 · 天河区", "杭州 · 西湖区", "成都 · 锦江区"
        )
        val reasons = listOf("高匹配度", "官方推荐", "活跃用户", "新用户推荐")
        val descriptions = listOf(
            "根据您的偏好，为您精准匹配",
            "官方精选优质用户，认证信息完整",
            "智能算法推荐，匹配度高",
            "官方推荐活跃用户，互动意愿强"
        )

        return (1..count).map { index ->
            val isOfficial = index % 3 == 0
            TodaySelectionUser(
                id = "sel_$index",
                name = names.random(),
                age = (22..30).random(),
                location = locations.random(),
                avatar = "https://example.com/user/u$index.jpg",
                tags = listOf(
                    "${(155..170).random()}cm",
                    listOf("本科", "硕士", "博士").random(),
                    "${(5..20).random()}k-${(10..30).random()}k"
                ),
                selectionReason = reasons.random(),
                selectionDescription = descriptions.random(),
                selectionType = if (isOfficial) SelectionType.OFFICIAL else SelectionType.AI_MATCHED,
//                matchScore = if (!isOfficial) (80f..95f).random() else null
            )
        }
    }
}