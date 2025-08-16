package com.xly.business.login.model

data class UserInfoRegisterReq (
    var step: Int = 1,
    var phoneNumber: String = "",
    var gender: String = "", //性别 1 男 2女
    var age: Int = 10, //年龄
    var height: Int = 130, //身高
    var weight: Int = 130, //体重
    var isWeightPrivate: Boolean = true, //体重
    var educationLevel: Boolean = true, //学历 1 ，大专以下；2 大专，3本科，4硕士，5博士
    var school: String = "", //学校
    var companyType: String = "", //公司性质和工作
    var occupation: String = "", //公司性质和工作
    var incomeLevel: String = "", //年收入：1 2 3 4 5 6 7

    var currentProvince: String = "", //年收入：1 2 3 4 5 6 7
    var currentCity: String = "", //年收入：1 2 3 4 5 6 7
    var currentDistrict: String = "", //年收入：1 2 3 4 5 6 7
    var hometownProvince: String = "", //年收入：1 2 3 4 5 6 7
    var hometownCity: String = "", //年收入：1 2 3 4 5 6 7
    var hometownDistrict: String = "", //年收入：1 2 3 4 5 6 7


    var houseStatus: String = "", //年收入：1 2 3 4 5 6 7
    var carStatus: String = "", //年收入：1 2 3 4 5 6 7


    var maritalStatus: String = "", //年收入：1 2 3 4 5 6 7
    var childrenStatus: String = "", //年收入：1 2 3 4 5 6 7


    var loveAttitude: String = "", //年收入：1 2 3 4 5 6 7
    var preferredAgeMin: String = "", //年收入：1 2 3 4 5 6 7
    var preferredAgeMax: String = "", //年收入：1 2 3 4 5 6 7
    var marriagePlan: String = "", //年收入：1 2 3 4 5 6 7


    var nickname: String = "", //年收入：1 2 3 4 5 6 7
    var avatarUrl: String = "", //年收入：1 2 3 4 5 6 7

    var lifePhotos: String = "", //年收入：1 2 3 4 5 6 7

    var selfIntroduction: String = "", //年收入：1 2 3 4 5 6 7

    var lifestyle: String = "", //年收入：1 2 3 4 5 6 7

    var idealPartner: String = "", //年收入：1 2 3 4 5 6 7

    var userTags: String = "", //年收入：1 2 3 4 5 6 7

    var realName: String = "", //年收入：1 2 3 4 5 6 7
    var idCardNumber: String = "", //年收入：1 2 3 4 5 6 7
    var preferredTags: String = "", //年收入：1 2 3 4 5 6 7




)