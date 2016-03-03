$(function() {

	// 判断整数value是否大于0
	jQuery.validator.addMethod("isIntGtZero", function(value, element) {
		value = parseInt(value);
		return this.optional(element) || value > 0;
	}, "输入的值不正确，必须为大于0的整数.");

	// 判断整数value是否大于或等于0
	jQuery.validator.addMethod("isIntGteZero", function(value, element) {
		value = parseInt(value);
		return this.optional(element) || value >= 0;
	}, "输入的值不正确，必须为大于0的整数.");

	// 判断浮点型
	jQuery.validator.addMethod("isFloat", function(value, element) {
		return this.optional(element) || /^[-\+]?\d+(\.\d+)?$/.test(value);
	}, "只能包含数字、小数点等字符");
	
	jQuery.validator.addMethod("isPrice", function(value, element) {
		return this.optional(element) || /^\d+(?:\.\d{0,2})$/.test(value);
	}, "金额只能包含数字,并且精确到小数点两位");
	
	
	// 只能输入[0-9]数字
	jQuery.validator.addMethod("isDigits", function(value, element) {
		return this.optional(element) || /^\d+$/.test(value);
	}, "只能输入0-9数字");

	// 判断中文字符
	jQuery.validator.addMethod("isChinese", function(value, element) {
		return this.optional(element) || /^[\u0391-\uFFE5]+$/.test(value);
	}, "只能包含中文字符。");

	// 判断英文字符
	jQuery.validator.addMethod("isEnglish", function(value, element) {
		return this.optional(element) || /^[A-Za-z]+$/.test(value);
	}, "只能包含英文字符。");

	// 手机号码验证
	jQuery.validator.addMethod("isMobile",function(value, element) {
			var length = value.length;
			return this.optional(element) || (length == 11 && /^(((13[0-9]{1})|(14[0-9]{1})|(15[0-9]{1})|(16[0-9]{1})|(17[0-9]{1})|(18[0-9]{1})|(19[0-9]{1}))+\d{8})$/
				.test(value));
	}, "请正确填写手机号码。");

	// 电话号码验证
	jQuery.validator.addMethod("isPhone", function(value, element) {
		var tel = /^(\d{3,4}-?)?\d{7,9}$/g;
		return this.optional(element) || (tel.test(value));
	}, "请正确填写您的电话号码。");

	// 联系电话(手机/电话皆可)验证
	jQuery.validator
			.addMethod(
					"isTel",
					function(value, element) {
						var length = value.length;
						var mobile = /^(( (13[0-9]{1})|(14[0-9]{1})|(15[0-9]{1})|(16[0-9]{1})|(17[0-9]{1})|(18[0-9]{1})|(19[0-9]{1}) )+\d{8})$/;
						var tel = /^(\d{3,4}-?)?\d{7,9}$/g;
						return this.optional(element) || tel.test(value)
								|| (length == 11 && mobile.test(value));
					}, "请正确填写您的联系方式");

	// 匹配qq
	jQuery.validator.addMethod("isQq", function(value, element) {
		return this.optional(element) || /^[1-9]\d{4,12}$/;
	}, "匹配QQ");

	// 邮政编码验证
	jQuery.validator.addMethod("isZipCode", function(value, element) {
		var zip = /^[0-9]{6}$/;
		return this.optional(element) || (zip.test(value));
	}, "请正确填写您的邮政编码。");

	// 匹配密码，以字母开头，长度在6-12之间，只能包含字符、数字和下划线。
	jQuery.validator.addMethod("isPwd", function(value, element) {
		return this.optional(element) || /^[a-zA-Z]\\w{6,12}$/.test(value);
	}, "以字母开头，长度在6-12之间，只能包含字符、数字和下划线。");

	// 身份证号码验证
	jQuery.validator.addMethod("isIdCardNo", function(value, element) {
		// var idCard = /^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/;
		return this.optional(element) || isIdCardNo(value);
	}, "请输入正确的身份证号码。");

	// IP地址验证
	jQuery.validator
			.addMethod(
					"ip",
					function(value, element) {
						return this.optional(element)
								|| /^(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.)(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.){2}([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))$/
										.test(value);
					}, "请填写正确的IP地址。");

	// 字符验证，只能包含中文、英文、数字、下划线等字符。
	jQuery.validator.addMethod("stringCheck", function(value, element) {
		return this.optional(element)
				|| /^[a-zA-Z0-9\u4e00-\u9fa5-_]+$/.test(value);
	}, "只能包含中文、英文、数字、下划线等字符");

	// 匹配english
	jQuery.validator.addMethod("isEnglish", function(value, element) {
		return this.optional(element) || /^[A-Za-z]+$/.test(value);
	}, "匹配english");

	// 匹配汉字
	jQuery.validator.addMethod("isChinese", function(value, element) {
		return this.optional(element) || /^[\u4e00-\u9fa5]+$/.test(value);
	}, "匹配汉字");

	// 匹配中文(包括汉字和字符)
	jQuery.validator.addMethod("isChineseChar", function(value, element) {
		return this.optional(element) || /^[\u0391-\uFFE5]+$/.test(value);
	}, "匹配中文(包括汉字和字符) ");

	// 判断是否为合法字符(a-zA-Z0-9-_)
	jQuery.validator.addMethod("isRightfulString", function(value, element) {
		return this.optional(element) || /^[A-Za-z0-9_-]+$/.test(value);
	}, "判断是否为合法字符(a-zA-Z0-9-_)");

	// 判断是否包含中英文特殊字符，除英文"-_"字符外
	jQuery.validator
			.addMethod(
					"isContainsSpecialChar",
					function(value, element) {
						var reg = RegExp(/[(\ )(\`)(\~)(\!)(\@)(\#)(\$)(\%)(\^)(\&)(\*)(\()(\))(\+)(\=)(\|)(\{)(\})(\')(\:)(\;)(\')(',)(\[)(\])(\.)(\<)(\>)(\/)(\?)(\~)(\！)(\@)(\#)(\￥)(\%)(\…)(\&)(\*)(\（)(\）)(\—)(\+)(\|)(\{)(\})(\【)(\】)(\‘)(\；)(\：)(\”)(\“)(\’)(\。)(\，)(\、)(\？)]+/);
						return this.optional(element) || !reg.test(value);
					}, "输入的格式有误，请重新输入.");

	// 身份证号码的验证规则
	function isIdCardNo(num) {

		var len = num.length, re;
		if (len == 15)
			re = new RegExp(/^(\d{6})()?(\d{2})(\d{2})(\d{2})(\d{2})(\w)$/);
		else if (len == 18)
			re = new RegExp(/^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/);
		else {
			// alert("输入的数字位数不对。");
			return false;
		}
		var a = num.match(re);
		if (a != null) {
			if (len == 15) {
				var D = new Date("19" + a[3] + "/" + a[4] + "/" + a[5]);
				var B = D.getYear() == a[3] && (D.getMonth() + 1) == a[4]
						&& D.getDate() == a[5];
			} else {
				var D = new Date(a[3] + "/" + a[4] + "/" + a[5]);
				var B = D.getFullYear() == a[3] && (D.getMonth() + 1) == a[4]
						&& D.getDate() == a[5];
			}
			if (!B) {
				// alert("输入的身份证号 "+ a[0] +" 里出生日期不对。");
				return false;
			}
		}
		if (!re.test(num)) {
			// alert("身份证最后一位只能是数字和字母。");
			return false;
		}
		return true;
	}

});