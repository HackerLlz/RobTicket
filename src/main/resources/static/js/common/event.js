TouClick.ready(function () {
    var touclick_start = function (randCode_name, code_type, cssConfig, rand, targetdiv) {
        var touclick = TouClick.get("touclick-" + randCode_name).start(
            {
                // captcha pic url
                // gp_url: ctx + "passcodeNew/getPassCodeNew?module="
                // 	+ code_type + "&rand=" + rand,
                gp_url: "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=" + code_type + "&rand=" + rand,
                // click callback
                onClick: function (click_result) {
                    var $randCode = $("#" + randCode_name);
                    $randCode.val(click_result);
                    var error_msg = $("#error_msg" + targetdiv);
                    var randCodeDom = $randCode[0];
                    if (error_msg.data("tag") === 1) {
                        error_msg.hide();
                        //showError(randCodeDom, window["TouLocal"].getErrorMessage(randCodeDom,0,1), 1);
                    }
                    TouLocal.clickCallback(randCodeDom);
                },
                // reload callback
                onReload: function () {
                    $("#" + randCode_name).val("");
                    $("#error_msg").css('display', 'none');
                    var currentCookie = $.cookie("current_captcha_type")
                    if (currentCookie && currentCookie !== "Z") {
                        location.reload(true);
                    } else {
                        //console.log('cookie is correct');
                    }
                },
                onReloading: function () {
                    return true;
                }
            });
        if (typeof (cssConfig.style) === "object") {
            touclick.setCss(cssConfig.style);
        }
        if (typeof (cssConfig.arrow) === "object") {
            touclick.setArrow(cssConfig.arrow.direction,
                cssConfig.arrow.offset);
        }
        return touclick;
    };
    for (var i = 0; i < targetelement.length; i++) {
        if (!TouLocal.checkZByTargeElement(targetelement[i])) continue;
        var randCode_name = "randCode";
        if (targetelement[i] !== "") {
            randCode_name += "_" + targetelement[i];
        }
        var $targetdiv = $('#' + targetdiv[i]), code_type = $targetdiv.data("code_type"),
            touclick_type = $targetdiv.data("touclick-type");
        var page_identity = $targetdiv.data('page-identity');
        page_identity = page_identity ? page_identity : '';
        var rand = "sjrand";
        if (code_type == "passenger") {
            rand = "randp";
        }
        var touclickCssName = code_type + "_" + targetelement[i] + page_identity;
        var cssConfig = TouLocal["localCssConfig"][touclickCssName][touclick_type];
        var touclickObject = touclick_start(randCode_name, code_type, cssConfig, rand, targetdiv[i]);
        if (typeof (cssConfig.hook) === 'function' && window["isTouclickCssConfigHook"] !== true) {
            //window["isTouclickCssConfigHook"] = true;
            if (typeof (cssConfig["hookName"]) === 'string') {
                window[cssConfig["hookName"]] = cssConfig.hook(touclickObject, $targetdiv);
            } else {
                window.touclickHook = cssConfig.hook(touclickObject, $targetdiv);
            }

        }
    }

    $('.captchaFloatButton').each(function (i, v) {

        var that = $(this), isbut = false, istou = false, timeoutHandle, timeout = false,
            touclick_name = that.data("touclick-name");
        if (!TouLocal.checkZByTargeElement(touclick_name)) {
            return;
        }

        var touclick = TouClick.get("touclick-" + TouLocal.getRandCodeName(touclick_name));
        var $touclick = $(touclick.getDom());


        var enterFun = function (evt) {

            var _this = $(this);
            if (_this.hasClass("touclick")) {
                istou = true;
            } else if ($(evt.target).hasClass("captchaFloatTargetButton")) {
                isbut = true;
            } else {
                return;
            }
            timeout = true;
            touclick.show();
            TouLocal.showCallback(touclick_name);
        };
        var leaveFun = function () {
            $(this).hasClass("touclick") ? istou = false : isbut = false;
            if (!istou && !isbut) {
                timeout = false;
                if (timeoutHandle) {
                    clearTimeout(timeoutHandle);
                    timeoutHandle = 0;
                }
                timeoutHandle = setTimeout(function () {
                    if (timeout === false) {
                        touclick.hidden();
                        //TouLocal.hiddenCallback(touclick_name);
                    }
                }, 700);
            }
        };

        if (TouLocal.isTouch) {
            that.click(function (evt) {
                var _this = $(this);
                if (!$(evt.target).hasClass("captchaFloatTargetButton")) {
                    return;
                }
                TouLocal.showCallback(touclick_name);
                if (!timeout) {
                    timeout = true;
                    touclick.show();
                    evt.stopPropagation();
                }
            });
            $(document).click(function (evt) {
                if (timeout) {
                    touclick.hidden();
                    timeout = false;
                }
            });
        } else {
            that.click(enterFun);
            that.children(".captchaFloatTargetButton").mouseleave(leaveFun);
            $touclick.mouseenter(enterFun).mouseleave(leaveFun);
        }

        $('#qr_submit').text(login_messages["submitAfterVerify"]);//.attr("title",login_messages["getCaptchaByClick"]);
        var leftTicketOrderNote = $('#leftTicketOrderNote');
        leftTicketOrderNote.text(login_messages["leftTicketOrderNoteMessage"]).css({
            "fontSize": "14px",
            "display": "inline-block",
            "position": "absolute",
            "width": "220px",
            "fontWeight": "bold"
        });
        leftTicketOrderNote.parent().css({"height": "25px"});
        if (leftTicketOrderNote[0]) {
            $("#error_msgmypasscode2").css({"position": "absolute"});
        }
        var captchaButton = $touclick.parent();
        captchaButton.append("<a href='../gonggao/yzmsysm.html' target='_blank' style='display:inline-block;margin-left:0px;font-size:12px;height:20px;'>验证码如何使用？</a>");
    });
});