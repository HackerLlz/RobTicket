var popup_browser = navigator.appName
var popup_b_version = navigator.appVersion
var popup_version = popup_b_version.split(';');
var popup_trin_version_flag = popup_version && popup_version.length > 1;
var popup_trim_Version = popup_trin_version_flag ? popup_version[1].replace(/[ ]/g, '') : '';
var popup_uam_dataType = 'json';
var popup_uam_type = 'POST';
if (popup_browser == 'Microsoft Internet Explorer' && popup_trim_Version == 'MSIE7.0') {
  popup_uam_dataType = 'jsonp';
  popup_uam_type = 'GET';
} else if(popup_browser == 'Microsoft Internet Explorer' && popup_trim_Version == 'MSIE8.0') {
  popup_uam_dataType = 'jsonp';
  popup_uam_type = 'GET';
} else if(popup_browser == 'Microsoft Internet Explorer' && popup_trim_Version == 'MSIE9.0') {
  popup_uam_dataType = 'jsonp';
  popup_uam_type = 'GET';
}

// 统一认证登录
var popup_passport_appId = 'otn';
var popup_passport_baseUrl = 'https://kyfw.12306.cn/passport/';

// 四个流程
// 获取验证码
var popup_passport_captcha = popup_passport_baseUrl + 'captcha/captcha-image64?login_site=E&module=login&rand=sjrand&';
var getCode_url = '/login/code';
// 验证验证码
var popup_passport_captcha_check = popup_passport_baseUrl + 'captcha/captcha-check';
var checkCode_url = '/login/checkCode';
// 登陆，返回一个uamtk
var popup_passport_login =  popup_passport_baseUrl + 'web/login';
var login_url = '/login/doLogin';
// 用uamtk取静态信息, 用于已登录时直接登陆
var popup_passport_apptk_static = popup_passport_baseUrl + 'web/auth/uamtk-static'
var uamtkStatic_url = '/login/uamtkStatic';
// 登陆成功后验证重置uamtk和存TK到cookie中
var popup_passport_uamtk = popup_passport_baseUrl + 'web/auth/uamtk';
var base_uamauthclient_url = popup_baseUrl + popup_publicName + '/uamauthclient';
var uamtkUrl = '/login/uamtk';
var uamtkClientUrl = '/login/uamtkClient';

var popup_is_uam_login = 'Y'; // 是否统一认证登录
var popup_is_login_passCode = 'Y' // 是否启用验证码校验登录（仅本地登录）
var popup_is_sweep_login = 'Y' // 统一认证登录情况下是否开启扫码登录
var popup_is_login = 'N' // 是否已登录

var popup_baseUrl = 'https://kyfw.12306.cn';
var popup_publicName = '/otn'; //预发布环境

var login_success_url = "/ticket/view";
var popup_loginCallBack = function() {
    if(!$.popup_isPop){
        //获取新的uamtk
        var uamtkData = {"appid": popup_passport_appId};
        $.ajax({
            type: "POST",
            url: uamtkUrl,
            async: false,
            data: JSON.stringify(uamtkData),
            contentType:'application/json',    // 不加传过去的json后面有个= 会出问题
            dataType: "json",    // 加了data就不用再转json对象了
            xhrFields: {
            	withCredentials: true
            },
            success: function (data) {
                if (data.result_code == 0) {
                    var apptk = data.newapptk || data.apptk;
                    // 将TK种到自己的Cookies中，要tk才能通过checkUser
                    // 发送tk参数到服务器，服务器set-cookie tk到客户端，可能怕有些客户端不让设置cookie
                    var uamtkClientData = {tk: apptk};
                    $.ajax({
                        type: "POST",
                        async: false,
                        url: uamtkClientUrl,
                        data: JSON.stringify(uamtkClientData),
                        contentType:'application/json',    // 不加传过去的json后面有个= 会出问题
                        dataType: "json",    // 加了data就不用再转json对象了
                        xhrFields: {
                            withCredentials: true
                        },
                        success: function (data) {
                            $('.mask').fadeOut();
                            $(".modal-login").hide();
                            if($.pop_secretStr && $.pop_start_time){
                                $.todo_submitOrderRe($.pop_secretStr, $.pop_start_time);
                            }
                            // 跳转到查票界面
                            window.location.href = login_success_url;
                        },
                        error: function () {
                        }
                    });
                }
            },
            error: function () {
            }
        });
    }else{
    	//弹框登录回调
    	if("Y" == popup_is_uam_login){
    		//到认证中心认证去
			$.ajax({
				type : "POST",
				url : popup_passport_uamtk,
				async: false,
				data : {
					appid : popup_passport_appId
				},
				dataType : "jsonp",
				jsonp : "callback",
				success : function(data) {
					if (data.result_code == 0) {
						var apptk = data.newapptk || data.apptk;
						//将TK种到自己的Cookies中
						$.ajax({
							type : "POST",
							async: false,
							url : base_uamauthclient_url,
							data : {
								tk : apptk
							},
							datatype : "json",
							success : function(data) {
								if (data.result_code == 0) {
									$('.mask').fadeOut();
									$(".modal-login").hide();
									if($.pop_secretStr && $.pop_start_time){
										$.todo_submitOrderRe($.pop_secretStr, $.pop_start_time);
									}
								}
							},
							error : function() {}
						});									
					} 
				},
				error : function() {}
			});
    	}else{
    		$('.mask').fadeOut();
			$(".modal-login").hide();
			if($.pop_secretStr && $.pop_start_time){
				$.todo_submitOrderRe($.pop_secretStr, $.pop_start_time);
			}
    	}
    }
}
// // 初始读取conf，已登录状态，重定向 （popup_uamIsLogin 登陆后）
// var popup_loginedCallBack = function () {
//     if(!$.popup_isPop){
//         console.log("uamtk: " + getCookieByName("uamtk"));
//         var uamtk = getCookieByName("uamtk");
//         $.ajax({
//             type: "POST",
//             url: "/user/login",
//             async: false,
//             data: {
//                 "uamtk": uamtk
//             },
//             success: function (data) {
//                 if (data == "success"){
//                     window.location.href = login_success_url;
//                 }
//             },
//             error : function() {}
//         });
//     }
// }
var popup_qr_appId = 'otn'
var popup_url = {
    'loginConf': popup_baseUrl + popup_publicName + '/login/conf',
    // 本地登录
    'getPassCodeNew': popup_baseUrl + popup_publicName + '/passcodeNew/getPassCodeNew?module=login&rand=sjrand&',
    'checkRandCodeAnsyn': popup_baseUrl + popup_publicName + '/passcodeNew/checkRandCodeAnsyn',
    'login': popup_baseUrl + popup_publicName + '/login/loginAysnSuggest',
    'getBanners': popup_baseUrl + popup_publicName + '/index12306/getLoginBanner',
    // 扫码登录
    'qr': popup_baseUrl + '/passport/web/create-qr',
    'qr64': popup_baseUrl + '/passport/web/create-qr64',
    'checkqr': popup_baseUrl + '/passport/web/checkqr'
}

//验证码顶部高度
var popup_defaultPasscodeHeight = 30;
var popup_ifSuccessCode = false;

var popup_passCodeImg = $('#J-loginImg'); // 验证码图片

var popup_ispopup_CreateQr = false;
var popup_t = null, popup_s = '-1';
// var popup_slideIsLoad = true;

var popup_isPopupLogin = true;

var forie = 'forie.html'

jQuery.extend({
	pop_secretStr : "",
	pop_start_time : "",
	popup_isPop : true,
    popup_show_login_error: function(msg) {
        if('验证码错误！' != msg && '请选择验证码！' != msg ) {
            $('#J-password').val('');
        }
        $('#J-login-error').show().find('span').html(msg);
    },
    popup_hide_login_error: function() {
        $('#J-login-error').hide().find('span').html('');
    },
    // 统一认证登录, popup_checkPassCode 验证完验证码之后
    popup_loginForUam: function() {
        var randCode = '';
        var obj = $('#J-passCodeCoin div');

        for(var i = 0; i < obj.length; i++) {
            randCode += $(obj[i]).attr('randcode') + ',';
        }
        randCode = randCode.substring(0, randCode.length-1);

        // 12306服务器应该用了cors协议且让 http://localhost 通过，所以能跨域登陆成功
        var login_data = {
            'username': $('#J-userName').val(),
            'password': $('#J-password').val(),
            'appid': popup_passport_appId,
            'answer': randCode
        };
        $.ajax({
            // crossDomain: true,
            // xhrFields: { withCredentials : true },  // cors协议需要，若服务器设置了可携带cookie，那么可以带上cookie，同源的会被服务器接收
            url: login_url,
            // data: {
            //     'username': $('#J-userName').val(),
            //     'password': $('#J-password').val(),
            //     'appid': popup_passport_appId,
            //     'answer': randCode
            // },
            data: JSON.stringify(login_data),
            contentType:'application/json',    // 不加传过去的json后面有个= 会出问题
            // dataType: "json",    // 加了data就不用再转json对象了
            type: "POST",
            timeout: 10000,
            success: function(data, textStatus, XMLHttpRequest) {
                // if(data.result_code == 0) {
                //     $.popup_hideCommonLogin();
                //     popup_loginCallBack();
                // }else {
                //     $.popup_show_login_error(data.result_message)
                //     $.popup_createPassCode()
                //     $('#J-passCodeCoin').html('');
                // }
                if(data == "success") {
                    // 跳转到查票界面
                    window.location.href = login_success_url;
                }else {
                    $.popup_show_login_error(data == "success"? "登陆成功": "登陆失败")
                    $.popup_createPassCode()
                    $('#J-passCodeCoin').html('');
                }
            },
            error: function() {
                $.popup_hideCommonLogin();
            }
        });
    },
    // 本地登录+验证码
    popup_loginForLocation_passcode: function () {
       var randCode = '';
        var obj = $('#J-passCodeCoin div');

        for(var i = 0; i < obj.length; i++) {
            randCode += $(obj[i]).attr('randcode') + ',';
        }
        randCode = randCode.substring(0,randCode.length-1);

        // 清空选中的验证码
        $('#J-passCodeCoin').html('');

        $.ajax({
            url: popup_url.login,
            data: {
                'loginUserDTO.user_name': $('#J-userName').val(),
                'userDTO.password': $('#J-password').val(),
                'randCode': randCode
            },
            type: 'POST',
            timeout: 10000,
            success: function(response) {
                var data = response.data;
                if (data && data.loginCheck == 'Y') {
                    $.popup_hideCommonLogin();
                    // 成功回调
                    popup_loginCallBack()
                } else if (data && data.message) {
                    $.popup_show_login_error(data.message)
                    $.popup_createPassCode_location();
                    $('#J-passCodeCoin').html('');
                }  else if (response.messages) {
                    $.popup_show_login_error(response.messages)
                    $.popup_createPassCode_location();
                    $('#J-passCodeCoin').html('');
                } else {
                    $.popup_hideCommonLogin();
                }
            },
            error: function(e) {
                $.popup_hideCommonLogin();
            }
        });
    },
    // 本地登录
    popup_loginForLocation: function () {
        $.ajax({
            url: popup_url.login,
            data: {
                'loginUserDTO.user_name': $('#J-userName').val(),
                'userDTO.password': $('#J-password').val()
            },
            type: 'POST',
            timeout: 10000,
            success: function(response) {
                var data = response.data;
                if (data && data.loginCheck == 'Y') {
                    $.popup_hideCommonLogin();
                    // 成功回调
                    popup_loginCallBack()
                } else if (data && data.message) {
                    // ==========================================
                    $.popup_show_login_error(data.message)
                } else if (response.messages) {
                    // ==========================================
                    $.popup_show_login_error(response.messages)
                } else {
                    $.popup_hideCommonLogin();
                }
            },
            error: function(e) {
                $.popup_hideCommonLogin();
            }
        });
    },
    popup_hideCommonLogin: function() {
        $('#J-userName').val('');
        $('#J-password').val('');
        $('#J-passCodeCoin').html('');
        $('#J-login-error').hide();
    },
    popup_showLoginType: function(index) {
        $('#J-loginImg').hide();
        $('.lgcode-error').hide();
        $('.lgcode-loading').hide();
        $('.lgcode-loading img').hide();
        $('.lgcode-success').hide();
        if(0 == index) {
            $('#J-loginImg').show();    //show code
        }else if(1 == index) {
            $('.lgcode-error').show();  // code error
        }else if(2 == index) {
            $('.lgcode-success').show(); // code success
        }else if(3 == index) {
            $('.lgcode-loading').show();    // loading code
            $('.lgcode-loading img').show();
        }
    },
    popup_getClickPos: function(e) {
        var xPage = (navigator.appName == 'Netscape') ? e.pageX : e.clientX + (document.documentElement.scrollLeft || window.pageXOffset || document.body.scrollLeft);
        var yPage = (navigator.appName == 'Netscape') ? e.pageY : e.clientY + (document.documentElement.scrollTop || window.pageYOffset || document.body.scrollTop);
        identifyImage = document.getElementById('J-loginImg');
        img_x = $.popup_locationLeft(identifyImage);
        img_y = $.popup_locationTop(identifyImage);
        
        var xPos = xPage-img_x;
        var yPos = yPage-img_y - popup_defaultPasscodeHeight;
        if(xPos > 0 && yPos > 0) {
            var html = '<div randCode="' +xPos + ',' + yPos + '" class="lgcode-active" style="top: ' + (yPos + 16) + 'px; left: ' + (xPos - 13) + 'px;"></div>';
            $('#J-passCodeCoin').append(html);
        }
        $('.lgcode-active').click(function(e) {
            $(this).remove();
            e.stopPropagation();
        });
    },
    popup_locationLeft: function(element) {
        offsetTotal = element.offsetLeft;
        scrollTotal = 0;
        if (element.tagName != 'BODY') {
           if (element.offsetParent != null) {
              return offsetTotal+scrollTotal + $.popup_locationLeft(element.offsetParent);
           }
        }
        return offsetTotal + scrollTotal;
    },
    popup_locationTop: function(element) {
        offsetTotal = element.offsetTop;
        scrollTotal = 0;
        if (element.tagName != 'BODY') {
           if (element.offsetParent != null) {
              return offsetTotal + scrollTotal + $.popup_locationTop(element.offsetParent);
           }
        }
        return offsetTotal + scrollTotal;
    },
    // 初始化登录
    popup_initLogin: function(isPopupLogin) {

        var popup_browser_flag = false;
        if (popup_browser == "Microsoft Internet Explorer" && popup_trim_Version == "MSIE7.0") {
            popup_browser_flag = true;
        } else if (popup_browser == "Microsoft Internet Explorer" && popup_trim_Version == "MSIE6.0") {
            popup_browser_flag = true;
        }
        if (popup_browser_flag) {
            location.href = forie; //浏览器升级页面
            return;
        }
        
    	$.popup_isPop = isPopupLogin;
        popup_isPopupLogin = isPopupLogin;

        $.popup_hideCommonLogin();
        $.popup_showLoginType(3); // loading code

        // 获取配置信息
        $.popup_getConf()

        $('#J-userName').focus(function() {
            $.popup_hide_login_error();
        })
        
        $('#J-password').focus(function() {
            $.popup_hide_login_error();
        })

        // 注册切换登录方式事件
        $.popup_switchLoginWay()

        // 注册刷新二维码事件
        $.popup_refreshQrCode()
        
        // 刷新验证码
        $('.lgcode-refresh').unbind('click').click(function() {
            $('.lgcode-refresh').addClass('lgcode-refresh-click')

            if (popup_is_uam_login == 'Y') {
                $.popup_refreshPassCode(false);
            } else {
                $.popup_refreshPassCode_location(false);
            }
            
            setTimeout(function () {
                $('.lgcode-refresh').removeClass('lgcode-refresh-click')
            }, 100)
        })
        
        // 选中验证码
        $('#J-loginImgArea').unbind('click').click(function(event) {
            $.popup_getClickPos(event)
        })
    },
    // 获取配置信息
    popup_getConf: function () {
	    return;
	    // var confUrl = "/ticket/conf";
        $.ajax({
            url: popup_url.loginConf,
            // url: confUrl,
            type: 'POST',
            // type: 'GET',
            timeout: 10000,
            success: function(response) {
                var data = response.data;
                if (data) {
                    popup_is_uam_login = data.is_uam_login;
                    popup_is_login_passCode = data.is_login_passCode;
                    popup_is_sweep_login = data.is_sweep_login;
                    popup_is_login = data.is_login;
                    $.popup_isLogin()
                }
            },
            error: function(error) {
                // error
            }
        });
    },
    popup_isLogin: function () {
        if (popup_is_uam_login == 'Y') {
            if (popup_isPopupLogin) {
                $.popup_uamIsShowQr();
            } else {
                $.popup_uamIsLogin();
            }
        } else {
            // 本地登录
            if (popup_is_login == 'Y') {
                // popup_loginedCallBack();
                popup_loginCallBack();
            } else {
                // 隐藏扫码登录入口
                $.popup_hideQrCode()

                $('.login-account').show();

                // 本地登录
                if (popup_is_login_passCode == 'Y') {
                    // 显示验证码
                    $.popup_showPasscode()
                    // 获取本地登录验证码
                    $.popup_createPassCode_location()
                } else {
                    // 隐藏验证码
                    $.popup_hidePasscode()

                    // 重置登录窗口居中
                    $.popup_resetLoginBox();
                }

                // 表单验证
                $.popup_validate()
            }
        }
    },
    // 重置登录窗口居中
    popup_resetLoginBox: function () {
        //设置登录窗口局中
        var loginBox = $('.login-panel .login-box');
        loginBox.css('margin-top',-loginBox.outerHeight()/2)
    },
    // 是否已登录，仅限统一认证登录
    popup_uamIsLogin: function () {
        var uamtkStatic_data = {"appid": popup_passport_appId};
        $.ajax({
            url: uamtkStatic_url,
            // data:{ 'appid': popup_passport_appId },
            data: JSON.stringify(uamtkStatic_data),
            contentType:'application/json',
            type: 'POST',
            // xhrFields : {
            //     withCredentials : true
            // },
            dataType: 'json',
            timeout: 10000,
            xhrFields: {
                withCredentials: true
            },
            success: function (response) {
                if (response.result_code == '0') {
                    // 已经登录
                    // popup_loginedCallBack();
                    // popup_loginCallBack();
                    var apptk = response.newapptk || response.apptk;
                    // 将TK种到自己的Cookies中，要tk才能通过checkUser
                    // 发送tk参数到服务器，服务器set-cookie tk到客户端，可能怕有些客户端不让设置cookie
                    var uamtkClientData = {tk: apptk};
                    $.ajax({
                        type: "POST",
                        async: false,
                        url: uamtkClientUrl,
                        data: JSON.stringify(uamtkClientData),
                        contentType:'application/json',    // 不加传过去的json后面有个= 会出问题
                        dataType: "json",    // 加了data就不用再转json对象了
                        xhrFields: {
                            withCredentials: true
                        },
                        success: function (data) {
                            $('.mask').fadeOut();
                            $(".modal-login").hide();
                            if($.pop_secretStr && $.pop_start_time){
                                $.todo_submitOrderRe($.pop_secretStr, $.pop_start_time);
                            }
                            // 跳转到查票界面
                            window.location.href = login_success_url;
                        },
                        error: function () {
                        }
                    });
                } else {
                    $.popup_uamIsShowQr();
                }
            },
            error: function (error) {
            }
        });
    },
    popup_uamIsShowQr: function () {
        if (popup_is_sweep_login == 'Y') {
            // 显示扫码登录入口
            $.popup_showQrCode()

            // loading
            $('#J-login-code-loading').show();
            $('#J-login-code-con').hide()
            $.popup_hideQrError();

            // 创建登录二维码
            $.popup_createQr()
        } else {
            // 隐藏扫码登录入口
            $.popup_hideQrCode()
            $('.login-account').show();
            // 获取统一认证登录验证码
            $.popup_createPassCode()
        }

        // 表单验证
        $.popup_validate()
    },
    // 扫码登录-获取二维码接口
    popup_createQr: function () {
        $.ajax({
            url: popup_url.qr64,
            data: { 
                appid: popup_qr_appId 
            },
            type: 'POST',
            timeout: 10000,
            success: function(data) {
                if(data && data.result_code === '0' && data.image) {
                    $('#J-qrImg').attr('src', 'data:image/jpg;base64,' + data.image);

                    $('#J-login-code-loading').hide();
                    $('#J-login-code-con').show();
                    $('#J-code-error-mask').hide();
                    $('#J-code-error').hide();

                    popup_t = null
                    popup_s = -1

                    popup_t = setInterval(function () {
                        if (popup_s == '2' || popup_s == '3') {
                            clearInterval(popup_t)
                        } else {
                            // 轮询调用二维码检查接口，直至返回状态为2：登录成功，（已识别且已授权）、3：已失效
                            // $.popup_checkQr(data.uuid)
                        }
                    }, 1000)

                } else {
                    // error
                }
            },
            error: function(error) {
                // error
            }
        })
    },
    // 扫码登录-轮询调用二维码检查接口
    popup_checkQr: function (uuid) {
        $.ajax({
            url: popup_url.checkqr,
            data: { 
                uuid: uuid,
                appid: popup_qr_appId 
            },
            type: 'POST',
            timeout: 10000,
            success: function(data) {
                if (data) {
                    popup_s = data.result_code
                    $.popup_tipsQrInfo(parseInt(data.result_code))
                }
            },
            error: function(err) {

            }
        })
    },
    // 显示二维码已失效
    popup_showQrError: function (msg) {
        $('#J-code-error-mask').show()
        $('#J-code-error').show()
        $('#J-code-error').find('p').html(msg)
    },
    // 隐藏二维码已失效
    popup_hideQrError: function () {
        $('#J-code-error-mask').hide();
        $('#J-code-error').hide();
    },
    // 显示二维码loading效果
    popup_showQrLoading: function () {
        $('.login-code-loading').show();
        $('.login-code-con').hide();
    },
    // 隐藏二维码loading效果
    popup_hideQrLoading: function () {
        $('.login-code-loading').hide();
        $('.login-code-con').show();
    },
    // 二维码上的信息提示
    popup_tipsQrInfo: function (resCode) {

        // 0：未识别、
        // 1：已识别，暂未授权（未点击授权或不授权）、
        // 2：登录成功，（已识别且已授权）、
        // 3：已失效、
        // 5系统异常
        var codeErrorMask = $('#J-code-error-mask')
            , codeError = $('#J-code-error')
            , codeTips = $('#J-login-code-con')
            , codeTipsSuccess = $('#J-login-code-success')

        if (resCode == 0) {
            codeErrorMask.hide()
            codeError.hide()
        } else {
            codeErrorMask.show()
            codeError.show()

            switch(resCode) {
                case 1:
                    codeTips.hide()
                    codeTipsSuccess.removeClass('hide')
                    break;
                case 2:
                    codeTips.hide()
                    codeTipsSuccess.removeClass('hide')
                    // 成功回调
                    popup_loginCallBack()
                    break;
                case 3:
                    codeTips.show()
                    codeError.find('p').html('二维码已失效')
                    codeError.find('a').show()
                    codeTipsSuccess.addClass('hide')
                    break;
                case 5:
                    codeTips.show()
                    codeError.find('p').html('系统异常')
                    codeError.find('a').show()
                    codeTipsSuccess.addClass('hide')
                    break;
                default:
                    codeError.find('p').html('二维码已失效')
                    codeError.find('a').show()

                    codeTipsSuccess.addClass('hide')
            }
        }
    },
    // 表单验证
    popup_validate: function () {
	    return

        $('#J-login').click(function() {

            var userName = $('#J-userName').val()
            var password = $('#J-password').val()
            var mobile=/^(13[0-9])|(14[0-9])|(15[0-9])|(18[0-9])|(17[0-9])\d{8}$/;
            var tel = /^[A-Za-z]{1}([A-Za-z0-9]|[_]) {0,29}$/;
            var tel_other = /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i;

            if(!userName) {
                $.popup_show_login_error('请输入用户名！')
                return false;
            }
            // if(!tel.test(userName) && !tel_other.test(userName) && !mobile.test(userName)) {
            //     $.popup_show_login_error('登录名格式不正确！')
            //     return false;
            // }
            if (!password) {
                $.popup_show_login_error('请输入密码！')
                return false;
            }
            if (password && password.length < 6) {
                $.popup_show_login_error('密码长度不能少于6位！')
                return false;
            }

            if (popup_is_login_passCode == 'Y') {
                if ($('#J-passCodeCoin div').length == 0) {
                    $.popup_show_login_error('请选择验证码！')
                    return false;
                }
            } else {
                // 不需要验证码，直接返回校验通过
                popup_ifSuccessCode = true;
            }

            $.popup_login();
        });
    },
    // 登录
    popup_login: function() {
        if(!popup_ifSuccessCode) {
            if (popup_is_uam_login == 'Y') {
                // 统一认证登录校验验证码
                $.popup_checkPassCode()
            } else {
                // 本地登录校验验证码
                // $.popup_checkPassCode_location()
                $.popup_checkPassCode()
            }
        } else {
            if (popup_is_uam_login == 'Y') {
                // 拉取统一认证登录
                $.popup_loginForUam()
            } else {
                if (popup_is_login_passCode == 'Y') {
                    // 本地登录校验验证码
                    $.popup_checkPassCode_location()
                } else {
                    // 拉取本地登录
                    $.popup_loginForLocation()
                }
            }
        }
    },
    // 显示扫码登录
    popup_showQrCode: function () {
        $('.login-box').removeClass('login-box-account')
        $('.login-code').show();
    },
    // 隐藏扫码登录
    popup_hideQrCode: function () {
        $('.login-box').addClass('login-box-account');
        $('.login-code').hide();
    },
    // 刷新二维码
    popup_refreshQrCode: function () {
        $('.code-error .btn').unbind('click').click(function () {
            $('#J-login-code-loading').show();
            $('#J-login-code-con').hide();
            $.popup_hideQrError()
            // 生成二维码
            $.popup_createQr()
        })
    },
    // 切换登录方式
    popup_switchLoginWay: function () {

        // 扫码登录
        $('.login-hd-code a').unbind('click').click(function () {
            $('#J-login-code-loading').show();
            $('#J-login-code-con').hide();

            // 隐藏已失效二维码
            $.popup_hideQrError()

            // 隐藏扫码成功
            $('#J-login-code-success').addClass('hide');

            $.popup_hideCommonLogin()

            if (popup_t) {
                clearInterval(popup_t)
                popup_t = null
                popup_s = -1
            }

            // 生成二维码
            $.popup_createQr()

        })

        // 账号登录
        $('.login-hd-account a').unbind('click').click(function () {
            // 清空上一次选择的验证码
            $('#J-passCodeCoin').html('')

            if (popup_t) {
                clearInterval(popup_t)
                popup_t = null
                popup_s = -1
            }
            // 生成验证码
            $.popup_createPassCode()
        })
        
        // if (popup_slideIsLoad) {
        //     $('.login-box .login-hd li').each(function (i) {
        //         $(this).on('click', function () {
        //             if (i === 1) {
        //                 // 账号登录
        //                 if (popup_t) {
        //                     clearInterval(popup_t)
        //                     popup_t = null
        //                     popup_s = -1
        //                 }
        //                 popup_ispopup_CreateQr = !popup_ispopup_CreateQr
        //                 // 生成验证码
        //                 $.popup_createPassCode()
        //             } else {
        //                 // 扫码登录
        //                 if (popup_ispopup_CreateQr) {
        //                     // 扫码登录
        //                     $.popup_createQr()
        //                     popup_ispopup_CreateQr = !popup_ispopup_CreateQr
        //                 }
        //             }
        //         })
        //     })
        //     popup_slideIsLoad = !popup_slideIsLoad
        // }
    },
    // 创建统一认证验证码
    popup_createPassCode: function () {
        // var temp = new Date().getTime();
        $.ajax({
            url: getCode_url + "?" + new Date().getTime(),
            // xhrFields : {
            //     withCredentials : true
            // },
            // dataType:'jsonp',
            dataType: 'json',    // 就不用把response转json对象了
            type: 'GET',
            timeout: 10000,
            success: function(response) {
                if(response.image) {
                    popup_ifSuccessCode = false;
                    popup_passCodeImg.attr('src', 'data:image/jpg;base64,' + response.image);
                    $.popup_showLoginType(0); // show code img
                }
            },
            error: function (err) {
            }
        })
    },
    // 创建本地登录验证码
    popup_createPassCode_location: function () {
        popup_ifSuccessCode = false;
        var temp = new Date().getTime();
        popup_passCodeImg.attr('src', popup_url.getPassCodeNew + temp);
        $.popup_showLoginType(0); // show code img
    },
    // 校验验证码
    popup_checkPassCode: function () {
        var result = false;
        var randCode = '';
        var obj = $('#J-passCodeCoin div');
        
        for(var i = 0; i < obj.length; i++) {
            randCode += $(obj[i]).attr('randcode') + ',';
        }
        randCode = randCode.substring(0, randCode.length - 1);

        $.ajax({
            url: checkCode_url,
            // xhrFields: { withCredentials : true },
            // crossDomain: true,
            // dataType: 'jsonp',
            // data:{ 'answer': randCode, 'rand': 'sjrand', 'login_site': 'E' },
            data:{ 'answer': randCode},
            dataType: 'json',
            type: 'GET',
            timeout: 10000,
            success: function(response) {
               if(response.result_code == 4) {
                    // 校验通过
                    popup_ifSuccessCode = true;
                    $.popup_showLoginType(2); // code popup_validate success
                    // 登录
                    $.popup_loginForUam()
                } else {
                    // 校验失败
                    $.popup_passCodeError();
               }
            },
            error: function (err) {
            }
        });
    },
    // 本地登录校验验证码
    popup_checkPassCode_location: function () {
        var result = false;
        var randCode = '';
        var obj = $('#J-passCodeCoin div');
        
        for(var i = 0; i < obj.length; i++) {
            randCode += $(obj[i]).attr('randcode') + ',';
        }
        randCode = randCode.substring(0, randCode.length - 1);

        $.ajax({
            url: popup_url.checkRandCodeAnsyn,
            xhrFields: { withCredentials : true },
            data:{ 'randCode': randCode, 'rand': 'sjrand', 'login_site': 'E' },
            type: 'POST',
            timeout: 10000,
            success: function(response) {
                var data = response.data
                if(data && data.result == 1) {
                    // 校验通过
                    popup_ifSuccessCode = true;
                    $.popup_showLoginType(2); // code popup_validate success
                    // 登录
                    $.popup_loginForLocation_passcode()
                } else {
                    // 校验失败
                    $.popup_passCodeError_location();
                }
            },
            error: function(err) {
            }
        });
    },
    // 统一认证登录验证码校验失败
    popup_passCodeError: function () {
        $.popup_show_login_error('验证码错误！');
        $.popup_refreshPassCode(true);
    },
    // 本地登录验证码校验失败
    popup_passCodeError_location: function () {
        $.popup_show_login_error('验证码错误！');
        $.popup_refreshPassCode_location(true);
    },
    // 统一认证登录刷新验证码
    popup_refreshPassCode: function(type) {
        $.popup_hide_login_error();
        $('#J-passCodeCoin').html('');
        if(type) {
            $.popup_showLoginType(1); // show code img
            setTimeout('$.popup_createPassCode()', 1000);
        } else {
            $.popup_createPassCode();
        }
    },
    // 本地登录刷新验证码
    popup_refreshPassCode_location: function (type) {
        $.popup_hide_login_error();
        $('#J-passCodeCoin').html('');
        if(type) {
            $.popup_showLoginType(1); // show code img
            setTimeout('$.popup_createPassCode_location()', 1000);
        } else {
            $.popup_createPassCode_location();
        }
    },
    // 显示验证码
    popup_showPasscode: function () {
        // $('.login-pwd-code').show();
        $('.login-box').removeClass('login-box-account-nocode')
    },
    // 隐藏验证码
    popup_hidePasscode: function () {
        // $('.login-pwd-code').hide();
        $('.login-box').addClass('login-box-account-nocode')
    },
    popup_clearInterval: function() {
      if (popup_t) {
          clearInterval(popup_t)
          popup_t = null
          popup_s = -1
      }
    },
    getBanners: function () {
    	// 轮播图
        $.ajax({
            url: popup_url.getBanners,
            type: 'GET',
            timeout: 10000,
            dataType: 'text',
            success: function(response) {
                if (response) {
                    var data = JSON.parse(response);
                    $.each(data.data.index_banner_url, function(index, value){
                    	var thtml =  value.src ? "<a href=\"" + value.src + "\"></a>" : "<a style='cursor:auto;' href='javascript:void(0)'></a>";
                        $('.loginSlide .bd ul').append("<li style=\"background: url(" + value.url + ") center center no-repeat;\">" + thtml + "</li>");
                    })
                    $(".loginSlide").slide({ 
                        titCell:".hd ul", 
                        mainCell:".bd ul", 
                        effect:"leftLoop", 
                        vis:"auto", 
                        autoPlay:true, 
                        autoPage:true, 
                        trigger:"click" ,
                        interTime:"6000"
                      });
                }
            },
            error: function(error) {
                
            }
        });
    }
});

