package com.xly.middlelibrary.utils

import com.xly.business.square.model.Matchmaker


object MatchmakerMockData {

    /**
     * 生成Mock红娘数据
     */
    fun generateMatchmakers(): List<Matchmaker> {
        // 8张头像图片资源，循环使用
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
            Matchmaker(
                id = "mm_001",
                name = "张红娘",
                avatar = avatarResources[0], // head_one
                rating = 4.9f,
                userCount = 256,
                location = "北京 · 朝阳区",
                description = "10年专业经验，专注高端客户匹配，成功撮合500+对新人。擅长深度沟通，精准匹配，一对一贴心服务。",
                tags = listOf("高端匹配", "一对一服务", "成功率95%"),
                successRate = 95.5f,
                isVerified = true,
                isVIP = true,
                yearsOfExperience = 10
            ),
            Matchmaker(
                id = "mm_002",
                name = "李月老",
                avatar = avatarResources[1], // head_two
                rating = 4.8f,
                userCount = 189,
                location = "上海 · 浦东新区",
                description = "资深婚恋顾问，8年从业经验，以耐心细致著称。擅长处理复杂情感问题，帮助客户找到最适合的另一半。",
                tags = listOf("耐心细致", "情感咨询", "成功率92%"),
                successRate = 92.3f,
                isVerified = true,
                isVIP = false,
                yearsOfExperience = 8
            ),
            Matchmaker(
                id = "mm_003",
                name = "王媒婆",
                avatar = avatarResources[2], // head_three
                rating = 4.7f,
                userCount = 142,
                location = "深圳 · 南山区",
                description = "年轻活力，5年经验，擅长年轻人群匹配。了解现代婚恋观念，沟通方式轻松自然，深受年轻客户喜爱。",
                tags = listOf("年轻人群", "现代观念", "成功率90%"),
                successRate = 90.1f,
                isVerified = true,
                isVIP = false,
                yearsOfExperience = 5
            ),
            Matchmaker(
                id = "mm_004",
                name = "刘牵线",
                avatar = avatarResources[3], // head_four
                rating = 4.6f,
                userCount = 98,
                location = "广州 · 天河区",
                description = "专业婚恋服务，6年从业经验。注重客户需求分析，提供个性化匹配方案，帮助每一位客户找到幸福。",
                tags = listOf("个性化服务", "需求分析", "成功率88%"),
                successRate = 88.7f,
                isVerified = false,
                isVIP = false,
                yearsOfExperience = 6
            ),
            Matchmaker(
                id = "mm_005",
                name = "陈月老",
                avatar = avatarResources[4], // head_five
                rating = 4.9f,
                userCount = 312,
                location = "杭州 · 西湖区",
                description = "15年资深婚恋专家，行业知名人士。服务过上千位客户，成功率极高。擅长高端客户和复杂情况处理。",
                tags = listOf("资深专家", "行业知名", "成功率98%"),
                successRate = 98.2f,
                isVerified = true,
                isVIP = true,
                yearsOfExperience = 15
            ),
            Matchmaker(
                id = "mm_006",
                name = "赵红娘",
                avatar = avatarResources[5], // head_six
                rating = 4.5f,
                userCount = 76,
                location = "成都 · 锦江区",
                description = "亲和力强，4年经验，擅长本地市场。了解本地文化特色，帮助客户找到志趣相投的另一半。",
                tags = listOf("本地市场", "亲和力强", "成功率85%"),
                successRate = 85.3f,
                isVerified = true,
                isVIP = false,
                yearsOfExperience = 4
            ),
            Matchmaker(
                id = "mm_007",
                name = "周媒人",
                avatar = avatarResources[6], // head_seven
                rating = 4.8f,
                userCount = 203,
                location = "武汉 · 江汉区",
                description = "专业认证婚恋顾问，7年从业经验。注重客户隐私保护，提供安全可靠的服务，获得客户一致好评。",
                tags = listOf("专业认证", "隐私保护", "成功率91%"),
                successRate = 91.6f,
                isVerified = true,
                isVIP = true,
                yearsOfExperience = 7
            ),
            Matchmaker(
                id = "mm_008",
                name = "吴牵线",
                avatar = avatarResources[7], // head_eight
                rating = 4.4f,
                userCount = 54,
                location = "南京 · 鼓楼区",
                description = "新人婚恋顾问，3年经验，充满热情。虽然经验相对较少，但服务态度认真负责，正在快速成长中。",
                tags = listOf("新人顾问", "认真负责", "成功率80%"),
                successRate = 80.5f,
                isVerified = false,
                isVIP = false,
                yearsOfExperience = 3
            ),
            Matchmaker(
                id = "mm_009",
                name = "郑月老",
                avatar = avatarResources[0], // head_one (循环)
                rating = 4.7f,
                userCount = 167,
                location = "西安 · 雁塔区",
                description = "经验丰富，9年从业，服务过多个地区客户。擅长跨地域匹配，帮助不同城市的客户找到合适的另一半。",
                tags = listOf("跨地域匹配", "经验丰富", "成功率89%"),
                successRate = 89.8f,
                isVerified = true,
                isVIP = false,
                yearsOfExperience = 9
            ),
            Matchmaker(
                id = "mm_010",
                name = "孙红娘",
                avatar = avatarResources[1], // head_two (循环)
                rating = 4.6f,
                userCount = 118,
                location = "重庆 · 渝中区",
                description = "温柔体贴，6年经验，以客户满意度高著称。擅长倾听客户需求，提供专业建议，帮助客户建立良好的关系。",
                tags = listOf("温柔体贴", "满意度高", "成功率87%"),
                successRate = 87.4f,
                isVerified = true,
                isVIP = false,
                yearsOfExperience = 6
            ),
            Matchmaker(
                id = "mm_011",
                name = "钱媒婆",
                avatar = avatarResources[2], // head_three (循环)
                rating = 4.3f,
                userCount = 89,
                location = "天津 · 和平区",
                description = "本地婚恋服务，5年经验。熟悉本地文化和习俗，帮助客户找到符合本地传统的理想伴侣。",
                tags = listOf("本地服务", "传统文化", "成功率83%"),
                successRate = 83.2f,
                isVerified = false,
                isVIP = false,
                yearsOfExperience = 5
            ),
            Matchmaker(
                id = "mm_012",
                name = "周月老",
                avatar = avatarResources[3], // head_four (循环)
                rating = 4.9f,
                userCount = 278,
                location = "苏州 · 工业园区",
                description = "行业顶尖专家，12年丰富经验。服务过各行各业客户，成功率极高。注重客户体验，提供全方位婚恋服务。",
                tags = listOf("行业顶尖", "全方位服务", "成功率96%"),
                successRate = 96.8f,
                isVerified = true,
                isVIP = true,
                yearsOfExperience = 12
            )
        )
    }

    /**
     * 生成推荐红娘（前3名）
     */
    fun generateFeaturedMatchmakers(): List<Matchmaker> {
        return generateMatchmakers().take(3)
    }

    /**
     * 根据ID生成单个Mock数据
     */
    fun generateMatchmakerById(id: String): Matchmaker? {
        return generateMatchmakers().find { it.id == id }
    }

    /**
     * 生成指定数量的Mock数据
     */
    fun generateMatchmakers(count: Int): List<Matchmaker> {
        return generateMatchmakers().take(count)
    }
}